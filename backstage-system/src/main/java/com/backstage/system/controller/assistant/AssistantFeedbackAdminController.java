package com.backstage.system.controller.assistant;

import com.backstage.common.constant.HttpStatus;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.assistant.dto.*;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackTagVO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackVO;
import com.backstage.system.service.assistant.IAssistantFeedbackCategoryService;
import com.backstage.system.service.assistant.IAssistantFeedbackProcessRecordService;
import com.backstage.system.service.assistant.IAssistantFeedbackService;
import com.backstage.system.service.assistant.IAssistantFeedbackTagService;
import com.backstage.system.utils.UserContextUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * AI 助手反馈管理接口 Controller（需要管理员权限）
 *
 * @author backstage
 */
@Api(tags = "AI助手反馈-管理接口")
@RestController
@RequestMapping("/pc/admin/feedback")
public class AssistantFeedbackAdminController extends BaseController {

    public AssistantFeedbackAdminController(IAssistantFeedbackService feedbackService, IAssistantFeedbackCategoryService categoryService, IAssistantFeedbackTagService feedbackTagService, IAssistantFeedbackProcessRecordService processRecordService) {
        this.feedbackService = feedbackService;
        this.categoryService = categoryService;
        this.feedbackTagService = feedbackTagService;
        this.processRecordService = processRecordService;
    }

    private final IAssistantFeedbackService feedbackService;
    private final IAssistantFeedbackCategoryService categoryService;
    private final IAssistantFeedbackTagService feedbackTagService;
    private final IAssistantFeedbackProcessRecordService processRecordService;

    /**
     * 创建公告（仅管理员）
     */
    @ApiOperation("创建公告")
    @PreAuthorize("hasAuthority('system:feedback:manage')")
    @PostMapping("/announcement/create")
    public R<AssistantFeedbackVO> createAnnouncement(@Validated @RequestBody AssistantFeedbackCreateDTO dto) {
        ensureAdmin();
        Long userId = getCurrentUserId();

        // 验证分类是否为公告分类
        if (!categoryService.isAdminOnly(dto.getCategoryId())) {
            return R.fail("只能创建公告类型的反馈");
        }

        AssistantFeedbackVO feedback = feedbackService.createFeedback(userId, dto, true);
        return R.ok(feedback, "公告创建成功");
    }

    /**
     * 创建反馈标签
     */
    @ApiOperation("创建反馈标签")
    @PreAuthorize("hasAuthority('system:feedback:manage')")
    @PostMapping("/tag/create")
    public R<AssistantFeedbackTagVO> createTag(@Validated @RequestBody AssistantFeedbackTagCreateDTO dto) {
        ensureAdmin();
        AssistantFeedbackTagVO tag = feedbackTagService.createTag(dto, getCurrentUserId());
        return R.ok(tag, "标签创建成功");
    }

    /**
     * 反馈管理列表（分页）
     */
    @ApiOperation("反馈管理列表")
    @PreAuthorize("hasAuthority('system:feedback:manage')")
    @PostMapping("/page")
    public TableDataInfo pageFeedback(@RequestBody AssistantFeedbackPageDTO dto) {
        ensureAdmin();
        return feedbackService.pageFeedback(dto);
    }

    /**
     * 工单管理列表（分页）
     */
    @ApiOperation("工单管理列表")
    @PreAuthorize("hasAuthority('system:feedback:manage')")
    @GetMapping("/ticket/list")
    public TableDataInfo ticketList(AssistantTicketQueryDTO queryDTO) {
        ensureAdmin();
        startPage();
        return feedbackService.listTickets(queryDTO);
    }

    /**
     * 置顶反馈
     */
    @ApiOperation("置顶反馈")
    @PreAuthorize("hasAuthority('system:feedback:manage')")
    @PostMapping("/{id}/pin")
    public R<String> pinFeedback(@PathVariable("id") Long feedbackId,
                                  @RequestParam("pinOrder") Integer pinOrder) {
        ensureAdmin();
        feedbackService.pinFeedback(feedbackId, pinOrder);
        return R.ok("置顶成功");
    }

    /**
     * 取消置顶
     */
    @ApiOperation("取消置顶")
    @PreAuthorize("hasAuthority('system:feedback:manage')")
    @PostMapping("/{id}/unpin")
    public R<String> unpinFeedback(@PathVariable("id") Long feedbackId) {
        ensureAdmin();
        feedbackService.unpinFeedback(feedbackId);
        return R.ok("取消置顶成功");
    }

    /**
     * 更新反馈状态
     */
    @ApiOperation("更新反馈状态")
    @PreAuthorize("hasAuthority('system:feedback:manage')")
    @PostMapping("/{id}/status")
    public R<AssistantFeedbackVO> updateStatus(@PathVariable("id") Long feedbackId,
                                                 @Validated @RequestBody AssistantTicketStatusUpdateDTO dto) {
        ensureAdmin();
        Long handlerId = getCurrentUserId();
        AssistantFeedbackVO feedback = feedbackService.updateTicketStatus(feedbackId, handlerId, dto);
        return R.ok(feedback, "状态更新成功");
    }

    /**
     * 更新工单状态
     */
    @ApiOperation("更新工单状态")
    @PreAuthorize("hasAuthority('system:feedback:manage')")
    @PostMapping("/ticket/{ticketId}/status")
    public R<AssistantFeedbackVO> updateTicketStatus(@PathVariable("ticketId") Long ticketId,
                                                     @Validated @RequestBody AssistantTicketStatusUpdateDTO dto) {
        ensureAdmin();
        Long handlerId = getCurrentUserId();
        AssistantFeedbackVO feedback = feedbackService.updateTicketStatus(ticketId, handlerId, dto);
        return R.ok(feedback, "工单状态更新成功");
    }

    /**
     * 追加处理备注（不改变状态）
     */
    @ApiOperation("追加处理备注")
    @PreAuthorize("hasAuthority('system:feedback:manage')")
    @PostMapping("/{id}/remark")
    public R<String> appendRemark(@PathVariable("id") Long feedbackId,
                                  @Validated @RequestBody AssistantTicketStatusUpdateDTO dto) {
        ensureAdmin();
        Long handlerId = getCurrentUserId();
        feedbackService.appendProcessingRemark(feedbackId, handlerId, dto.getRemark());
        return R.ok("备注追加成功");
    }

    /**
     * 修改处理记录备注
     */
    @ApiOperation("修改处理记录备注")
    @PreAuthorize("hasAuthority('system:feedback:manage')")
    @PutMapping("/process-record/{recordId}/remark")
    public R<String> updateProcessRecordRemark(@PathVariable("recordId") Long recordId,
                                               @Validated @RequestBody UpdateRemarkDTO dto) {
        ensureAdmin();
        Long operatorId = getCurrentUserId();
        processRecordService.updateRemark(recordId, dto.getRemark(), operatorId);
        return R.ok("备注修改成功");
    }

    /**
     * 删除反馈（逻辑删除）
     */
    @ApiOperation("删除反馈")
    @PreAuthorize("hasAuthority('system:feedback:manage')")
    @DeleteMapping("/{id}")
    public R<String> deleteFeedback(@PathVariable("id") Long feedbackId) {
        ensureAdmin();
        feedbackService.deleteFeedback(feedbackId);
        return R.ok("删除成功");
    }

    /**
     * 获取当前用户 ID
     */
    private Long getCurrentUserId() {
        try {
            return UserContextUtil.getCurrentUserId();
        } catch (Exception e) {
            throw new ServiceException("获取用户信息失败", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 确保当前用户是管理员（level >= 4）。
     * 当前阶段保留等级校验作为兜底，避免仅凭新权限点放开历史管理员边界。
     */
    private void ensureAdmin() {
        try {
            Integer level = UserContextUtil.getCurrentLevel();
            if (level == null || level < 4) {
                throw new ServiceException("无权限操作", HttpStatus.FORBIDDEN);
            }
        } catch (ServiceException exception) {
            throw exception;
        } catch (Exception e) {
            throw new ServiceException("权限验证失败", HttpStatus.FORBIDDEN);
        }
    }
}
