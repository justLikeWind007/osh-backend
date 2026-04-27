package com.backstage.common.aspect;

import com.backstage.common.annotation.DistributeLock;
import com.backstage.common.constant.DistributeLockConstant;
import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.exception.DistributeLockException;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁切面
 *
 * @author Hope_Lau
 */
@Aspect
@Component
public class DistributeLockAspect {

    private RedissonClient redissonClient;

    public DistributeLockAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    private static final Logger LOG = LoggerFactory.getLogger(DistributeLockAspect.class);

    @Around("@annotation(com.backstage.common.annotation.DistributeLock)")
    public Object process(ProceedingJoinPoint pjp) throws Exception {
        Object response = null;
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        DistributeLock distributeLock = method.getAnnotation(DistributeLock.class);

        String key = distributeLock.key();
        if (DistributeLockConstant.NONE_KEY.equals(key)) {
            if (DistributeLockConstant.NONE_KEY.equals(distributeLock.keyExpression())) {
                throw new DistributeLockException("no lock key found...");
            }
            SpelExpressionParser parser = new SpelExpressionParser();
            Expression expression = parser.parseExpression(distributeLock.keyExpression());

            EvaluationContext context = new StandardEvaluationContext();
            // 获取参数值
            Object[] args = pjp.getArgs();

            // 获取运行时参数的名称
            StandardReflectionParameterNameDiscoverer discoverer
                    = new StandardReflectionParameterNameDiscoverer();
            String[] parameterNames = discoverer.getParameterNames(method);

            // 将参数绑定到context中
            if (parameterNames != null) {
                for (int i = 0; i < parameterNames.length; i++) {
                    context.setVariable(parameterNames[i], args[i]);
                }
            }

            // 解析表达式，获取结果
            key = String.valueOf(expression.getValue(context));
        }

        String scene = distributeLock.scene();

        Long userId = ThreadLocalUtil.get(OshUserConstants.USER_ID, Long.class);
        String lockKey = scene + "#" + key;
        if (distributeLock.includeUserId() && userId != null) {
            lockKey = scene + "#user:" + userId + "#" + key;
        }

        int expireTime = distributeLock.expireTime();
        int waitTime = distributeLock.waitTime();
        boolean releaseImmediately = distributeLock.releaseImmediately();
        if (!releaseImmediately && expireTime == DistributeLockConstant.DEFAULT_EXPIRE_TIME) {
            throw new DistributeLockException("expireTime must be configured when releaseImmediately is false");
        }
        RLock rLock = redissonClient.getLock(lockKey);
        boolean lockResult = false;
        if (waitTime == DistributeLockConstant.DEFAULT_WAIT_TIME) {
            if (expireTime == DistributeLockConstant.DEFAULT_EXPIRE_TIME) {
                LOG.info("lock for key : {}", lockKey);
                rLock.lock();
            } else {
                LOG.info("lock for key : {}, expire : {}", lockKey, expireTime);
                rLock.lock(expireTime, TimeUnit.MILLISECONDS);
            }
            lockResult = true;
        } else {
            if (expireTime == DistributeLockConstant.DEFAULT_EXPIRE_TIME) {
                LOG.info("try lock for key : {}, wait : {}", lockKey, waitTime);
                lockResult = rLock.tryLock(waitTime, TimeUnit.MILLISECONDS);
            } else {
                LOG.info("try lock for key : {} , expire : {} , wait : {}", lockKey, expireTime, waitTime);
                lockResult = rLock.tryLock(waitTime, expireTime, TimeUnit.MILLISECONDS);
            }
        }

        if (!lockResult) {
            LOG.warn("lock failed for key : {} , expire : {}", lockKey, expireTime);
            throw new DistributeLockException("acquire lock failed... key : " + lockKey);
        }

        try {
            LOG.info("lock success for key : {} , expire : {}", lockKey, expireTime);
            response = pjp.proceed();
        } catch (Throwable e) {
            throw new Exception(e);
        } finally {
            if (releaseImmediately && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                LOG.info("unlock for key : {} , expire : {}", lockKey, expireTime);
            }
        }
        return response;
    }
}
