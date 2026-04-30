package com.backstage.common.async;

/**
 * 业务线程池名称常量
 */
public final class AsyncExecutorNames
{
    private AsyncExecutorNames()
    {
    }

    /**
     * 默认通用线程池，兼容历史注入点
     */
    public static final String DEFAULT = "threadPoolTaskExecutor";

    /**
     * 聚合查询线程池，适合详情页/首页等多路并行查询
     */
    public static final String AGGREGATION = "aggregationTaskExecutor";

    /**
     * 通知线程池，适合邮件、站内信、消息推送
     */
    public static final String NOTIFICATION = "notificationTaskExecutor";
}
