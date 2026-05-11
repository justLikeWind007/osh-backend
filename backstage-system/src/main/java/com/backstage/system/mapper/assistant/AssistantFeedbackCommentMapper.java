package com.backstage.system.mapper.assistant;

import com.backstage.system.domain.assistant.AssistantFeedbackComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 助手反馈评论 Mapper
 *
 * @author backstage
 */
@Mapper
public interface AssistantFeedbackCommentMapper extends BaseMapper<AssistantFeedbackComment> {
}
