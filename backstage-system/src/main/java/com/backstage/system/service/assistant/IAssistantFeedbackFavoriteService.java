package com.backstage.system.service.assistant;

import com.backstage.common.core.domain.AjaxResult;

/**
 * 反馈收藏服务接口
 *
 * @author backstage
 */
public interface IAssistantFeedbackFavoriteService {

    /**
     * 收藏
     *
     * @param feedbackId 反馈ID
     * @param userId 用户ID
     * @return 操作结果
     */
    AjaxResult favorite(Long feedbackId, Long userId);

    /**
     * 取消收藏
     *
     * @param feedbackId 反馈ID
     * @param userId 用户ID
     * @return 操作结果
     */
    AjaxResult unfavorite(Long feedbackId, Long userId);

    /**
     * 查询用户是否已收藏
     *
     * @param feedbackId 反馈ID
     * @param userId 用户ID
     * @return 是否已收藏
     */
    boolean isFavorited(Long feedbackId, Long userId);
}
