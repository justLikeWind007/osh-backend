package com.backstage.system.mapper.assistant;

import com.backstage.system.domain.assistant.AssistantFeedbackCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 助手反馈分类 Mapper
 *
 * @author backstage
 */
@Mapper
public interface AssistantFeedbackCategoryMapper extends BaseMapper<AssistantFeedbackCategory> {
}
