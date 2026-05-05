package com.backstage.system.controller.assistant;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.constant.HttpStatus;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.assistant.dto.AssistantCourseQaAskDTO;
import com.backstage.system.domain.assistant.dto.AssistantFeedbackCommentDTO;
import com.backstage.system.domain.assistant.dto.AssistantFeedbackCreateDTO;
import com.backstage.system.domain.assistant.dto.AssistantFeedbackPageDTO;
import com.backstage.system.domain.assistant.dto.AssistantSiteQaAskDTO;
import com.backstage.system.domain.assistant.vo.AssistantAnswerVO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackVO;
import com.backstage.system.domain.assistant.vo.AssistantInitVO;
import com.backstage.system.service.assistant.IAssistantFeedbackCommentService;
import com.backstage.system.service.assistant.IAssistantFeedbackService;
import com.backstage.system.service.assistant.IAssistantService;
import com.backstage.system.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pc/assistant")
public class AssistantController extends BaseController {

    private static final int VIP_LEVEL = 3;

    private final IAssistantService assistantService;

    private final IAssistantFeedbackService assistantFeedbackService;

    private final IAssistantFeedbackCommentService assistantFeedbackCommentService;

    @Anonymous
    @GetMapping("/init")
    public R<AssistantInitVO> init(@RequestParam(value = "courseId", required = false) Long courseId) {
        return R.ok(assistantService.getInit(courseId, safeCurrentUserId(), safeCurrentUserLevel()));
    }

    //    TODO 等RAG 课程好了之后替换掉这个接口
    @Anonymous
    @PostMapping("/site-qa/ask")
    public R<AssistantAnswerVO> askSiteQuestion(@Validated @RequestBody AssistantSiteQaAskDTO dto) {
        return R.ok(assistantService.answerSiteQuestion(dto.getQuestion()));
    }

    @PostMapping("/course-qa/ask")
    public R<AssistantAnswerVO> askCourseQuestion(@Validated @RequestBody AssistantCourseQaAskDTO dto) {
        Long userId = safeCurrentUserId();
        Integer userLevel = safeCurrentUserLevel();
        AssistantInitVO init = assistantService.getInit(dto.getCourseId(), userId, userLevel);

        if (userId == null) {
            return R.fail(HttpStatus.UNAUTHORIZED, "请先登录后再使用课程问答");
        }
        if (!Boolean.TRUE.equals(init.getCourseQaEnabled())) {
            return R.fail(HttpStatus.FORBIDDEN, init.getCourseQaReason());
        }

        return R.ok(assistantService.answerCourseQuestion(dto.getCourseId(), dto.getQuestion()));
    }

    @PostMapping("/feedback/create")
    public R<AssistantFeedbackVO> createFeedback(@Validated @RequestBody AssistantFeedbackCreateDTO dto) {
        Long userId = safeCurrentUserId();
        if (userId == null) {
            return R.fail(HttpStatus.UNAUTHORIZED, "请先登录后再提交反馈");
        }
        return R.ok(assistantFeedbackService.createFeedback(userId, dto), "提交成功");
    }

    @PostMapping("/feedback/page")
    public TableDataInfo pageFeedback(@RequestBody AssistantFeedbackPageDTO dto) {
        Long userId = safeCurrentUserId();
        if (userId == null) {
            throw new ServiceException("请先登录后查看反馈", HttpStatus.UNAUTHORIZED);
        }
        dto.setUserId(userId);
        return assistantFeedbackService.pageFeedbackList(dto);
    }

    @PostMapping("/feedback/{id}/comment")
    public R<Long> createComment(@PathVariable("id") Long feedbackId,
                                  @Validated @RequestBody AssistantFeedbackCommentDTO dto) {
        Long userId = safeCurrentUserId();
        if (userId == null) {
            return R.fail(HttpStatus.UNAUTHORIZED, "请先登录后再发表评论");
        }
        
        // 从路径参数设置反馈ID
        dto.setFeedbackId(feedbackId);
        
        Long commentId = assistantFeedbackCommentService.createComment(dto, userId);
        return R.ok(commentId, "评论成功");
    }

    private Long safeCurrentUserId() {
        try {
            return UserContextUtil.getCurrentUserId();
        } catch (Exception ignore) {
            return null;
        }
    }

    private Integer safeCurrentUserLevel() {
        try {
            return UserContextUtil.getCurrentLevel();
        } catch (Exception ignore) {
            return 0;
        }
    }
}
