package com.backstage.system.mapper.assistant;

import com.backstage.system.domain.assistant.AssistantFeedbackTagRel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 反馈标签关联 Mapper
 *
 * @author backstage
 */
@Mapper
public interface AssistantFeedbackTagRelMapper extends BaseMapper<AssistantFeedbackTagRel> {
}
