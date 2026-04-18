package com.backstage.common.aspect;

import com.backstage.common.annotation.DistributeLock;
import com.backstage.common.exception.redisson.DistributeLockException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static com.backstage.common.threadlocal.ThreadLocalUtil.getCurrentUserId;

/**
 * @author xuanqing
 * @create 2026-04-17 22:53
 */
public class DistributeLockAspect {
  @Resource
  private RedissonClient redissonClient;
    // SpEL 解析器，复用同一个实例
    private final SpelExpressionParser spelParser = new SpelExpressionParser();
    // 用于获取方法参数名（需要编译时保留参数名，或用 -parameters 编译选项）
    private final LocalVariableTableParameterNameDiscoverer nameDiscoverer =
            new LocalVariableTableParameterNameDiscoverer();
  /**
   * 拦截所有带 @DistributeLock 注解的方法
   */
  @Around("@annotation(distributeLock)")
  public Object around(ProceedingJoinPoint joinPoint, DistributeLock distributeLock) throws Throwable {

    // 1. 生成最终锁 key
    String lockKey = buildLockKey(joinPoint, distributeLock);

    // 2. 从 Redisson 获取锁对象（此时还没加锁）
    RLock lock = redissonClient.getLock(lockKey);

    boolean locked = false;
    try {
      // 3. 尝试加锁
      locked = tryAcquireLock(lock, distributeLock);

      // 4. 加锁失败，抛出异常
      if (!locked) {
        throw new DistributeLockException("操作频繁，请稍后再试");
      }

      // 5. 加锁成功，执行业务方法
      return joinPoint.proceed();

    } finally {
      // 6. 释放锁（只释放自己持有的锁）
      if (locked && lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }
  }
  /**
   * 构建最终锁 key
   * 格式1：scene#key
   * 格式2：scene#user:userId#key
   */
  private String buildLockKey(ProceedingJoinPoint joinPoint, DistributeLock distributeLock) {
    String scene = distributeLock.scene();

    // 解析 key：keyExpression 优先，其次用 key
    String resolvedKey;
    if (!distributeLock.keyExpression().isEmpty()) {
      resolvedKey = resolveSpEL(joinPoint, distributeLock.keyExpression());
    } else {
      resolvedKey = distributeLock.key();
    }

    // 拼接用户 ID
    if (distributeLock.includeUserId()) {
      Long userId = getCurrentUserId();
      return scene + "#user:" + userId + "#" + resolvedKey;
    } else {
      return scene + "#" + resolvedKey;
    }
  }

  /**
   * 解析 SpEL 表达式
   * 例如：#orderId → 方法参数 orderId 的实际值
   */
  private String resolveSpEL(ProceedingJoinPoint joinPoint, String expression) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    // 获取方法参数名数组，例如 ["orderId", "userId"]
    String[] paramNames = nameDiscoverer.getParameterNames(method);

    // 获取方法参数值数组，例如 [12345L, 888L]
    Object[] paramValues = joinPoint.getArgs();

    // 构建 SpEL 上下文，把参数名和值绑定进去
    EvaluationContext context = new StandardEvaluationContext();
    if (paramNames != null) {
      for (int i = 0; i < paramNames.length; i++) {
        context.setVariable(paramNames[i], paramValues[i]);
      }
    }

    // 解析表达式，返回字符串结果
    Object value = spelParser.parseExpression(expression).getValue(context);
    return value == null ? "null" : value.toString();
  }

  /**
   * 尝试加锁
   * 根据 waitTime 和 expireTime 决定加锁策略
   */
  private boolean tryAcquireLock(RLock lock, DistributeLock distributeLock) throws InterruptedException {
    long waitTime = distributeLock.waitTime();
    long expireTime = distributeLock.expireTime();

    if (waitTime == -1) {
      // 阻塞等待，直到拿到锁
      // expireTime=-1 时走 watchdog 自动续期
      if (expireTime == -1) {
        lock.lock();
      } else {
        lock.lock(expireTime, TimeUnit.MILLISECONDS);
      }
      return true;
    } else {
      // waitTime=0：立即尝试
      // waitTime>0：等待指定时间
      if (expireTime == -1) {
        // leaseTime=-1 → watchdog 自动续期
        return lock.tryLock(waitTime, -1, TimeUnit.MILLISECONDS);
      } else {
        return lock.tryLock(waitTime, expireTime, TimeUnit.MILLISECONDS);
      }
    }
  }

}
