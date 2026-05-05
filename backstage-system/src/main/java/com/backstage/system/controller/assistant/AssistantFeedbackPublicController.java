package com.backstage.system.controller.assistant;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.system.domain.assistant.dto.AssistantFeedbackPageDTO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackCategoryVO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackCommentVO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackDetailVO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackProcessRecordVO;
import com.backstage.system.service.assistant.IAssistantFeedbackCategoryService;
import com.backstage.system.service.assistant.IAssistantFeedbackCommentService;
import com.backstage.system.service.assistant.IAssistantFeedbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AssistantFeedbackPublicController extends BaseController {

    private final IAssistantFeedbackCategoryService categoryService;
    private final IAssistantFeedbackService feedbackService;
    private final IAssistantFeedbackCommentService commentService;

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
     * 分页查询反馈列表
     */
    @ApiOperation("分页查询反馈列表")
    @PostMapping("/page")
    public TableDataInfo pageFeedback(@RequestBody AssistantFeedbackPageDTO dto) {
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
}
