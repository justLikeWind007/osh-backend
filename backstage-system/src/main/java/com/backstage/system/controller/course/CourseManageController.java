package com.backstage.system.controller.course;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.Log;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.enums.BusinessType;
import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.domain.dto.*;
import com.backstage.system.domain.vo.*;
import com.backstage.system.service.course.ICourseManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 课程管理 Controller
 * 
 * @author ruoyi
 * @date 2026-03-24
 */
@Api(tags = "课程管理")
@RestController
@RequestMapping("/pc/course")
public class CourseManageController extends BaseController {
    
    @Autowired
    private ICourseManageService courseManageService;
    
    // ==================== 课程列表查询接口 ====================

    @Anonymous
    @ApiOperation("获取全部标签&模糊查询标签")
    @GetMapping("/tags")
    public R<List<Map<String, Object>>> getTags(@ApiParam("关键字")  @RequestParam(required = false) String keyword) {
        return R.ok(courseManageService.searchTags(keyword));
    }

    /**
     * 分页查询课程列表
     */
    @Anonymous
    @ApiOperation("查询课程列表")
    @GetMapping("/list")
    public R<?> list(CourseQueryDTO courseDTO) {
        return R.ok(courseManageService.selectCourseList(courseDTO));
    }
    
    /**
     * 获取课程详情
     */
    @Anonymous
    @ApiOperation("获取课程详情")
    @GetMapping("/{courseId}")
    public R<CourseDetailVO> getCourseDetail(
            @ApiParam("课程 ID") @PathVariable Long courseId) {
        Long userId = getUserId();
        return R.ok(courseManageService.getCourseDetail(courseId, userId));
    }
    /**
     * 上传 课程中的 章节 视频
     */
    @Log(title = "章节视频", businessType = BusinessType.INSERT)
   // @PreAuthorize("@ss.hasPermi('system:course:video:upload')")
    @ApiOperation("上传章节视频")
    @PostMapping("/section/video/upload")
    public R<VideoUploadVO> uploadSectionVideo(
            @ApiParam("视频文件") @RequestParam("file") MultipartFile file,
            @ApiParam("课程 ID") @RequestParam("courseId") Long courseId,
            @ApiParam("章节 ID") @RequestParam("sectionId") Long sectionId) {
        Long userId = getUserId();
         courseManageService.uploadSectionVideo(file, courseId, sectionId, userId);
        return R.ok();
    }


    /**
     * 上传课程资料：与课程绑定
     */
    @Log(title = "课程资料", businessType = BusinessType.UPDATE)
    //  @PreAuthorize("@ss.hasPermi('system:course:material:upload')")
    @ApiOperation("上传课程资料")
    @PostMapping("/material/upload/{courseId}")
    public R<Void> uploadMaterial(
            @ApiParam("课程 ID") @PathVariable Long courseId,
            @ApiParam("资料文件") @RequestParam("file") MultipartFile file,
            @ApiParam("资料名称") @RequestParam("materialName") String materialName) {
        Long userId = getUserId();
        courseManageService.uploadSectionMaterial( file,courseId, materialName, userId);
        return R.ok();
    }

    /**
     * 上传课程封面：与课程进行一对一绑定
     */
    @Log(title = "课程封面", businessType = BusinessType.UPDATE)
    //  @PreAuthorize("@ss.hasPermi('system:course:cover:upload')")
    @ApiOperation("上传课程封面")
    @PostMapping("/cover/upload/{courseId}")
    public R<Void> uploadCourseCover(
            @ApiParam("课程 ID") @PathVariable Long courseId,
            @ApiParam("封面文件") @RequestParam("file") MultipartFile file) {
        Long userId = getUserId();
        courseManageService.uploadCourseCover(file, courseId, userId);
        return R.ok();
    }

    /**
     * 获取课程资料列表
     */
    @Anonymous
    @ApiOperation("获取课程资料列表")
    @GetMapping("/{courseId}/materials")
    public R<List<CourseMaterialVO>> getCourseMaterials(
            @ApiParam("课程 ID") @PathVariable Long courseId) {
        Long userId = getUserId();
        return R.ok(courseManageService.getCourseMaterials(courseId, userId));
    }

    /**
     * 删除文件
     */
    @Log(title = "课程资料", businessType = BusinessType.DELETE)
    //@PreAuthorize("@ss.hasPermi('system:course:material:delete')")
    @ApiOperation("删除课程资料")
    @DeleteMapping("/material/{materialId}")
    public R<Void> deleteMaterial(
            @ApiParam("资料 ID") @PathVariable Long materialId) {
        Long userId = getUserId();
        courseManageService.deleteMaterial(materialId, userId);
        return R.ok();
    }



    /**
     * 编辑：添加章节/课时
     * 为已存在的课程追加新的章节或课时内容
     */
    @Log(title = "课程章节", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('system:course:section:add')")
    @ApiOperation("添加章节/课时")
    @PostMapping("/{courseId}/section")
    public R<Long> addSection(
            @ApiParam("课程ID") @PathVariable Long courseId,
            @ApiParam("章节DTO") @Valid @RequestBody SectionCreateDTO sectionCreateDTO) {
        Long userId = getUserId();
        Long sectionId = courseManageService.addSection(courseId, sectionCreateDTO, userId);
        return R.ok(sectionId);
    }
    
    /**
     * 修改课程
     */
    @Log(title = "课程管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('system:course:edit')")
    @ApiOperation("修改课程")
    @PutMapping
    public R<Void> edit(@RequestBody OshCourse course) {
        Long userId = getUserId();
        courseManageService.updateCourse(course, userId);
        return R.ok();
    }
    
    /**
     * 删除课程
     */
    @Log(title = "课程管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('system:course:remove')")
    @ApiOperation("删除课程")
    @DeleteMapping("/{courseId}")
    public R<Void> remove(@ApiParam("课程 ID") @PathVariable Long courseId) {
        Long userId = getUserId();
        courseManageService.deleteCourse(courseId, userId);
        return R.ok();
    }

    /**
     * 检查购买权限
     * 检查用户是否有权限观看此章节
     */
    @Anonymous
    @ApiOperation("检查购买权限")
    @GetMapping("/section/{sectionId}/access")
    public R<SectionAccessVO> checkSectionAccess(
            @ApiParam("章节ID") @PathVariable Long sectionId) {
        Long userId = getUserId();
        return R.ok(courseManageService.checkSectionAccess(sectionId, userId));
    }



    // ==================== 课程章节接口 ====================
    
    /**
     * 获取课程大纲
     */
    @Anonymous
    @ApiOperation("获取课程大纲")
    @GetMapping("/{courseId}/sections")
    public R<List<CourseSectionVO>> getCourseSections(
            @ApiParam("课程 ID") @PathVariable Long courseId) {
        Long userId = getUserId();
        return R.ok(courseManageService.getCourseSections(courseId, userId));
    }
    
    /**
     * 立即学习（试看免费章节）
     */
    @Anonymous
    @ApiOperation("立即学习")
    @PostMapping("/learn")
    public R<SectionDTO> learnSection(@RequestBody Map<String, Long> params) {
        Long courseId = params.get("courseId");
        Long sectionId = params.get("sectionId");
        Long userId = getUserId();
        
        return R.ok(courseManageService.learnSection(courseId, sectionId, userId));
    }
    
    /**
     * 更新学习进度
     */
    @Log(title = "课程学习", businessType = BusinessType.UPDATE)
    @ApiOperation("更新学习进度")
    @PostMapping("/progress/update")
    public R<Void> updateProgress(@RequestBody Map<String, Object> params) {
        Long courseId = (Long) params.get("courseId");
        Long sectionId = (Long) params.get("sectionId");
        Double progress = (Double) params.get("progress");
        Long userId = getUserId();
        
        courseManageService.updateProgress(courseId, sectionId, userId, progress);
        return R.ok();
    }
    
    
    // ==================== 视频播放接口 ====================
    
    /**
     * 获取章节视频信息
     * 返回视频URL、时长、封面、分辨率等信息
     */
    @Anonymous
    @ApiOperation("获取章节视频信息")
    @GetMapping("/section/{sectionId}/video")
    public R<SectionVideoVO> getSectionVideo(
            @ApiParam("章节ID") @PathVariable Long sectionId) {
        Long userId = getUserId();
        return R.ok(courseManageService.getSectionVideo(sectionId, userId));
    }
    
    /**
     * 更新播放进度
     * 提交当前播放位置、进度百分比
     */
    @Log(title = "视频播放", businessType = BusinessType.UPDATE)
    @ApiOperation("更新播放进度")
    @PutMapping("/section/{sectionId}/progress")
    public R<Void> updatePlayProgress(
            @ApiParam("章节ID") @PathVariable Long sectionId,
            @RequestBody ProgressDTO progressDTO) {
        Long userId = getUserId();
        courseManageService.updatePlayProgress(sectionId, progressDTO, userId);
        return R.ok();
    }
    
    /**
     * 获取播放历史
     * 获取用户上次播放位置
     */
    @ApiOperation("获取播放历史")
    @GetMapping("/section/{sectionId}/progress")
    public R<SectionProgressVO> getPlayProgress(
            @ApiParam("章节ID") @PathVariable Long sectionId) {
        Long userId = getUserId();
        return R.ok(courseManageService.getPlayProgress(sectionId, userId));
    }
    
    /**
     * 记录观看完成
     * 标记章节学习完成
     */
    @Log(title = "视频播放", businessType = BusinessType.UPDATE)
    @ApiOperation("记录观看完成")
    @PostMapping("/section/{sectionId}/complete")
    public R<Long> markSectionComplete(
            @ApiParam("章节ID") @PathVariable Long sectionId) {
        Long userId = getUserId();
        Long examId = courseManageService.markSectionComplete(sectionId, userId);
        return R.ok(examId, examId != null ? "章节已完成，请参加考试" : "章节已完成");
    }

    

    
    // ==================== 课程问答接口 ====================
    
    /**
     * 提问（课程小节内）
     */
    @Log(title = "课程问答", businessType = BusinessType.INSERT)
    @ApiOperation("提问")
    @PostMapping("/question")
    public R<Long> askQuestion(@RequestBody QuestionDTO questionDTO) {
        Long userId = getUserId();
        Long questionId = courseManageService.askQuestion(questionDTO, userId);
        return R.ok(questionId);
    }
    
    /**
     * 回答问题（仅课程服务人员）
     */
    @Log(title = "课程问答", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('system:course:question:answer')")
    @ApiOperation("回答问题")
    @PostMapping("/question/{questionId}/answer")
    public R<Void> answerQuestion(
            @ApiParam("问题 ID") @PathVariable Long questionId,
            @RequestBody Map<String, String> params) {
        String answerContent = params.get("answerContent");
        Long userId = getUserId();
        
        courseManageService.answerQuestion(questionId, answerContent, userId);
        return R.ok();
    }
    
    /**
     * 跳转到问答板块问题详情
     */
    @Anonymous
    @ApiOperation("获取问题详情")
    @GetMapping("/question/{questionId}")
    public R<QuestionDTO> getQuestionDetail(
            @ApiParam("问题 ID") @PathVariable Long questionId) {
        return R.ok(courseManageService.getQuestionDetail(questionId));
    }
    
    
    // ==================== 课程评价接口 ====================
    
    /**
     * 提交课程评价
     */
    @Log(title = "课程评价", businessType = BusinessType.INSERT)
    @ApiOperation("提交课程评价")
    @PostMapping("/review")
    public R<Void> submitReview(@RequestBody ReviewDTO reviewDTO) {
        Long userId = getUserId();
        courseManageService.submitReview(reviewDTO, userId);
        return R.ok();
    }
    
    /**
     * 获取课程评价统计
     */
    @Anonymous
    @ApiOperation("获取课程评价统计")
    @GetMapping("/{courseId}/reviews/statistics")
    public R<Map<String, Object>> getReviewStatistics(
            @ApiParam("课程 ID") @PathVariable Long courseId) {
        return R.ok(courseManageService.getReviewStatistics(courseId));
    }
    
    
    // ==================== 课程服务人员接口 ====================
    
    /**
     * 申请成为课程服务人员
     */
    @Log(title = "课程服务人员", businessType = BusinessType.INSERT)
    @ApiOperation("申请成为课程服务人员")
    @PostMapping("/staff/apply")
    public R<Long> applyStaff(
            @RequestBody Map<String, Object> params) {
        Long courseId = (Long) params.get("courseId");
        String staffType = (String) params.get("staffType");
        Integer examScore = (Integer) params.get("examScore");
        Long userId = getUserId();
        
        Long applyId = courseManageService.applyStaff(courseId, staffType, examScore, userId);
        return R.ok(applyId);
    }
    
    /**
     * 审核服务人员申请（管理员）
     */
    @Log(title = "课程服务人员", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('system:course:staff:audit')")
    @ApiOperation("审核服务人员申请")
    @PostMapping("/staff/{applyId}/audit")
    public R<Void> auditStaff(
            @ApiParam("申请 ID") @PathVariable Long applyId,
            @RequestBody Map<String, String> params) {
        String auditStatus = params.get("auditStatus");
        String auditRemark = params.get("auditRemark");
        Long userId = getUserId();
        
        courseManageService.auditStaff(applyId, auditStatus, auditRemark, userId);
        return R.ok();
    }
    
    /**
     * 获取课程服务人员列表
     */
    @Anonymous
    @ApiOperation("获取课程服务人员列表")
    @GetMapping("/{courseId}/staffs")
    public R<List<Map<String, Object>>> getCourseStaffs(
            @ApiParam("课程 ID") @PathVariable Long courseId) {
        return R.ok(courseManageService.getCourseStaffs(courseId));
    }
    
    // ==================== 课程收藏接口 ====================
    
    /**
     * 添加课程收藏
     */
    @ApiOperation("添加课程收藏")
    @PostMapping("/{courseId}/favorite")
    public R<Void> addFavorite(
            @ApiParam("课程 ID") @PathVariable Long courseId) {
        Long userId = getUserId();
        courseManageService.addFavorite(courseId, userId);
        return R.ok();
    }
    
    /**
     * 取消课程收藏
     */
    @ApiOperation("取消课程收藏")
    @DeleteMapping("/{courseId}/favorite")
    public R<Void> removeFavorite(
            @ApiParam("课程 ID") @PathVariable Long courseId) {
        Long userId = getUserId();
        courseManageService.removeFavorite(courseId, userId);
        return R.ok();
    }
    
    /**
     * 检查是否已收藏课程
     */
    @ApiOperation("检查是否已收藏课程")
    @GetMapping("/{courseId}/favorite")
    public R<Boolean> checkFavorited(
            @ApiParam("课程 ID") @PathVariable Long courseId) {
        Long userId = getUserId();
        return R.ok(courseManageService.checkFavorited(courseId, userId));
    }
}
