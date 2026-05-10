package com.backstage.system.service.assistant.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.backstage.system.domain.assistant.AssistantFeedbackProcessRecord;
import com.backstage.system.domain.assistant.AssistantTicketStatus;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackProcessRecordVO;
import com.backstage.system.mapper.assistant.AssistantFeedbackProcessRecordMapper;
import com.backstage.system.service.assistant.IAssistantFeedbackProcessRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 反馈处理记录 Service 实现
 *
 * @author backstage
 */
@Service
public class AssistantFeedbackProcessRecordServiceImpl
        extends ServiceImpl<AssistantFeedbackProcessRecordMapper, AssistantFeedbackProcessRecord>
        implements IAssistantFeedbackProcessRecordService {

    @Override
    public void createRecord(Long feedbackId, String fromStatus, String toStatus, Long operatorId, String operatorName, String remark) {
        AssistantFeedbackProcessRecord record = new AssistantFeedbackProcessRecord();
        record.setFeedbackId(feedbackId);
        record.setFromStatus(AssistantTicketStatus.normalize(fromStatus));
        record.setToStatus(AssistantTicketStatus.normalize(toStatus));
        record.setOperatorId(operatorId);
        record.setOperatorName(StrUtil.blankToDefault(StrUtil.trim(operatorName), "匿名用户"));
        record.setRemark(StrUtil.blankToDefault(StrUtil.trim(remark), ""));
        record.setCreateBy(operatorId);
        record.setUpdateBy(operatorId);
        record.setDeleteFlag(0);
        save(record);
    }

    @Override
    public List<AssistantFeedbackProcessRecordVO> listByFeedbackId(Long feedbackId) {
        return lambdaQuery()
                .eq(AssistantFeedbackProcessRecord::getFeedbackId, feedbackId)
                .orderByAsc(AssistantFeedbackProcessRecord::getCreateTime)
                .list()
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    private AssistantFeedbackProcessRecordVO toVO(AssistantFeedbackProcessRecord record) {
        AssistantFeedbackProcessRecordVO vo = BeanUtil.copyProperties(record, AssistantFeedbackProcessRecordVO.class);
        vo.setFromStatusText(AssistantTicketStatus.getDescriptionByCode(record.getFromStatus()));
        vo.setToStatusText(AssistantTicketStatus.getDescriptionByCode(record.getToStatus()));
        return vo;
    }
}
