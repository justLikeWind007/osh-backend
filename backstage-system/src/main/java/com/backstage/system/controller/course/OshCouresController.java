package com.backstage.system.controller.course;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.Log;
import com.backstage.common.constant.HttpStatus;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.enums.BusinessType;

import com.backstage.system.domain.course.OshCoures;
import com.backstage.system.domain.dto.CourseCreateDTO;
import com.backstage.system.domain.vo.CourseDetailVO;
import com.backstage.system.domain.vo.VideoUploadVO;
import com.backstage.system.service.IOshCouresService;
import com.backstage.system.service.course.ICourseManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private static final Logger log = LoggerFactory.getLogger(OshCouresController.class);
    
    @Autowired
    private IOshCouresService oshCouresService;
    
    @Autowired
    private ICourseManageService courseManageService;


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
     * 新增课程（包含章节、视频、资料）
     */
    @Anonymous
    @Log(title = "课程", businessType = BusinessType.INSERT)
    @ApiOperation("新增课程（包含章节、视频、资料）")
    @PostMapping("/create")
    public R<Long> createCourse(@RequestBody CourseCreateDTO courseCreateDTO)
    {
        try {
            // 获取当前用户 ID（实际项目中需要从登录信息中获取）
            Long userId = getUserId();
            
            // 调用 Service 层方法创建课程及章节
            Long courseId = courseManageService.createCourseWithSections(courseCreateDTO, userId);
            
            return R.ok(courseId);
        } catch (Exception e) {
            log.error("创建课程失败：{}", e.getMessage(), e);
            return R.fail("创建失败：" + e.getMessage());
        }
    }

    /**
     * 上传课程封面图片
     */
    @Anonymous
    @Log(title = "课程封面", businessType = BusinessType.UPLOAD)
    @ApiOperation("上传课程封面")
    @PostMapping("/cover/upload")
    public R<Void> uploadCourseCover(
            @RequestParam("file") MultipartFile file,
            @RequestParam("courseId") Long courseId)
    {
        try {
            Long userId = getUserId();
            courseManageService.uploadCourseCover(file, courseId, userId);
            return R.ok();
        } catch (Exception e) {
            log.error("上传封面失败：{}", e.getMessage(), e);
            return R.fail("上传失败：" + e.getMessage());
        }
    }

    /**
     * 上传课时视频
     */
    @Anonymous
    @Log(title = "课时视频", businessType = BusinessType.UPLOAD)
    @ApiOperation("上传课时视频")
    @PostMapping("/section/video")
    public R<Object> uploadSectionVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("courseId") Long courseId,
            @RequestParam("sectionId") Long sectionId)
    {
        try {
            Long userId = getUserId();
            VideoUploadVO result = courseManageService.uploadSectionVideo(file, courseId, sectionId, userId);
            return R.ok(result);
        } catch (Exception e) {
            log.error("上传视频失败：{}", e.getMessage(), e);
            return R.fail("上传失败：" + e.getMessage());
        }
    }

    /**
     * 上传课时资料
     */
    @Anonymous
    @Log(title = "课时资料", businessType = BusinessType.UPLOAD)
    @ApiOperation("上传课时资料")
    @PostMapping("/section/material")
    public R<Long> uploadSectionMaterial(
            @RequestParam("file") MultipartFile file,
            @RequestParam("courseId") Long courseId,
            @RequestParam("materialName") String materialName)
    {
        try {
            Long userId = getUserId();
            Long materialId = courseManageService.uploadSectionMaterial(file, courseId, materialName, userId);
            return R.ok(materialId);
        } catch (Exception e) {
            log.error("上传资料失败：{}", e.getMessage(), e);
            return R.fail("上传失败：" + e.getMessage());
        }
    }

    /**
     * 新增课程（简单版本，仅保存基本信息）
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