package com.backstage.system.controller.course;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.Log;
import com.backstage.common.constant.HttpStatus;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.enums.BusinessType;

import com.backstage.system.domain.course.OshCoures;
import com.backstage.system.domain.vo.CourseDetailVO;
import com.backstage.system.service.IOshCouresService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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
public class OshCouresController extends BaseController
{
    @Autowired
    private IOshCouresService oshCouresService;


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
        List<OshCoures> list = oshCouresService.selectCourseList(columnId);
        return getDataTable(list);
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
            @ApiParam("专栏 ID") @RequestParam(required = false, defaultValue = "0") Long columnId)
    {
        // 校验 appid 是否 为空
        if (appid == null || appid.trim().isEmpty())
        {
            return R.fail("appid 不能为空");
        }
        
        // TODO: 校验请求中的 appid 的有效性

        OshCoures course = oshCouresService.selectCourseById(id);
        if (course == null)
        {
            return R.fail("课程不存在");
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
    public R<Void> add(@RequestBody OshCoures course)
    {
        int deleteResult = oshCouresService.insertCourse(course);
        if (deleteResult>0) {
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
    public R<Void> edit(@RequestBody OshCoures course)
    {
        int deleteResult = oshCouresService.updateCourse(course);
        if (deleteResult>0) {
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
    public R<String> remove(@PathVariable Long[] ids)
    {
        // 2. 执行删除操作（修正原拼写错误：oshCouresService → oshCourseService）
        int deleteResult = oshCouresService.deleteCourseByIds(ids);

        // 3. 结果判断与日志记录
        if (deleteResult>0) {
            return R.ok();
        } else {
            return R.fail("删除课程失败");
        }
    }
}