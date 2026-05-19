package com.backstage.system.task;

import com.backstage.system.service.assistant.IAssistantFeedbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 待确认工单自动处理任务。
 */
//@Component
// todo 迁到 XXL-Job
public class FeedbackPendingConfirmTask {

    private static final Logger log = LoggerFactory.getLogger(FeedbackPendingConfirmTask.class);

    private final IAssistantFeedbackService assistantFeedbackService;

    public FeedbackPendingConfirmTask(IAssistantFeedbackService assistantFeedbackService) {
        this.assistantFeedbackService = assistantFeedbackService;
    }

    /**
     * 每 30 分钟扫描一次待确认工单：
     * 1. 发送 3 天 / 6 天提醒
     * 2. 达到自动确认窗口后自动转为已解决
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void processPendingConfirmTickets() {
        log.info("开始处理待确认工单提醒与自动确认任务");
        assistantFeedbackService.processPendingConfirmTickets();
        log.info("完成待确认工单提醒与自动确认任务");
    }
}
