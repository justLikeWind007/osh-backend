package com.backstage.system.service.assistant;

import com.backstage.system.domain.assistant.AssistantFeedbackComment;
import com.backstage.system.domain.assistant.dto.AssistantFeedbackCommentDTO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackCommentVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * AI 助手反馈评论 Service 接口
 *
 * @author backstage
 */
public interface IAssistantFeedbackCommentService extends IService<AssistantFeedbackComment> {

    /**
     * 创建评论
     *
     * @param dto    评论创建 DTO
     * @param userId 当前用户 ID
     * @return 评论 ID
     */
    Long createComment(AssistantFeedbackCommentDTO dto, Long userId);

    /**
     * 获取反馈的评论列表（2级结构）
     *
     * @param feedbackId 反馈 ID
     * @param pageNum    页码
     * @param pageSize   每页数量
     * @return 评论列表
     */
    List<AssistantFeedbackCommentVO> listCommentsByFeedbackId(Long feedbackId, Integer pageNum, Integer pageSize);

    /**
     * 获取反馈的评论总数
     *
     * @param feedbackId 反馈 ID
     * @return 评论总数
     */
    Long countByFeedbackId(Long feedbackId);

    /**
     * 删除评论（逻辑删除）
     *
     * @param commentId 评论 ID
     * @param userId    当前用户 ID
     * @return 是否成功
     */
    boolean deleteComment(Long commentId, Long userId);
}
