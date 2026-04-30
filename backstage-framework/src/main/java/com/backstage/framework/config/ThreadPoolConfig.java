package com.backstage.framework.config;

import com.backstage.common.utils.Threads;
import com.backstage.common.async.AsyncExecutorNames;
import com.backstage.framework.config.properties.AsyncThreadPoolProperties;
import com.backstage.framework.config.properties.AsyncThreadPoolProperties.ExecutorProperties;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 *
 * @author ruoyi
 **/
@Configuration
@EnableConfigurationProperties(AsyncThreadPoolProperties.class)
public class ThreadPoolConfig
{
    @Bean(name = AsyncExecutorNames.DEFAULT)
    @Primary
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(AsyncThreadPoolProperties properties, TaskDecorator taskDecorator)
    {
        return buildExecutor(properties.getCommon(), taskDecorator);
    }

    @Bean(name = AsyncExecutorNames.AGGREGATION)
    public ThreadPoolTaskExecutor aggregationTaskExecutor(AsyncThreadPoolProperties properties, TaskDecorator taskDecorator)
    {
        return buildExecutor(properties.getAggregation(), taskDecorator);
    }

    @Bean(name = AsyncExecutorNames.NOTIFICATION)
    public ThreadPoolTaskExecutor notificationTaskExecutor(AsyncThreadPoolProperties properties, TaskDecorator taskDecorator)
    {
        return buildExecutor(properties.getNotification(), taskDecorator);
    }

    /**
     * 执行周期性或定时任务
     */
    @Bean(name = "scheduledExecutorService")
    protected ScheduledExecutorService scheduledExecutorService(AsyncThreadPoolProperties properties)
    {
        return new ScheduledThreadPoolExecutor(properties.getScheduledCorePoolSize(),
                new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(true).build(),
                new ThreadPoolExecutor.CallerRunsPolicy())
        {
            @Override
            protected void afterExecute(Runnable r, Throwable t)
            {
                super.afterExecute(r, t);
                Threads.printException(r, t);
            }
        };
    }

    private ThreadPoolTaskExecutor buildExecutor(ExecutorProperties properties, TaskDecorator taskDecorator)
    {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(properties.getThreadNamePrefix());
        executor.setCorePoolSize(properties.getCorePoolSize());
        executor.setMaxPoolSize(properties.getMaxPoolSize());
        executor.setQueueCapacity(properties.getQueueCapacity());
        executor.setKeepAliveSeconds(properties.getKeepAliveSeconds());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(properties.getAwaitTerminationSeconds());
        executor.setTaskDecorator(taskDecorator);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
