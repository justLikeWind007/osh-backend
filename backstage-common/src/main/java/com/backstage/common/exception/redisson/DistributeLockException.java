package com.backstage.common.exception.redisson;

/**
 * @author xuanqing
 * @create 2026-04-18 09:07
 */
/**
 * 分布式锁加锁失败异常
 * 当 waitTime=0 且锁已被占用时抛出
 */
public class DistributeLockException extends RuntimeException{
    public DistributeLockException(String message) {
        super(message);
    }
}
