package com.backstage.system.controller.course;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.DistributeLock;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.response.PageResponse;
import com.backstage.common.utils.SecurityUtils;
import com.backstage.common.utils.StringUtils;
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
import org.springframework.security.access.prepost.PreAuthorize;
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
        Long userId = currentOshUser == null ? null : currentOshUser.getId();
        List<CourseSearchLoginVo> list = oshCourseService.pageQuerySearchCourse(userId, request);
        PageInfo<CourseSearchLoginVo> pageInfo = new PageInfo<>(list);
        return R.ok(PageResponse.of(pageInfo.getList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize()), "ok");
    }

    @ApiOperation("ES课程搜索")
    @PostMapping("/esSearch")
//    @PreAuthorize("hasAuthority('course:list')")
    public R esCourseSearch(@RequestBody CourseSearchRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        Long userId = currentOshUser == null ? null : currentOshUser.getId();
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
        return R.ok(oshCourseEsService.searchCourses(request, currentOshUser.getId()), "ok");
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

    @ApiOperation("获取课程资料数组")
    @GetMapping("/section/materials/{courseId}")
    public R<List<OshCourseMaterial>> getCourseMaterials(@NotNull @PathVariable Long courseId) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
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
    @PreAuthorize("hasAuthority('course:question:submit')")
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
    @PreAuthorize("hasAuthority('course:question:answer')")
    public R<Long> answerCourseQuestion(@Validated @RequestBody CourseQuestionAnswerRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
        }
        return R.ok(oshCourseQuestionService.answerQuestion(currentOshUser.getId(), currentOshUser.getUsername(), request));
    }

    @ApiOperation("新增/修改课程")
    @PostMapping("/save")
//    @PreAuthorize("hasAuthority('course:create')")
    @DistributeLock(scene = "resource", key = "operation", expireTime = 60000, waitTime = 0)
    public R<Long> save(@RequestBody CourseCreateRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) return R.fail("请先登录");

        // 手动校验必填（新增时才强制校验关键字段，更新时可部分修改）
        if (request.getId() == null) {
            if (StringUtils.isBlank(request.getTitle())) return R.fail("课程标题不能为空");
            if (StringUtils.isBlank(request.getCover())) return R.fail("课程封面不能为空");
            if (StringUtils.isBlank(request.getIntro())) return R.fail("课程介绍不能为空");
            if (request.getPrice() == null) return R.fail("课程价格不能为空");
            if (request.getTPrice() == null) return R.fail("课程原价不能为空");
            if (StringUtils.isBlank(request.getType())) return R.fail("课程类型不能为空");
        } else {
            if (StringUtils.isBlank(request.getTitle())) return R.fail("课程标题不能为空");
        }

        Long courseId;
        if (request.getId() != null) {
            // 更新逻辑：将 CreateRequest 转换为 UpdateRequest
            CourseUpdateRequest updateRequest = buildUpdateRequest(request);
            courseId = oshCourseService.updateCourse(updateRequest, currentOshUser);
            if (courseId == null) return R.fail("修改课程失败");
        } else {
            // 新增逻辑
            courseId = oshCourseService.createCourse(request, currentOshUser);
            if (courseId == null) return R.fail("新增课程失败");
        }
        return R.ok(courseId);
    }

    /**
     * 将 CourseCreateRequest 转换为 CourseUpdateRequest
     */
    private CourseUpdateRequest buildUpdateRequest(CourseCreateRequest req) {
        CourseUpdateRequest update = new CourseUpdateRequest();
        update.setId(req.getId());
        update.setTitle(req.getTitle());
        if (StringUtils.isNotBlank(req.getCover())) update.setCover(req.getCover());
        if (StringUtils.isNotBlank(req.getIntro())) update.setIntro(req.getIntro());
        update.setServiceContent(req.getServiceContent());
        update.setPrice(req.getPrice());
        update.setTPrice(req.getTPrice());
        update.setType(req.getType());
        update.setFreeType(req.getFreeType());
        update.setAfterServiceDays(req.getAfterServiceDays());
        update.setExamId(req.getExamId());
        update.setRemark(req.getRemark());
        update.setResourceType(req.getResourceType());
        update.setLevel(req.getLevel());
        update.setTags(req.getTags());
        update.setMaterial(req.getMaterial());
        return update;
    }


    // TODO 暂时只管控创建人可修改
    @ApiOperation("修改课程")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('course:update')")
    @DistributeLock(scene = "resource", key = "operation", expireTime = 60000, waitTime = 0)
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

    @ApiOperation("章节新增/修改")
    @PostMapping("/section/chapter/save")
    @PreAuthorize("hasAuthority('course:chapter:save')")
    public R<Long> saveChapterSection(@Validated @RequestBody CourseChapterCreateRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
        }
        try {
            Long sectionId;
            if (request.getId() != null) {
                // 有 id → 更新章节标题/排序
                oshCourseService.updateCourseChapter(request, currentOshUser);
                sectionId = request.getId();
            } else {
                // 无 id → 新增章节（courseId 必填）
                if (request.getCourseId() == null) {
                    return R.fail("新增章节时课程ID不能为空");
                }
                sectionId = oshCourseService.createCourseChapter(request, currentOshUser);
            }
            return sectionId == null ? R.fail("操作失败") : R.ok(sectionId);
        } catch (IllegalArgumentException ex) {
            return R.fail(ex.getMessage());
        }
    }


    @ApiOperation("视频小节添加")
    @PostMapping("/section/video/save")
    @PreAuthorize("hasAuthority('course:section:video')")
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
    @PreAuthorize("hasAuthority('course:section:text')")
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
//    @PreAuthorize("hasAuthority('course:collection:add')")
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
//    @PreAuthorize("hasAuthority('course:collection:remove')")
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
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) return R.fail("请先登录");

        boolean success = oshCourseService.safeDeleteSection(request.getCourseId(), request.getSectionId(), currentOshUser);
        return success ? R.ok("删除成功") : R.fail("删除失败");
    }

    @ApiOperation("批量删除课程")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('course:delete')")
    public R<String> deleteCourses(@RequestBody CourseDeleteRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) return R.fail("请先登录");
        if (request.getIds() == null || request.getIds().isEmpty()) return R.fail("请选择要删除的课程");

        oshCourseService.deleteCoursesByIds(request.getIds(), currentOshUser);
        return R.ok("删除成功");
    }

}
