package com.backstage.system.controller.course;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.DistributeLock;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.response.PageResponse;
import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.domain.course.OshCourseMaterial;
import com.backstage.system.domain.course.vo.*;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.request.*;
import com.backstage.system.service.IOshCourseCollectionService;
import com.backstage.system.service.IOshCourseQuestionService;
import com.backstage.system.service.IOshCourseService;
import com.backstage.system.service.course.IOshCourseEsService;
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
    private IOshCourseService oshCourseService;

    @Autowired
    private IOshCourseQuestionService oshCourseQuestionService;

    @Autowired
    private IOshCourseCollectionService oshCourseCollectionService;

    @Autowired
    private IOshCourseEsService oshCourseEsService;


    // TODO 后续追加 ES 查课
    // 免费,
    @ApiOperation("课程搜索")
    @PostMapping("/search")
    @Anonymous
    public R<PageResponse<CourseSearchLoginVo>> courseSearch(@RequestBody CourseSearchRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            List<CourseSearchLoginVo> list = oshCourseService.pageQuerySearchCourse(request);
            PageInfo<CourseSearchLoginVo> pageInfo = new PageInfo<>(list);
            return R.ok(PageResponse.of(pageInfo.getList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize()), "ok");
        }else{
            List<CourseSearchLoginVo> list = oshCourseService.pageQueryLoginSearchCourse(currentOshUser.getId(), request);
            PageInfo<CourseSearchLoginVo> pageInfo = new PageInfo<>(list);
            return R.ok(PageResponse.of(pageInfo.getList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize()), "ok");
        }
    }

    @ApiOperation("ES课程搜索")
    @PostMapping("/esSearch")
    @Anonymous
    public R esCourseSearch(@RequestBody CourseSearchRequest request) {
        User currentUser = UserContextUtil.getCurrentUser();
        Long userId = currentUser == null ? null : currentUser.getId();
        return R.ok(oshCourseEsService.searchCourses(request, userId), "ok");
    }

    @ApiOperation("全量同步课程到ES")
    @PostMapping("/esSync/all")
    @Anonymous
    public R<Integer> syncAllCoursesToEs() {
        return R.ok(oshCourseEsService.syncAllCoursesToEs(), "ok");
    }


    @ApiOperation("登录态课程搜索")
    @PostMapping("/loginSearch/")
    @Anonymous
    public R<PageResponse<CourseSearchLoginVo>> loginCourseSearch(@RequestBody CourseSearchRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
        }
        List<CourseSearchLoginVo> list = oshCourseService.pageQueryLoginSearchCourse(currentOshUser.getId(), request);
        PageInfo<CourseSearchLoginVo> pageInfo = new PageInfo<>(list);
        return R.ok(PageResponse.of(pageInfo.getList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize()), "ok");
        return R.ok(oshCourseEsService.searchCourses(request, currentUser.getId()), "ok");
    }

    @ApiOperation("收藏课程搜索")
    @PostMapping("/search/collection")
    @Anonymous
    public R<PageResponse<OshCourse>> collectionCourseSearch(@RequestBody CourseSearchRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
        }
        List<OshCourse> list = oshCourseService.pageQueryUserCollectionCourse(currentOshUser.getId(), request);
        PageInfo<OshCourse> pageInfo = new PageInfo<>(list);
        return R.ok(PageResponse.of(pageInfo.getList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize()), "ok");
    }

    // TODO  后续需要 调用苍鳞方法去拿可访问性
    @ApiOperation("课程详情")
    @GetMapping("/detail/{id}")
    @Anonymous
    public R<OshCourseDetailVo> getCourseDetail(@NotNull @PathVariable("id") Long id) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        Long userId = currentOshUser == null ? null : currentOshUser.getId();
        OshCourseDetailVo oshCourseDetailVo = oshCourseService.getCourseDetail(id, userId);
        if (oshCourseDetailVo == null) {
            return R.fail("课程不存在");
        }
        return R.ok(oshCourseDetailVo);
    }

    @ApiOperation("获取小节内容 videoUrl or text")
    @GetMapping("/section/content/{courseId}/{sectionId}")
    @Anonymous
    public R<String> getCourseSectionContent(@NotNull @PathVariable Long courseId, @NotNull @PathVariable Long sectionId) throws Exception {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        Long userId = currentOshUser.getId();
        Integer userBuyCourseOrFreeCourse = oshCourseService.isUserBuyCourseOrFreeCourse(courseId, userId);
        if (userBuyCourseOrFreeCourse.compareTo(0) > 0) {
            return R.ok(oshCourseService.getCourseSectionContent(sectionId, userId));
        } else {
            throw new Exception("您没有获取课程内容的权限");
        }
    }

    // TODO 需校验当前用户是否拥有当前课程材小节权限
    @ApiOperation("获取课程资料数组")
    @GetMapping("/section/materials/{courseId}")
    @Anonymous
    public R<List<OshCourseMaterial>> getCourseMaterials(@NotNull @PathVariable Long courseId) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null || !oshCourseService.hasUserBoughtCourse(courseId, currentOshUser.getId())) {
            return R.fail("您还未购买该课程，无法查看课程资料");
        }
        return R.ok(oshCourseService.getCourseMaterials(courseId));
    }

    @ApiOperation("课程章节内容")
    @GetMapping("/section/outline/{courseId}")
    @Anonymous
    public R<List<OshCourseSectionVo>> getSectionOutline(@NotNull @PathVariable Long courseId) {
        return R.ok(oshCourseService.getCourseSectionOutline(courseId));
    }


    @ApiOperation("课程提问提交")
    @PostMapping("/section/submit")
    @Anonymous
    public R<Long> submitCourseSectionQuestion(@Validated @RequestBody CourseSectionQuestionRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
        }
        if (!oshCourseService.canUserAskQuestion(request.getCourseId(), request.getSectionId(), currentOshUser.getId())) {
            return R.fail("您未购买该课程，且课程或章节未免费开放，无法提交课程问题");
        }
        return R.ok(oshCourseQuestionService.submitQuestion(currentOshUser.getId(), currentOshUser.getUsername(), request));
    }

    // TODO 需要校验只有购买了课程以及服务角色才能回答和追问, 部分免费的课程 没买课的也不能提问
    @ApiOperation("课程问题回答")
    @PostMapping("/question/answer")
    @Anonymous
    public R<Long> answerCourseQuestion(@Validated @RequestBody CourseQuestionAnswerRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
        }
        return R.ok(oshCourseQuestionService.answerQuestion(currentOshUser.getId(), currentOshUser.getUsername(), request));
    }

    @ApiOperation("新增课程")
    @PostMapping("/save")
    @Anonymous
    @DistributeLock(key = "resourceOperation", expireTime = 60000, waitTime = 0)
    public R<Long> save(@Validated @RequestBody CourseCreateRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
        }
        Long courseId = oshCourseService.createCourse(request, currentOshUser);
        if (courseId == null) {
            return R.fail("新增课程失败");
        }
        return R.ok(courseId);
    }

    // TODO 暂时只管控创建人可修改
    @ApiOperation("修改课程")
    @PostMapping("/update")
    @Anonymous
    @DistributeLock(key = "resourceOperation", expireTime = 60000, waitTime = 0)
    public R<Long> update(@Validated @RequestBody CourseUpdateRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
        }
        try {
            Long courseId = oshCourseService.updateCourse(request, currentOshUser);
            return R.ok(courseId);
        } catch (IllegalArgumentException ex) {
            return R.fail(ex.getMessage());
        }
    }

    @ApiOperation("章节添加")
    @PostMapping("/section/chapter/save")
    @Anonymous
    public R<Long> saveChapterSection(@Validated @RequestBody CourseChapterCreateRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
        }
        try {
            Long sectionId = oshCourseService.createCourseChapter(request, currentOshUser);
            return sectionId == null ? R.fail("新增一级章节失败") : R.ok(sectionId);
        } catch (IllegalArgumentException ex) {
            return R.fail(ex.getMessage());
        }
    }

    @ApiOperation("视频小节添加")
    @PostMapping("/section/video/save")
    @Anonymous
    public R<Long> saveVideoSection(@Validated @RequestBody CourseVideoSectionCreateRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
        }
        try {
            Long sectionId = oshCourseService.createCourseVideoSection(request, currentOshUser);
            return sectionId == null ? R.fail("新增视频小节失败") : R.ok(sectionId);
        } catch (IllegalArgumentException ex) {
            return R.fail(ex.getMessage());
        }
    }

    @ApiOperation("文本内容小节添加")
    @PostMapping("/section/textContent/save")
    @Anonymous
    public R<Long> saveTextSection(@Validated @RequestBody CourseTextSectionCreateRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
        }
        try {
            Long sectionId = oshCourseService.createCourseTextSection(request, currentOshUser);
            return sectionId == null ? R.fail("新增文本内容小节失败") : R.ok(sectionId);
        } catch (IllegalArgumentException ex) {
            return R.fail(ex.getMessage());
        }
    }


    @ApiOperation("获取章节提问列表")
    @GetMapping("/section/questions/{courseId}/{sectionId}")
    @Anonymous
    public R<PageResponse<CourseQuestionListItemVo>> getSectionQuestions(@NotNull @PathVariable Long courseId, @NotNull @PathVariable Long sectionId, @RequestParam(required = false) Integer pageNum, @RequestParam(required = false) Integer pageSize) {
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
    @Anonymous
    public R<List<CourseQuestionAnswerItemVo>> getQuestionAnswers(@NotNull @PathVariable Long questionId) {
        return R.ok(oshCourseQuestionService.listQuestionAnswers(questionId));
    }

    @ApiOperation("收藏课程")
    @PostMapping("/collection/add")
    @Anonymous
    public R collectCourse(@Validated @RequestBody CourseCollectionRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
        }
        oshCourseCollectionService.collectCourse(currentOshUser.getId(), currentOshUser.getUsername(), request.getCourseId());
        return R.ok("收藏课程成功");
    }

    @ApiOperation("取消收藏课程")
    @PostMapping("/collection/remove")
    @Anonymous
    public R removeCourseCollection(@Validated @RequestBody CourseCollectionRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
        }
        oshCourseCollectionService.removeCourseCollection(currentOshUser.getId(), currentOshUser.getUsername(), request.getCourseId());
        return R.ok("取消课程收藏成功");
    }

    @ApiOperation("删除章节/小节")
    @PostMapping("/sectionDelete")
    @Anonymous // 建议根据实际权限调整
    public R<String> deleteSection(@Validated @RequestBody CourseSectionDeleteRequest request) {
        User currentUser = UserContextUtil.getCurrentUser();
        if (currentUser == null) return R.fail("请先登录");
    public R<String> deleteSection(@PathVariable("id") Long id) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) return R.fail("请先登录");

        boolean success = oshCourseService.safeDeleteSection(request.getCourseId(), request.getSectionId(), currentUser);
        // 调用一个通用的安全删除 Service
        boolean success = oshCourseService.safeDeleteSection(id, currentOshUser);
        return success ? R.ok("删除成功") : R.fail("删除失败");
    }
}
