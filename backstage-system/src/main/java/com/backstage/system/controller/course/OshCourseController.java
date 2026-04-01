package com.backstage.system.controller.course;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.Log;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.enums.BusinessType;

import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.domain.course.vo.OshCourseDetailVo;
import com.backstage.system.domain.course.vo.OshCourseSectionVo;
import com.backstage.system.domain.vo.CourseDetailVO;
import com.backstage.system.request.CourseSearchRequest;
import com.backstage.system.service.IOshCouresService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
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
    private IOshCouresService oshCouresService;


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
        OshCourseDetailVo oshCourseDetailVo = oshCouresService.getCourseDetail(id);
        if (oshCourseDetailVo == null) {
            return R.fail("课程不存在");
        }
        return R.ok(oshCourseDetailVo);
    }

    @ApiOperation("课程章节内容")
    @GetMapping("/section/outline/{courseId}")
    @Anonymous
    public R<List<OshCourseSectionVo>> getSectionOutline(@PathVariable Long courseId){
        return R.ok(oshCouresService.getCourseSectionOutline(courseId));
    }

    /**
     * 需校验当前用户是否拥有当前课程小节权限
     * 同时需校验当前用户是否已购买 or 当前sectionId 是否免费
     */
    @ApiOperation("获取text小节内容")
    @GetMapping("/section/text/{sectionId}")
    public R<String> getTextCourseSection(@PathVariable Long sectionId){
        String textContent = oshCouresService.getTextCourseSectionContent(sectionId);
        if (textContent == null) {
            return R.fail("小节不存在或不是可用的文本内容");
        }
        return R.ok(textContent);
    }


    @ApiOperation("获取视频小节内容")
    @GetMapping("/section/video/{sectionId}")
    public R getVideoSection(@PathVariable Long sectionId){

        //  视频内容返回

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
