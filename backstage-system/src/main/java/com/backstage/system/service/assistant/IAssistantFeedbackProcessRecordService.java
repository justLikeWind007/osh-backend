package com.backstage.system.service.assistant;

import com.backstage.system.domain.assistant.vo.AssistantFeedbackProcessRecordVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.backstage.system.domain.assistant.AssistantFeedbackProcessRecord;

import java.util.List;

/**
 * 反馈处理记录 Service 接口
 *
 * @author backstage
 */
public interface IAssistantFeedbackProcessRecordService extends IService<AssistantFeedbackProcessRecord> {

    /**
     * 新增处理记录。
     *
     * @param feedbackId    反馈 ID
     * @param fromStatus    变更前状态
     * @param toStatus      变更后状态
     * @param operatorId    操作人 ID
     * @param operatorName  操作人名称
     * @param remark        处理说明
     */
    void createRecord(Long feedbackId, String fromStatus, String toStatus, Long operatorId, String operatorName, String remark);

    /**
     * 查询反馈处理记录时间线。
     *
     * @param feedbackId 反馈 ID
     * @return 处理记录列表
     */
    List<AssistantFeedbackProcessRecordVO> listByFeedbackId(Long feedbackId);
}
