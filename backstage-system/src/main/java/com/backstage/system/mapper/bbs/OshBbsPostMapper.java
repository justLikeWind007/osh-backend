package com.backstage.system.mapper.bbs;

import com.backstage.system.domain.bbs.OshBbsPost;
import com.backstage.system.domain.dto.bbs.OshBbsPostDto;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface OshBbsPostMapper {
    /** 查询帖子列表（关联用户表） */
    public List<OshBbsPostDto> selectPostList(OshBbsPost post);

    /** 检查用户是否点赞过 */
    public int checkIsSupport(@Param("postId") Long postId, @Param("userId") Long userId);

    int insertSupport(@Param("postId") Long postId, @Param("userId") Long userId);

    int incrementSupportCount(@Param("postId") Long postId);
    /**
     * 软删除帖子
     */
    int decrementSupportCount(@Param("postId") Long postId);
    /**
     * 根据ID查询帖子详情（包含已逻辑删除的，用于权限校验）
     */
    public OshBbsPost selectPostById(@Param("postId") Long postId);

    int deleteSupport(@Param("postId") Long postId, @Param("userId") Long userId);

    int insertComment(@Param("postId") Long postId,
                      @Param("content") String content,
                      @Param("replyId") Long replyId,
                      @Param("replyUserId") Long replyUserId,
                      @Param("userId") Long userId);

    int incrementCommentCount(@Param("postId") Long postId);

    int softDeletePost(@Param("id") Long id);
    int deleteAllSupportByPostId(@Param("postId") Long postId);

    // OshBbsPostMapper.java
    /**
     * 分页查询我的帖子列表 (返回 DTO 以包含用户信息)
     */
    List<OshBbsPostDto> selectMyPostList(@Param("userId") Long userId,
                                         @Param("offset") int offset,
                                         @Param("pageSize") int pageSize);

    /**
     * 查询我的帖子总记录数
     */
    int selectMyPostCount(@Param("userId") Long userId);

    /**
     * 插入新帖子并返回主键 ID
     */
    int insertPost(OshBbsPost post);
}