package com.backstage.system.service.assistant;

import com.backstage.common.core.page.TableDataInfo;
import com.backstage.system.domain.assistant.dto.AssistantFeedbackCreateDTO;
import com.backstage.system.domain.assistant.dto.AssistantFeedbackPageDTO;
import com.backstage.system.domain.assistant.dto.AssistantTicketQueryDTO;
import com.backstage.system.domain.assistant.dto.AssistantTicketStatusUpdateDTO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackDetailVO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackListVO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackProcessRecordVO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.backstage.system.domain.assistant.AssistantFeedback;

import java.util.List;

/**
 * AI 助手反馈服务接口
 *
 * @author backstage
 */
public interface IAssistantFeedbackService extends IService<AssistantFeedback> {

    /**
     * 创建反馈工单
     *
     * @param userId 用户 ID
     * @param dto    反馈创建 DTO
     * @return 创建的反馈工单
     */
    AssistantFeedbackVO createFeedback(Long userId, AssistantFeedbackCreateDTO dto);

    /**
     * 获取我的反馈列表（分页）
     *
     * @param userId 用户 ID
     * @return 分页结果
     */
    TableDataInfo getMyFeedback(Long userId);

    /**
     * 查询工单列表（分页）
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    TableDataInfo listTickets(AssistantTicketQueryDTO queryDTO);

    /**
     * 更新工单状态
     *
     * @param ticketId  工单 ID
     * @param handlerId 处理人 ID
     * @param dto       状态更新 DTO
     * @return 更新后的工单信息
     */
    AssistantFeedbackVO updateTicketStatus(Long ticketId, Long handlerId, AssistantTicketStatusUpdateDTO dto);

    /**
     * 分页查询反馈列表（公开接口）
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    TableDataInfo pageFeedback(AssistantFeedbackPageDTO dto);

    /**
     * 前台反馈列表（轻量字段）
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    TableDataInfo pageFeedbackList(AssistantFeedbackPageDTO dto);

    /**
     * 获取反馈详情
     *
     * @param feedbackId 反馈 ID
     * @return 反馈详情
     */
    AssistantFeedbackDetailVO getFeedbackDetail(Long feedbackId);

    /**
     * 查询反馈处理记录。
     *
     * @param feedbackId 反馈 ID
     * @return 处理记录时间线
     */
    List<AssistantFeedbackProcessRecordVO> listFeedbackProcessRecords(Long feedbackId);

    /**
     * 置顶反馈（最多 3 个）
     *
     * @param feedbackId 反馈 ID
     * @param pinOrder   置顶排序（1-3）
     * @return 是否成功
     */
    boolean pinFeedback(Long feedbackId, Integer pinOrder);

    /**
     * 取消置顶
     *
     * @param feedbackId 反馈 ID
     * @return 是否成功
     */
    boolean unpinFeedback(Long feedbackId);

    /**
     * 增加评论数量
     *
     * @param feedbackId 反馈 ID
     */
    void incrementCommentCount(Long feedbackId);

    /**
     * 减少评论数量
     *
     * @param feedbackId 反馈 ID
     */
    void decrementCommentCount(Long feedbackId);

    /**
     * 增加浏览次数
     *
     * @param feedbackId 反馈 ID
     */
    void incrementViewCount(Long feedbackId);

    /**
     * 逻辑删除反馈
     *
     * @param feedbackId 反馈 ID
     * @return 是否成功
     */
    boolean deleteFeedback(Long feedbackId);
}
