package com.backstage.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xuanqing
 * @create 2026-04-17 22:39
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributeLock {
    /**
     * 业务场景名，用于区分不同业务的锁
     * 例如：order:pay、coupon:receive、course:create
     */
    String scene();
    /**
     * 固定字符串 key
     * 适合全局锁、粒度较粗的互斥控制
     * key 和 keyExpression 二选一，keyExpression 优先
     */
    String key() default "";
    /**
     * SpEL 表达式动态生成 key
     * 例如：#orderId、#dto.courseId
     * 适合按业务对象唯一标识加锁
     */
    String keyExpression() default "";
    /**
     * 是否把当前用户 ID 拼进锁 key
     * true  → 用户维度锁，不同用户互不影响
     * false → 资源维度锁 / 全局锁
     */
    boolean includeUserId() default true;
    /**
     * 锁过期时间，单位毫秒
     * -1 → Redisson watchdog 自动续期（推荐）
     * >0 → 显式指定租约时间
     */
    long expireTime() default -1;
    /**
     * 等待获取锁的时间，单位毫秒
     * -1 → 阻塞等待直到拿到锁
     *  0 → 立即尝试，拿不到直接失败（接口请求推荐）
     * >0 → 等待指定时间，超时失败
     */
    long waitTime() default 0;
}
