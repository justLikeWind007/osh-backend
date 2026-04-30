package com.backstage.common.async;

import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * 异步任务辅助类，统一封装 CompletableFuture 提交与异常传播。
 */
@Component
public class AsyncTaskSupport
{
    public <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier, Executor executor)
    {
        return CompletableFuture.supplyAsync(supplier, executor);
    }

    public CompletableFuture<Void> runAsync(Runnable runnable, Executor executor)
    {
        return CompletableFuture.runAsync(runnable, executor);
    }

    public void awaitAll(CompletableFuture<?>... futures)
    {
        CompletableFuture.allOf(futures).join();
    }

    public <T> CompletableFuture<T> completedFuture(T value)
    {
        return CompletableFuture.completedFuture(value);
    }

    public <T> T join(CompletableFuture<T> future)
    {
        try
        {
            return future.join();
        }
        catch (CompletionException ex)
        {
            Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException)
            {
                throw (RuntimeException) cause;
            }
            throw ex;
        }
    }
}
