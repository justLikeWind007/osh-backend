package com.backstage.system.mapper.assistant;

import com.backstage.system.domain.assistant.AssistantFeedback;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 助手反馈 Mapper 接口
 *
 * @author backstage
 */
@Mapper
public interface AssistantFeedbackMapper extends BaseMapper<AssistantFeedback> {
}
