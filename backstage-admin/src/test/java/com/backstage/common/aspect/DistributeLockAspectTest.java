package com.backstage.common.aspect;

import com.backstage.common.annotation.DistributeLock;
import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.exception.DistributeLockException;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.After;
import org.junit.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DistributeLockAspectTest {

    @After
    public void tearDown() {
        ThreadLocalUtil.remove();
    }

    @Test
    public void shouldUnlockImmediatelyWhenReleaseImmediatelyIsTrue() throws Throwable {
        ProceedingJoinPoint pjp = mockProceedingJoinPoint("releaseImmediatelyMethod");
        RedissonClient redissonClient = mock(RedissonClient.class);
        RLock lock = mock(RLock.class);
        when(redissonClient.getLock("course#user:1#save")).thenReturn(lock);
        when(lock.isHeldByCurrentThread()).thenReturn(true);

        DistributeLockAspect aspect = new DistributeLockAspect(redissonClient);
        Object result = aspect.process(pjp);

        assertEquals("ok", result);
        verify(lock).lock(60000, TimeUnit.MILLISECONDS);
        verify(lock).unlock();
    }

    @Test
    public void shouldKeepLockUntilExpireWhenReleaseImmediatelyIsFalse() throws Throwable {
        ProceedingJoinPoint pjp = mockProceedingJoinPoint("holdUntilExpireMethod");
        RedissonClient redissonClient = mock(RedissonClient.class);
        RLock lock = mock(RLock.class);
        when(redissonClient.getLock("course#user:1#audit")).thenReturn(lock);

        DistributeLockAspect aspect = new DistributeLockAspect(redissonClient);
        Object result = aspect.process(pjp);

        assertEquals("ok", result);
        verify(lock).lock(60000, TimeUnit.MILLISECONDS);
        verify(lock, never()).unlock();
    }

    @Test(expected = DistributeLockException.class)
    public void shouldRequireExpireTimeWhenReleaseImmediatelyIsFalse() throws Throwable {
        ProceedingJoinPoint pjp = mockProceedingJoinPoint("invalidHoldMethod");
        RedissonClient redissonClient = mock(RedissonClient.class);

        DistributeLockAspect aspect = new DistributeLockAspect(redissonClient);
        aspect.process(pjp);
    }

    private ProceedingJoinPoint mockProceedingJoinPoint(String methodName) throws Throwable {
        ThreadLocalUtil.set(OshUserConstants.USER_ID, 1L);

        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        Method method = LockFacade.class.getMethod(methodName);

        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);
        when(pjp.getArgs()).thenReturn(new Object[0]);
        when(pjp.proceed()).thenReturn("ok");
        return pjp;
    }

    public static class LockFacade {

        @DistributeLock(scene = "course", key = "save", expireTime = 60000, waitTime = 0)
        public void releaseImmediatelyMethod() {
        }

        @DistributeLock(scene = "course", key = "audit", expireTime = 60000, waitTime = 0, releaseImmediately = false)
        public void holdUntilExpireMethod() {
        }

        @DistributeLock(scene = "course", key = "invalid", releaseImmediately = false)
        public void invalidHoldMethod() {
        }
    }
}
