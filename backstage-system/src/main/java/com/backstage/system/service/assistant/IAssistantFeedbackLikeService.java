package com.backstage.system.service.assistant;

import com.backstage.common.core.domain.AjaxResult;

/**
 * 反馈点赞服务接口
 *
 * @author backstage
 */
public interface IAssistantFeedbackLikeService {

    /**
     * 点赞
     *
     * @param feedbackId 反馈ID
     * @param userId 用户ID
     * @return 操作结果
     */
    AjaxResult like(Long feedbackId, Long userId);

    /**
     * 取消点赞
     *
     * @param feedbackId 反馈ID
     * @param userId 用户ID
     * @return 操作结果
     */
    AjaxResult unlike(Long feedbackId, Long userId);

    /**
     * 查询用户是否已点赞
     *
     * @param feedbackId 反馈ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    boolean isLiked(Long feedbackId, Long userId);
}
