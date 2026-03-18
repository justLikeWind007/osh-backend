package com.backstage.system.mapper.bbs;

import com.backstage.system.domain.dto.bbs.OshBbsCommentDto;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 帖子评论Mapper接口
 */
public interface OshBbsCommentMapper {

    /**
     * 查询评论列表（支持按帖子ID和回复ID筛选）
     * * @param postId 帖子ID
     * @param replyId 回复ID（0为主评论，非0为子回复）
     * @return 评论DTO列表
     */
    public List<OshBbsCommentDto> selectCommentList(@Param("postId") Long postId, @Param("replyId") Long replyId);

    /**
     * 新增评论（发布评论接口会用到）
     */
    public int insertComment(OshBbsCommentDto comment);
    
    /**
     * 更新帖子评论数（发布/删除评论时同步更新 osh_bbs_post 表）
     */
    public int updatePostCommentCount(@Param("postId") Long postId, @Param("count") Integer count);
}