package com.backstage.system.service.comment;

import com.backstage.common.core.domain.R;
import com.backstage.system.domain.comment.dto.CourseCommentAddDTO;
import com.backstage.system.domain.vo.CommentVo;

import java.util.List;

public interface ICommentService {

    /**
     * 校验课程是否属于指定专栏。
     */
    boolean existsCourseInColumn(Long columnId, Long courseId);

    /**
     * 查询课程评论树。
     */
    List<CommentVo> listCourseComments(Long courseId);

    /**
     * 新增课程评论。
     */
    R<CommentVo> addCourseComment(String token, CourseCommentAddDTO addDTO);
}
