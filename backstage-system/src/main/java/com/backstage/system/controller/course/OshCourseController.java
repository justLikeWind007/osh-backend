package com.backstage.system.controller.course;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.Log;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.enums.BusinessType;
import com.backstage.common.response.PageResponse;

import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.domain.course.OshCourseMaterial;
import com.backstage.system.domain.course.vo.CourseQuestionAnswerItemVo;
import com.backstage.system.domain.course.vo.CourseQuestionListItemVo;
import com.backstage.system.domain.course.vo.OshCourseDetailVo;
import com.backstage.system.domain.course.vo.OshCourseSectionVo;
import com.backstage.system.domain.user.User;
import com.backstage.system.domain.vo.CourseDetailVO;
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
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private IOshCourseService oshCouresService;

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
    public R<Map<String, Object>> courseSearch(@RequestBody CourseSearchRequest request) {
        List<OshCourse> list = oshCouresService.pageQuerySearchCourse(request);
        PageInfo<OshCourse> pageInfo = new PageInfo<>(list);
        Map<String, Object> data = new LinkedHashMap<>(4);
        data.put("rows", list);
        data.put("total", pageInfo.getTotal());
        data.put("pageNum", pageInfo.getPageNum());
        data.put("pageSize", pageInfo.getPageSize());
        return R.ok(data, "ok");
    }

    @ApiOperation("课程详情")
    @GetMapping("/detail/{id}")
    @Anonymous
    public R<OshCourseDetailVo> getCourseDetail(@NotNull @PathVariable("id") Long id) {

        User currentUser = userContextUtil.getCurrentUser();

        OshCourseDetailVo oshCourseDetailVo = oshCouresService.getCourseDetail(id, currentUser.getId());
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
        Integer userBuyCourseOrFreeCourse = oshCouresService.isUserBuyCourseOrFreeCourse(courseId, userId);
        if (userBuyCourseOrFreeCourse.compareTo(0) > 0) {
            return R.ok(oshCouresService.getCourseSectionContent(sectionId, userId));
        } else {
            throw new Exception("您没有改课程权限");
        }
    }

    // TODO 需校验当前用户是否拥有当前课程材小节权限
    @ApiOperation("获取课程资料数组")
    @GetMapping("/section/materials/{courseId}")
    public R<List<OshCourseMaterial>> getCourseMaterials(@NotNull @PathVariable Long courseId) {
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null || !oshCouresService.hasUserBoughtCourse(courseId, currentUser.getId())) {
            return R.fail("您还未购买该课程，无法查看课程资料");
        }
        return R.ok(oshCouresService.getCourseMaterials(courseId));
    }

    @ApiOperation("课程章节内容")
    @GetMapping("/section/outline/{courseId}")
    @Anonymous
    public R<List<OshCourseSectionVo>> getSectionOutline(@NotNull @PathVariable Long courseId) {
        return R.ok(oshCouresService.getCourseSectionOutline(courseId));
    }


    @ApiOperation("课程提问提交")
    @PostMapping("/section/submit")
    public R<Long> submitCourseSectionQuestion(@Validated @RequestBody CourseSectionQuestionRequest request){
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            return R.fail("请先登录");
        }
        if (!oshCouresService.canUserAskQuestion(request.getCourseId(), request.getSectionId(), currentUser.getId())) {
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

    // TODO 待做,还没真实确定入参具体结构
    @ApiOperation("保存课程")
    @PostMapping("/save")
    public R save(@RequestBody OshCourse course) {
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            return R.fail("请先登录");
        }
        if (course == null || course.getId() == null) {
            return R.fail("课程ID不能为空");
        }
        if (!oshCouresService.hasUserBoughtCourse(course.getId(), currentUser.getId())) {
            return R.fail("您还未购买该课程，无法保存课程");
        }
        return R.ok();
    }

    /**
     * 需校验当前用户是否拥有当前课程小节权限
     * 同时需校验当前用户是否已购买 or 当前sectionId 是否免费
     */
    @ApiOperation("获取text小节内容")
    @GetMapping("/section/text/{sectionId}")
    public R<String> getTextCourseSection(@NotNull @PathVariable Long sectionId) {
        String textContent = oshCouresService.getTextCourseSectionContent(sectionId);
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

    /**
     * 获取课程详细信息
     */
    //@PreAuthorize("@ss.hasPermi('system:course:query')")
    @Anonymous
    @ApiOperation("获取课程详细信息")
    @GetMapping(value = "/{id}")
    public R<CourseDetailVO> getInfo(
            @ApiParam("网校 appid") @RequestHeader("appid") String appid,
            @ApiParam("课程 ID") @PathVariable("id") Long id,
            @ApiParam("专栏 ID") @RequestParam(required = false, defaultValue = "0") Long columnId) {
        // 校验 appid 是否 为空
        if (appid == null || appid.trim().isEmpty()) {
            return R.fail("appid 不能为空");
        }

        // TODO: 校验请求中的 appid 的有效性

        OshCourse course = oshCouresService.selectCourseById(id);
        if (course == null) {
            return R.fail("课程不存在");
        }

        // 构建响应数据
        CourseDetailVO data = new CourseDetailVO();
        data.setId(course.getId());
        data.setTitle(course.getTitle());
        data.setCover(course.getCover());
        data.setTryContent(course.getIntro());
        data.setPrice(course.getPrice() != null ? course.getPrice().toString() : "0.00");
        data.setTPrice(course.getTPrice() != null ? course.getTPrice().toString() : "20.00");
        data.setType(course.getType());

        return R.ok(data, "ok");
    }

    /**
     * 新增课程
     */
    //@PreAuthorize("@ss.hasPermi('system:course:add')")
    @Anonymous
    @Log(title = "课程", businessType = BusinessType.INSERT)
    @ApiOperation("新增课程")
    @PostMapping
    public R<Void> add(@RequestBody OshCourse course) {
        int deleteResult = oshCouresService.insertCourse(course);
        if (deleteResult > 0) {
            return R.ok();
        } else {
            return R.fail("新增失败");
        }
    }

    /**
     * 修改课程
     */
    //@PreAuthorize("@ss.hasPermi('system:course:edit')")
    @Anonymous
    @Log(title = "课程", businessType = BusinessType.UPDATE)
    @ApiOperation("修改课程")
    @PutMapping
    public R<Void> edit(@RequestBody OshCourse course) {
        int deleteResult = oshCouresService.updateCourse(course);
        if (deleteResult > 0) {
            return R.ok();
        } else {
            return R.fail("修改失败");
        }
    }

    /**
     * 删除课程
     */
    //@PreAuthorize("@ss.hasPermi('system:course:remove')")
    @Anonymous
    @Log(title = "课程", businessType = BusinessType.DELETE)
    @ApiOperation("删除课程")
    @DeleteMapping("/{ids}")
    public R<String> remove(@PathVariable Long[] ids) {
        // 2. 执行删除操作（修正原拼写错误：oshCouresService → oshCourseService）
        int deleteResult = oshCouresService.deleteCourseByIds(ids);

        // 3. 结果判断与日志记录
        if (deleteResult > 0) {
            return R.ok();
        } else {
            return R.fail("删除课程失败");
        }
    }
}
