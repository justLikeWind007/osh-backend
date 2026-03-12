package com.backstage.system.controller.course;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.Log;
import com.backstage.common.constant.HttpStatus;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.AjaxResult;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.enums.BusinessType;

import com.backstage.system.domain.course.SysCourse;
import com.backstage.system.domain.vo.CourseDetailVO;
import com.backstage.system.service.ISysCourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
public class SysCourseController extends BaseController
{
    @Autowired
    private ISysCourseService sysCourseService;


    /**
     * 查询课程列表
     */
    //@PreAuthorize("@ss.hasPermi('system:course:list')")
    @Anonymous
    @ApiOperation("查询课程列表")
    @GetMapping("/list")
    public TableDataInfo list(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @ApiParam("专栏 ID") @RequestParam(required = false) Long columnId)
    {
        // 校验 appid 是否 为空
        if (appid == null || appid.trim().isEmpty())
        {
            TableDataInfo tableDataInfo = new TableDataInfo();
            tableDataInfo.setCode(HttpStatus.ERROR);
            tableDataInfo.setMsg("appid 不能为空或空字符串");
            tableDataInfo.setRows(null);
            return tableDataInfo;
        }


        startPage();
        List<SysCourse> list = sysCourseService.selectCourseList(columnId);
        return getDataTable(list);
    }

    /**
     * 获取课程详细信息
     */
    //@PreAuthorize("@ss.hasPermi('system:course:query')")
    @Anonymous
    @ApiOperation("获取课程详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(
            @ApiParam("网校 appid") @RequestHeader("appid") String appid,
            @ApiParam("课程 ID") @PathVariable("id") Long id,
            @ApiParam("专栏 ID") @RequestParam(required = false, defaultValue = "0") Long columnId)
    {
        // 校验 appid 是否 为空
        if (appid == null || appid.trim().isEmpty())
        {
            return AjaxResult.error("appid 不能为空");
        }
        
        // TODO: 校验请求中的 appid 的有效性

        SysCourse course = sysCourseService.selectCourseById(id);
        if (course == null)
        {
            return AjaxResult.error("课程不存在");
        }

        // 构建响应数据
        CourseDetailVO data = new CourseDetailVO();
        data.setId(course.getId());
        data.setTitle(course.getTitle());
        data.setCover(course.getCover());
        data.setTryContent(course.getTryContent());
        data.setPrice(course.getPrice() != null ? course.getPrice().toString() : "0.00");
        data.setTPrice(course.getTPrice() != null ? course.getTPrice().toString() : "20.00");
        data.setType(course.getType());

        return AjaxResult.success("ok", data);
    }

    /**
     * 新增课程
     */
    //@PreAuthorize("@ss.hasPermi('system:course:add')")
    @Anonymous
    @Log(title = "课程", businessType = BusinessType.INSERT)
    @ApiOperation("新增课程")
    @PostMapping
    public AjaxResult add(@RequestBody SysCourse course)
    {
        return toAjax(sysCourseService.insertCourse(course));
    }

    /**
     * 修改课程
     */
    //@PreAuthorize("@ss.hasPermi('system:course:edit')")
    @Anonymous
    @Log(title = "课程", businessType = BusinessType.UPDATE)
    @ApiOperation("修改课程")
    @PutMapping
    public AjaxResult edit(@RequestBody SysCourse course)
    {
        return toAjax(sysCourseService.updateCourse(course));
    }

    /**
     * 删除课程
     */
    //@PreAuthorize("@ss.hasPermi('system:course:remove')")
    @Anonymous
    @Log(title = "课程", businessType = BusinessType.DELETE)
	@ApiOperation("删除课程")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysCourseService.deleteCourseByIds(ids));
    }
}
