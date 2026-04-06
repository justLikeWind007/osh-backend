package com.backstage.system.controller.course;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.response.PageResponse;

import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.domain.course.OshCourseMaterial;
import com.backstage.system.domain.course.vo.CourseQuestionAnswerItemVo;
import com.backstage.system.domain.course.vo.CourseQuestionListItemVo;
import com.backstage.system.domain.course.vo.OshCourseDetailVo;
import com.backstage.system.domain.course.vo.OshCourseSectionVo;
import com.backstage.system.domain.user.User;
import com.backstage.system.request.CourseCreateRequest;
import com.backstage.system.request.CourseChapterCreateRequest;
import com.backstage.system.request.CourseCollectionRequest;
import com.backstage.system.request.CourseQuestionAnswerRequest;
import com.backstage.system.request.CourseQuestionPageRequest;
import com.backstage.system.request.CourseSearchRequest;
import com.backstage.system.request.CourseSectionQuestionRequest;
import com.backstage.system.request.CourseTextSectionCreateRequest;
import com.backstage.system.request.CourseVideoSectionCreateRequest;
import com.backstage.system.service.IOshCourseCollectionService;
import com.backstage.system.service.IOshCourseService;
import com.backstage.system.service.IOshCourseQuestionService;
import com.backstage.system.utils.UserContextUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 课程信息 Controller
 *
 * @author ruoyi
 * @date 2026-01-XX
 */
@Api(tags = "课程管理")
@RestController
@RequestMapping("/pc/course")
public class OshCourseController extends BaseController {


    @Autowired
    private IOshCourseService oshCoureService;

    @Autowired
    private UserContextUtil userContextUtil;

    @Autowired
    private IOshCourseQuestionService oshCourseQuestionService;

    @Autowired
    private IOshCourseCollectionService oshCourseCollectionService;


    // TODO 后续追加 ES 查课
    @ApiOperation("课程搜索")
    @PostMapping("/search")
    @Anonymous
    public R<PageResponse<OshCourse>> courseSearch(@RequestBody CourseSearchRequest request) {
        List<OshCourse> list = oshCoureService.pageQuerySearchCourse(request);
        PageInfo<OshCourse> pageInfo = new PageInfo<>(list);
        return R.ok(PageResponse.of(pageInfo.getList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize()), "ok");
    }

    @ApiOperation("收藏课程搜索")
    @PostMapping("/search/collection")
    public R<PageResponse<OshCourse>> collectionCourseSearch(@RequestBody CourseSearchRequest request) {
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            return R.fail("请先登录");
        }
        List<OshCourse> list = oshCoureService.pageQueryUserCollectionCourse(currentUser.getId(), request);
        PageInfo<OshCourse> pageInfo = new PageInfo<>(list);
        return R.ok(PageResponse.of(pageInfo.getList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize()), "ok");
    }

    @ApiOperation("课程详情")
    @GetMapping("/detail/{id}")
    @Anonymous
    public R<OshCourseDetailVo> getCourseDetail(@NotNull @PathVariable("id") Long id) {

        User currentUser = userContextUtil.getCurrentUser();
        OshCourseDetailVo oshCourseDetailVo = oshCoureService.getCourseDetail(id, currentUser.getId());
        if (oshCourseDetailVo == null) {
            return R.fail("课程不存在");
        }
        return R.ok(oshCourseDetailVo);
    }

    @ApiOperation("获取小节内容 videoUrl or text")
    @GetMapping("/section/content/{courseId}/{sectionId}")
    public R<String> getCourseSectionContent(
            @NotNull @PathVariable Long courseId,
            @NotNull @PathVariable Long sectionId
    ) throws Exception {
        User currentUser = userContextUtil.getCurrentUser();
        Long userId = currentUser.getId();
        Integer userBuyCourseOrFreeCourse = oshCoureService.isUserBuyCourseOrFreeCourse(courseId, userId);
        if (userBuyCourseOrFreeCourse.compareTo(0) > 0) {
            return R.ok(oshCoureService.getCourseSectionContent(sectionId, userId));
        } else {
            throw new Exception("您没有获取课程内容的权限");
        }
    }

    // TODO 需校验当前用户是否拥有当前课程材小节权限
    @ApiOperation("获取课程资料数组")
    @GetMapping("/section/materials/{courseId}")
    public R<List<OshCourseMaterial>> getCourseMaterials(@NotNull @PathVariable Long courseId) {
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null || !oshCoureService.hasUserBoughtCourse(courseId, currentUser.getId())) {
            return R.fail("您还未购买该课程，无法查看课程资料");
        }
        return R.ok(oshCoureService.getCourseMaterials(courseId));
    }

    @ApiOperation("课程章节内容")
    @GetMapping("/section/outline/{courseId}")
    @Anonymous
    public R<List<OshCourseSectionVo>> getSectionOutline(@NotNull @PathVariable Long courseId) {
        return R.ok(oshCoureService.getCourseSectionOutline(courseId));
    }


    @ApiOperation("课程提问提交")
    @PostMapping("/section/submit")
    public R<Long> submitCourseSectionQuestion(@Validated @RequestBody CourseSectionQuestionRequest request) {
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            return R.fail("请先登录");
        }
        if (!oshCoureService.canUserAskQuestion(request.getCourseId(), request.getSectionId(), currentUser.getId())) {
            return R.fail("您未购买该课程，且课程或章节未免费开放，无法提交课程问题");
        }
        return R.ok(oshCourseQuestionService.submitQuestion(currentUser.getId(), currentUser.getUsername(), request));
    }

    // TODO 需要校验只有购买了课程以及服务角色才能回答和追问, 部分免费的课程 没买课的也不能提问
    @ApiOperation("课程问题回答")
    @PostMapping("/question/answer")
    public R<Long> answerCourseQuestion(@Validated @RequestBody CourseQuestionAnswerRequest request) {
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            return R.fail("请先登录");
        }
        return R.ok(oshCourseQuestionService.answerQuestion(currentUser.getId(), currentUser.getUsername(), request));
    }

    @ApiOperation("新增课程")
    @PostMapping("/save")
    public R<Long> save(@Validated @RequestBody CourseCreateRequest request) {
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            return R.fail("请先登录");
        }
        Long courseId = oshCoureService.createCourse(request, currentUser);
        if (courseId == null) {
            return R.fail("新增课程失败");
        }
        return R.ok(courseId);
    }

    @ApiOperation("一级章节添加")
    @PostMapping("/section/chapter/save")
    public R<Long> saveChapterSection(@Validated @RequestBody CourseChapterCreateRequest request) {
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            return R.fail("请先登录");
        }
        try {
            Long sectionId = oshCoureService.createCourseChapter(request, currentUser);
            return sectionId == null ? R.fail("新增一级章节失败") : R.ok(sectionId);
        } catch (IllegalArgumentException ex) {
            return R.fail(ex.getMessage());
        }
    }

    @ApiOperation("视频小节添加")
    @PostMapping("/section/video/save")
    public R<Long> saveVideoSection(@Validated @RequestBody CourseVideoSectionCreateRequest request) {
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            return R.fail("请先登录");
        }
        try {
            Long sectionId = oshCoureService.createCourseVideoSection(request, currentUser);
            return sectionId == null ? R.fail("新增视频小节失败") : R.ok(sectionId);
        } catch (IllegalArgumentException ex) {
            return R.fail(ex.getMessage());
        }
    }

    @ApiOperation("图文小节添加")
    @PostMapping("/section/text/save")
    public R<Long> saveTextSection(@Validated @RequestBody CourseTextSectionCreateRequest request) {
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            return R.fail("请先登录");
        }
        try {
            Long sectionId = oshCoureService.createCourseTextSection(request, currentUser);
            return sectionId == null ? R.fail("新增图文小节失败") : R.ok(sectionId);
        } catch (IllegalArgumentException ex) {
            return R.fail(ex.getMessage());
        }
    }

    /**
     * 需校验当前用户是否拥有当前课程小节权限
     * 同时需校验当前用户是否已购买 or 当前sectionId 是否免费
     */
    @ApiOperation("获取text小节内容")
    @GetMapping("/section/text/{sectionId}")
    public R<String> getTextCourseSection(@NotNull @PathVariable Long sectionId) {
        String textContent = oshCoureService.getTextCourseSectionContent(sectionId);
        if (textContent == null) {
            return R.fail("小节不存在或不是可用的文本内容");
        }
        return R.ok(textContent);
    }

    @ApiOperation("获取视频小节内容")
    @GetMapping("/section/video/{sectionId}")
    public R getVideoSection(@PathVariable Long sectionId) {
        return R.ok();
    }

    @ApiOperation("获取章节提问列表")
    @GetMapping("/section/questions/{courseId}/{sectionId}")
    public R<PageResponse<CourseQuestionListItemVo>> getSectionQuestions(
            @NotNull @PathVariable Long courseId,
            @NotNull @PathVariable Long sectionId,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize
    ) {
        CourseQuestionPageRequest request = new CourseQuestionPageRequest();
        request.setCourseId(courseId);
        request.setSectionId(sectionId);
        request.setPageNum(pageNum == null ? 1 : pageNum);
        request.setPageSize(pageSize == null ? 10 : pageSize);
        List<CourseQuestionListItemVo> list = oshCourseQuestionService.listSectionQuestions(request);
        PageInfo<CourseQuestionListItemVo> pageInfo = new PageInfo<>(list);
        return R.ok(PageResponse.of(pageInfo.getList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize()), "ok");
    }

    // TODO 需要校验用户是否有权限对整个课程
    @ApiOperation("获取问题回答列表")
    @GetMapping("/question/answers/{questionId}")
    public R<List<CourseQuestionAnswerItemVo>> getQuestionAnswers(@NotNull @PathVariable Long questionId) {
        return R.ok(oshCourseQuestionService.listQuestionAnswers(questionId));
    }

    @ApiOperation("收藏课程")
    @PostMapping("/collection/add")
    public R<Void> collectCourse(@Validated @RequestBody CourseCollectionRequest request) {
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            return R.fail("请先登录");
        }
        oshCourseCollectionService.collectCourse(currentUser.getId(), currentUser.getUsername(), request.getCourseId());
        return R.ok("收藏课程成功");
    }

    @ApiOperation("取消收藏课程")
    @PostMapping("/collection/remove")
    public R<Void> removeCourseCollection(@Validated @RequestBody CourseCollectionRequest request) {
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            return R.fail("请先登录");
        }
        oshCourseCollectionService.removeCourseCollection(currentUser.getId(), currentUser.getUsername(), request.getCourseId());
        return R.ok("取消课程收藏成功");
    }
}
