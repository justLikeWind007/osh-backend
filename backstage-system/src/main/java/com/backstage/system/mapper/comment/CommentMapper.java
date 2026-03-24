package com.backstage.system.mapper.comment;

import com.backstage.system.domain.comment.Comment;
import com.backstage.system.domain.vo.CommentVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper {

    /**
     * 校验课程是否属于指定专栏。
     */
    boolean existsCourseInColumn(@Param("columnId") Long columnId, @Param("courseId") Long courseId);

    /**
     * 查询课程主评论列表。
     */
    List<CommentVo> selectCourseCommentList(@Param("courseId") Long courseId, @Param("bizType") Integer bizType);

    /**
     * 查询指定主评论下的回复列表。
     */
    List<CommentVo> selectCourseReplyList(@Param("courseId") Long courseId, @Param("parentIds") List<Long> parentIds,
                                          @Param("bizType") Integer bizType);

    /**
     * 校验父评论是否为当前课程下的一级评论。
     */
    boolean existsRootCommentInCourse(@Param("courseId") Long courseId, @Param("commentId") Long commentId,
                                      @Param("bizType") Integer bizType);

    /**
     * 插入评论。
     */
    int insertComment(@Param("comment") Comment comment);

    /**
     * 插入评论业务关联。
     */
    int insertCommentRelation(@Param("commentId") Long commentId, @Param("bizType") Integer bizType, @Param("bizId") Long bizId);

    /**
     * 增加父评论回复数。
     */
    int increaseReplyCount(@Param("commentId") Long commentId);

    /**
     * 查询评论详情。
     */
    CommentVo selectCommentById(@Param("commentId") Long commentId);
}
