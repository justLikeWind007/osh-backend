package com.backstage.common.annotation;

import com.backstage.common.constant.DistributeLockConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式锁注解
 *
 * @author Hope_Lau
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributeLock {

    /**
     * 锁的场景
     *
     * @return
     */
    public String scene() default DistributeLockConstant.BLANK;

    /**
     * 加锁的key，优先取key()，如果没有，则取keyExpression()
     *
     * @return
     */
    public String key() default DistributeLockConstant.NONE_KEY;

    /**
     * SPEL表达式:
     * <pre>
     *     #id
     *     #insertResult.id
     * </pre>
     *
     * @return
     */
    public String keyExpression() default DistributeLockConstant.NONE_KEY;

    /**
     * 是否在锁 key 中拼接当前用户 ID。
     *
     * @return
     */
    public boolean includeUserId() default true;

    /**
     * 超时时间，毫秒
     * 默认情况下不设置超时时间，会自动续期
     *
     * @return
     */
    public int expireTime() default DistributeLockConstant.DEFAULT_EXPIRE_TIME;

    /**
     * 加锁等待时长，毫秒
     * 默认值情况下,阻塞等待
     * @return
     */
    public int waitTime() default DistributeLockConstant.DEFAULT_WAIT_TIME;

    /**
     * 是否在方法执行结束后立即释放锁。
     * 为 false 时，要求显式配置 expireTime，锁将保留到过期时间结束。
     *
     * @return
     */
    public boolean releaseImmediately() default true;
}
