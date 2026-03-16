package com.backstage.system.controller.common;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.backstage.common.annotation.Anonymous;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.backstage.common.annotation.Log;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.AjaxResult;
import com.backstage.common.enums.BusinessType;
import com.backstage.system.domain.common.OshRecommendCourses;
import com.backstage.system.service.common.IOshRecommendCoursesService;
import com.backstage.common.utils.poi.ExcelUtil;
import com.backstage.common.core.page.TableDataInfo;

/**
 * 推荐列内容Controller
 * 
 * @author ruoyi
 * @date 2026-03-10
 */
@RestController
@RequestMapping("/pc")
public class OshRecommendCoursesController extends BaseController
{
    @Autowired
    private IOshRecommendCoursesService oshRecommendCoursesService;

    /**
     * 查询推荐列内容列表
     */
//    @PreAuthorize("@ss.hasPermi('system:courses:list')")
    @Anonymous
    @GetMapping("/hot")
    public AjaxResult list(OshRecommendCourses oshRecommendCourses)
    {
        startPage();
        List<OshRecommendCourses> list = oshRecommendCoursesService.selectOshRecommendCoursesList(oshRecommendCourses);

        AjaxResult ajax = AjaxResult.success();
        ajax.put("msg", "ok");
        ajax.put("data", list);
        ajax.put("code", 20000);
        return ajax;
    }

    /**
     * 导出推荐列内容列表
     */
    @PreAuthorize("@ss.hasPermi('system:courses:export')")
    @Log(title = "推荐列内容", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OshRecommendCourses oshRecommendCourses)
    {
        List<OshRecommendCourses> list = oshRecommendCoursesService.selectOshRecommendCoursesList(oshRecommendCourses);
        ExcelUtil<OshRecommendCourses> util = new ExcelUtil<OshRecommendCourses>(OshRecommendCourses.class);
        util.exportExcel(response, list, "推荐列内容数据");
    }

    /**
     * 获取推荐列内容详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:courses:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(oshRecommendCoursesService.selectOshRecommendCoursesById(id));
    }

    /**
     * 新增推荐列内容
     */
    @PreAuthorize("@ss.hasPermi('system:courses:add')")
    @Log(title = "推荐列内容", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OshRecommendCourses oshRecommendCourses)
    {
        return toAjax(oshRecommendCoursesService.insertOshRecommendCourses(oshRecommendCourses));
    }

    /**
     * 修改推荐列内容
     */
    @PreAuthorize("@ss.hasPermi('system:courses:edit')")
    @Log(title = "推荐列内容", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OshRecommendCourses oshRecommendCourses)
    {
        return toAjax(oshRecommendCoursesService.updateOshRecommendCourses(oshRecommendCourses));
    }

    /**
     * 删除推荐列内容
     */
    @PreAuthorize("@ss.hasPermi('system:courses:remove')")
    @Log(title = "推荐列内容", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(oshRecommendCoursesService.deleteOshRecommendCoursesByIds(ids));
    }
}
