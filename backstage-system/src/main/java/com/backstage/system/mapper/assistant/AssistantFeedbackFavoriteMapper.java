package com.backstage.system.mapper.assistant;

import com.backstage.system.domain.assistant.AssistantFeedbackFavorite;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 反馈收藏 Mapper 接口
 *
 * @author backstage
 */
public interface AssistantFeedbackFavoriteMapper extends BaseMapper<AssistantFeedbackFavorite> {

    /**
     * 忽略重复收藏插入
     *
     * @param feedbackId 反馈 ID
     * @param userId 收藏用户 ID
     * @param createTime 收藏时间
     * @return 插入行数
     */
    @Insert("insert ignore into assistant_feedback_favorite(feedback_id, user_id, create_time) values(#{feedbackId}, #{userId}, #{createTime})")
    int insertIgnore(@Param("feedbackId") Long feedbackId, @Param("userId") Long userId, @Param("createTime") LocalDateTime createTime);
}
