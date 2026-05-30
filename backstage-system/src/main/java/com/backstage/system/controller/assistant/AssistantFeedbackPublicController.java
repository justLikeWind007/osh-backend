package com.backstage.system.controller.assistant;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.enums.AnnouncementChannelEnum;
import com.backstage.common.enums.AnnouncementModuleEnum;
import com.backstage.system.domain.announcement.vo.AnnouncementMarqueeVO;
import com.backstage.system.domain.assistant.dto.AssistantFeedbackPageDTO;
import com.backstage.system.domain.assistant.vo.*;
import com.backstage.system.service.announcement.IAnnouncementMarqueeQueryService;
import com.backstage.system.service.assistant.IAssistantFeedbackCategoryService;
import com.backstage.system.service.assistant.IAssistantFeedbackCommentService;
import com.backstage.system.service.assistant.IAssistantFeedbackService;
import com.backstage.system.service.assistant.IAssistantFeedbackTagService;
import com.backstage.system.utils.UserContextUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI 助手反馈公开接口 Controller（公开接口所以家@Anonymous）
 *
 * @author backstage
 */
@Anonymous
@Api(tags = "AI助手反馈-公开接口")
@RestController
@RequestMapping("/pc/public/feedback")
public class AssistantFeedbackPublicController extends BaseController {

    public AssistantFeedbackPublicController(IAssistantFeedbackCategoryService categoryService, IAssistantFeedbackService feedbackService, IAssistantFeedbackCommentService commentService, IAssistantFeedbackTagService feedbackTagService, IAnnouncementMarqueeQueryService announcementMarqueeQueryService) {
        this.categoryService = categoryService;
        this.feedbackService = feedbackService;
        this.commentService = commentService;
        this.feedbackTagService = feedbackTagService;
        this.announcementMarqueeQueryService = announcementMarqueeQueryService;
    }

    private final IAssistantFeedbackCategoryService categoryService;
    private final IAssistantFeedbackService feedbackService;
    private final IAssistantFeedbackCommentService commentService;
    private final IAssistantFeedbackTagService feedbackTagService;
    private final IAnnouncementMarqueeQueryService announcementMarqueeQueryService;

    /**
     * 反馈公告列表。
     * <p>前端固定渲染为不可点击的纯文本跑马灯。</p>
     */
    @ApiOperation("查询反馈公告")
    @GetMapping("/announcement/list")
    public R<List<AnnouncementMarqueeVO>> listAnnouncements(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        return R.ok(announcementMarqueeQueryService.list(
                AnnouncementModuleEnum.FEEDBACK, AnnouncementChannelEnum.SYSTEM_NOTICE, limit));
    }

    /**
     * 获取反馈分类列表（用户可见）
     */
    @ApiOperation("获取反馈分类列表")
    @GetMapping("/category/list")
    public R<List<AssistantFeedbackCategoryVO>> listCategories() {
        List<AssistantFeedbackCategoryVO> categories = categoryService.listUserCategories();
        return R.ok(categories);
    }

    /**
     * 获取反馈标签列表
     */
    @ApiOperation("获取反馈标签列表")
    @GetMapping("/tag/list")
    public R<List<AssistantFeedbackTagVO>> listTags(@RequestParam(required = false) String keyword) {
        return R.ok(feedbackTagService.listEnabledTags(keyword));
    }

    /**
     * 分页查询反馈列表
     */
    @ApiOperation("分页查询反馈列表")
    @PostMapping("/page")
    public TableDataInfo pageFeedback(@RequestBody AssistantFeedbackPageDTO dto) {
        dto.setUserId(safeCurrentUserId());
        return feedbackService.pageFeedbackList(dto);
    }

    /**
     * 获取反馈详情
     */
    @ApiOperation("获取反馈详情")
    @GetMapping("/detail/{id}")
    public R<AssistantFeedbackDetailVO> getFeedbackDetail(@PathVariable("id") Long id) {
        AssistantFeedbackDetailVO detail = feedbackService.getFeedbackDetail(id);
        return R.ok(detail);
    }

    @ApiOperation("获取工单状态摘要")
    @GetMapping("/status-summary/{id}")
    public R<AssistantFeedbackDetailVO> getFeedbackStatusSummary(@PathVariable("id") Long id) {
        return R.ok(feedbackService.getFeedbackStatusSummary(id));
    }

    /**
     * 获取反馈处理记录时间线
     */
    @ApiOperation("获取反馈处理记录")
    @GetMapping("/{id}/process-record/list")
    public R<List<AssistantFeedbackProcessRecordVO>> listProcessRecords(@PathVariable("id") Long feedbackId) {
        return R.ok(feedbackService.listFeedbackProcessRecords(feedbackId));
    }

    /**
     * 分页查询反馈的评论列表
     */
    @ApiOperation("分页查询反馈的评论列表")
    @GetMapping("/{id}/comment/list")
    public R<List<AssistantFeedbackCommentVO>> listComments(
            @PathVariable("id") Long feedbackId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        List<AssistantFeedbackCommentVO> comments = commentService.listCommentsByFeedbackId(feedbackId, pageNum, pageSize);
        return R.ok(comments);
    }

    private Long safeCurrentUserId() {
        try {
            return UserContextUtil.getCurrentUserId();
        } catch (Exception ignore) {
            return null;
        }
    }
}
