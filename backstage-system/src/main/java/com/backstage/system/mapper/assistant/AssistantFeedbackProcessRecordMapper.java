package com.backstage.system.mapper.assistant;

import com.backstage.system.domain.assistant.AssistantFeedbackProcessRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 反馈处理记录 Mapper
 *
 * @author backstage
 */
@Mapper
public interface AssistantFeedbackProcessRecordMapper extends BaseMapper<AssistantFeedbackProcessRecord> {
}
