package com.backstage.framework.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 业务线程池配置。
 */
@ConfigurationProperties(prefix = "backstage.async")
public class AsyncThreadPoolProperties
{
    private ExecutorProperties common = new ExecutorProperties();

    private ExecutorProperties aggregation = new ExecutorProperties();

    private ExecutorProperties notification = new ExecutorProperties();

    private int scheduledCorePoolSize = 16;

    public AsyncThreadPoolProperties()
    {
        common.setCorePoolSize(16);
        common.setMaxPoolSize(64);
        common.setQueueCapacity(500);
        common.setKeepAliveSeconds(120);
        common.setAwaitTerminationSeconds(30);
        common.setThreadNamePrefix("common-async-");

        aggregation.setCorePoolSize(8);
        aggregation.setMaxPoolSize(24);
        aggregation.setQueueCapacity(200);
        aggregation.setKeepAliveSeconds(60);
        aggregation.setAwaitTerminationSeconds(20);
        aggregation.setThreadNamePrefix("aggregation-async-");

        notification.setCorePoolSize(4);
        notification.setMaxPoolSize(16);
        notification.setQueueCapacity(300);
        notification.setKeepAliveSeconds(120);
        notification.setAwaitTerminationSeconds(30);
        notification.setThreadNamePrefix("notification-async-");
    }

    public ExecutorProperties getCommon()
    {
        return common;
    }

    public void setCommon(ExecutorProperties common)
    {
        this.common = common;
    }

    public ExecutorProperties getAggregation()
    {
        return aggregation;
    }

    public void setAggregation(ExecutorProperties aggregation)
    {
        this.aggregation = aggregation;
    }

    public ExecutorProperties getNotification()
    {
        return notification;
    }

    public void setNotification(ExecutorProperties notification)
    {
        this.notification = notification;
    }

    public int getScheduledCorePoolSize()
    {
        return scheduledCorePoolSize;
    }

    public void setScheduledCorePoolSize(int scheduledCorePoolSize)
    {
        this.scheduledCorePoolSize = scheduledCorePoolSize;
    }

    public static class ExecutorProperties
    {
        private int corePoolSize = 8;

        private int maxPoolSize = 16;

        private int queueCapacity = 200;

        private int keepAliveSeconds = 60;

        private int awaitTerminationSeconds = 20;

        private String threadNamePrefix = "async-";

        public int getCorePoolSize()
        {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize)
        {
            this.corePoolSize = corePoolSize;
        }

        public int getMaxPoolSize()
        {
            return maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize)
        {
            this.maxPoolSize = maxPoolSize;
        }

        public int getQueueCapacity()
        {
            return queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity)
        {
            this.queueCapacity = queueCapacity;
        }

        public int getKeepAliveSeconds()
        {
            return keepAliveSeconds;
        }

        public void setKeepAliveSeconds(int keepAliveSeconds)
        {
            this.keepAliveSeconds = keepAliveSeconds;
        }

        public int getAwaitTerminationSeconds()
        {
            return awaitTerminationSeconds;
        }

        public void setAwaitTerminationSeconds(int awaitTerminationSeconds)
        {
            this.awaitTerminationSeconds = awaitTerminationSeconds;
        }

        public String getThreadNamePrefix()
        {
            return threadNamePrefix;
        }

        public void setThreadNamePrefix(String threadNamePrefix)
        {
            this.threadNamePrefix = threadNamePrefix;
        }
    }
}
