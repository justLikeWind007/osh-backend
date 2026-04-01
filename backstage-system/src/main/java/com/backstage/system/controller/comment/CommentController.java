package com.backstage.system.controller.comment;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.comment.dto.CourseCommentAddDTO;
import com.backstage.system.service.comment.ICommentService;
import com.backstage.system.domain.vo.CommentVo;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论查询接口
 */
@RestController
@RequestMapping("/pc/comment")
public class CommentController extends BaseController {

    @Autowired
    private ICommentService commentService;

    // TODO  后续每个评论带出 2个回复
    @Anonymous
    @ApiOperation("专栏中课程评论列表")
    @GetMapping("/course/list")
    public R courseList(@RequestParam("columnId") Long columnId,
                                             @RequestParam("courseId") Long courseId) {
        if (!commentService.existsCourseInColumn(columnId, courseId)) {
            return R.fail("该课程不属于当前专栏");
        }

        startPage();
        List<CommentVo> rootComments = commentService.listCourseComments(courseId);
        PageInfo<CommentVo> pageInfo = new PageInfo<>(rootComments);

        Map<String, Object> data = new LinkedHashMap<>(2);
        data.put("rows", rootComments);
        data.put("total", pageInfo.getTotal());
        return R.ok(data, "ok");
    }

    @Anonymous
    @ApiOperation("新增专栏中课程评论")
    @PostMapping("/course/add")
    public R<CommentVo> addCourseComment(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @ApiParam("token") @RequestHeader(value = "token", required = false) String token,
            @RequestBody CourseCommentAddDTO addDTO) {
        return commentService.addCourseComment(token, addDTO);
    }
}
