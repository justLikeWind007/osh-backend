package com.backstage.system.mapper.assistant;

import com.backstage.system.domain.assistant.AssistantFeedbackLike;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 反馈点赞 Mapper 接口
 *
 * @author backstage
 */
public interface AssistantFeedbackLikeMapper extends BaseMapper<AssistantFeedbackLike> {

    /**
     * 忽略重复点赞插入
     *
     * @param feedbackId 反馈 ID
     * @param userId 点赞用户 ID
     * @param createTime 点赞时间
     * @return 插入行数
     */
    @Insert("insert ignore into assistant_feedback_like(feedback_id, user_id, create_time) values(#{feedbackId}, #{userId}, #{createTime})")
    int insertIgnore(@Param("feedbackId") Long feedbackId, @Param("userId") Long userId, @Param("createTime") LocalDateTime createTime);
}
