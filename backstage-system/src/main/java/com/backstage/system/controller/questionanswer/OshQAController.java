package com.backstage.system.controller.questionanswer;

import com.backstage.common.annotation.OshUserEvent;
import com.backstage.common.annotation.OshUserLevel;
import com.backstage.common.constant.ResourceType;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.system.domain.questionanswer.dto.*;
import com.backstage.system.domain.questionanswer.vo.QATagVO;
import com.backstage.system.domain.questionanswer.vo.QueryQuestionDetailVO;
import com.backstage.system.domain.questionanswer.vo.QueryQuestionListVO;
import com.backstage.system.service.questionanswer.IOshQAAnswerService;
import com.backstage.system.service.questionanswer.IOshQAQuestionService;
import com.backstage.system.service.questionanswer.IOshQATagService;
import com.backstage.system.utils.UserContextUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/3/24
 * Time: 21:08
 */
@RestController
@RequestMapping("/api/qna")
public class OshQAController {

    @Autowired
    private IOshQATagService IOshQATagService;
    @Autowired
    private IOshQAQuestionService IOshQAQuestionService;
    @Autowired
    private IOshQAAnswerService iOshQAAnswerService;

    @ApiOperation("标签录入")
    @GetMapping("/tag/search")
    @OshUserEvent(module = "答疑模块", actionType = "查询", description = "标签录入")
    @PreAuthorize("hasAuthority('qna:tag:search')")
    public R<List<QATagVO>> searchTags(@RequestParam(value = "type",required = false) String type) {
        return IOshQATagService.searchTags(type);
    }

    @ApiOperation("新增问题")
    @PostMapping("/question/create")
    @OshUserEvent(module = "答疑模块", actionType = "新增", description = "新增问题", resourceType = ResourceType.QA_TAG_TYPE)
    @PreAuthorize("hasAuthority('qna:question:create')")
    public R<String> addQuestion(
            @RequestBody AddQuestionDTO addQuestionDTO) {
        return IOshQAQuestionService.addQuestion(UserContextUtil.getCurrentUserId(), addQuestionDTO.getResourceNo(),
                addQuestionDTO.getResourceType(), addQuestionDTO.getContent(), addQuestionDTO.getIsPaidOnly(), addQuestionDTO.getTags());
    }

    @ApiOperation("发布问题")
    @PostMapping("/question/publish")
    @OshUserEvent(module = "答疑模块", actionType = "新增", description = "发布问题", resourceType = ResourceType.QA_QUESTION_TYPE)
    @PreAuthorize("hasAuthority('qna:question:publish')")
    public R<String> publishQuestion(
            @RequestBody PublishQuestionDTO publishQuestionDTO) {
        return IOshQAQuestionService.publishQuestion(UserContextUtil.getCurrentUserId(), publishQuestionDTO.getQuestionId());
    }

    @ApiOperation("我的草稿")
    @GetMapping("/question/my/draft")
    @OshUserEvent(module = "答疑模块", actionType = "查询", description = "我的草稿", resourceType = ResourceType.QA_QUESTION_TYPE)
    @PreAuthorize("hasAuthority('qna:question:myDraft')")
    public R<List<QueryQuestionListVO>> myDraft() {
        return IOshQAQuestionService.myDraft(UserContextUtil.getCurrentUserId());
    }

    @ApiOperation("编辑问题")
    @PostMapping("/question/my/draft/edit")
    @OshUserEvent(module = "答疑模块", actionType = "编辑", description = "编辑问题", resourceType = ResourceType.QA_QUESTION_TYPE)
    @PreAuthorize("hasAuthority('qna:question:myDraftEdit')")
    public R<String> editQuestion(
            @RequestBody EditQuestionDTO editQuestionDTO) {
        return IOshQAQuestionService.editQuestion(UserContextUtil.getCurrentUserId(), editQuestionDTO.getQuestionId(), editQuestionDTO.getResourceNo(),
                editQuestionDTO.getResourceType(), editQuestionDTO.getContent(), editQuestionDTO.getIsPaidOnly(), editQuestionDTO.getTags());
    }

    @ApiOperation("删除问题")
    @PostMapping("/question/delete")
    @OshUserEvent(module = "答疑模块", actionType = "删除", description = "删除问题", resourceType = ResourceType.QA_QUESTION_TYPE)
    @PreAuthorize("hasAuthority('qna:question:delete')")
    public R<String> deleteQuestion(
            @RequestBody DeleteQuestionDTO deleteQuestionDTO) {
        return IOshQAQuestionService.deleteQuestion(UserContextUtil.getCurrentUserId(), deleteQuestionDTO.getQuestionId());
    }

    @ApiOperation("关注问题")
    @PostMapping("/question/follow")
    @OshUserEvent(module = "答疑模块", actionType = "关注", description = "关注问题", resourceType = ResourceType.QA_QUESTION_TYPE)
    @PreAuthorize("hasAuthority('qna:question:follow')")
    public R<String> followQuestion(
            @RequestBody FollowQuestionDTO followQuestionDTO) {
        return IOshQAQuestionService.followQuestion(UserContextUtil.getCurrentUserId(), followQuestionDTO.getQuestionId());
    }

    @ApiOperation("取消关注问题")
    @PostMapping("/question/follow/cancel")
    @OshUserEvent(module = "答疑模块", actionType = "取消关注", description = "取消关注问题", resourceType = ResourceType.QA_QUESTION_TYPE)
    @PreAuthorize("hasAuthority('qna:question:cancelFollow')")
    public R<String> cancelFollowQuestion(
            @RequestBody FollowQuestionDTO followQuestionDTO) {
        return IOshQAQuestionService.cancelFollowQuestion(UserContextUtil.getCurrentUserId(), followQuestionDTO.getQuestionId());
    }

    @ApiOperation("问题列表")
    @PostMapping("/question/list")
    @OshUserEvent(module = "答疑模块", actionType = "查询", description = "问题列表", resourceType = ResourceType.QA_QUESTION_TYPE)
    @PreAuthorize("hasAuthority('qna:question:list')")
    @OshUserLevel(value = 5)
    public TableDataInfo list(@RequestBody QueryQuestionListDTO queryQuestionListDTO) {
        return IOshQAQuestionService.list(UserContextUtil.getCurrentUserId(), queryQuestionListDTO.getResourceNo(),
                queryQuestionListDTO.getResourceType(), queryQuestionListDTO.getType(), queryQuestionListDTO.getKeyword(), queryQuestionListDTO.getPageNum(), queryQuestionListDTO.getPageSize());
    }

    @ApiOperation("回答")
    @PostMapping("/answer/createPost")
    @OshUserEvent(module = "答疑模块", actionType = "新增", description = "回答", resourceType = ResourceType.QA_ANSWER_TYPE)
    @PreAuthorize("hasAuthority('qna:answer:create')")
    public R<String> answer(@RequestBody AnswerDTO answerDTO) {
        return iOshQAAnswerService.answer(UserContextUtil.getCurrentUserId(), answerDTO.getQuestionId(), answerDTO.getContent());
    }

    @ApiOperation("采纳回答")
    @PostMapping("/question/solve")
    @OshUserEvent(module = "答疑模块", actionType = "修改", description = "采纳回答", resourceType = ResourceType.QA_ANSWER_TYPE)
    @PreAuthorize("hasAuthority('qna:question:solve')")
    public R<String> solve(@RequestBody SolveQuestionDTO solveQuestionDTO) {
        return IOshQAQuestionService.solve(UserContextUtil.getCurrentUserId(), solveQuestionDTO.getQuestionId(), solveQuestionDTO.getAnswerId());
    }

    @ApiOperation("取消采纳回答")
    @PostMapping("/question/cancel/solve")
    @OshUserEvent(module = "答疑模块", actionType = "修改", description = "取消采纳回答", resourceType = ResourceType.QA_ANSWER_TYPE)
    @PreAuthorize("hasAuthority('qna:question:cancelSolve')")
    public R<String> cancelSolve(@RequestBody SolveQuestionDTO solveQuestionDTO) {
        return IOshQAQuestionService.cancelSolve(UserContextUtil.getCurrentUserId(), solveQuestionDTO.getQuestionId(), solveQuestionDTO.getAnswerId());
    }

    @ApiOperation("问题详情")
    @PostMapping("/question/detail")
    @OshUserEvent(module = "答疑模块", actionType = "查询", description = "查询问题详情", resourceType = ResourceType.QA_QUESTION_TYPE)
    @PreAuthorize("hasAuthority('qna:question:detail')")
    public R<QueryQuestionDetailVO> detail(@RequestBody QueryQuestionDetailDTO queryQuestionDetailDTO) {
        return IOshQAQuestionService.detail(UserContextUtil.getCurrentUserId(), queryQuestionDetailDTO.getQuestionId());
    }

    @ApiOperation("给回答点赞")
    @PostMapping("/answer/vote")
    @OshUserEvent(module = "答疑模块", actionType = "点赞", description = "给回答点赞", resourceType = ResourceType.QA_ANSWER_TYPE)
    @PreAuthorize("hasAuthority('qna:answer:vote')")
    public R<String> vote(@RequestBody AnswerVoteDTO answerVoteDTO) {
        return IOshQAQuestionService.vote(UserContextUtil.getCurrentUserId(), answerVoteDTO.getAnswerId());
    }

    @ApiOperation("取消给回答点赞")
    @PostMapping("/answer/vote/cancel")
    @OshUserEvent(module = "答疑模块", actionType = "取消点赞", description = "取消给回答点赞", resourceType = ResourceType.QA_ANSWER_TYPE)
    @PreAuthorize("hasAuthority('qna:answer:cancelVote')")
    public R<String> cancelVote(@RequestBody AnswerVoteDTO answerVoteDTO) {
        return IOshQAQuestionService.cancelVote(UserContextUtil.getCurrentUserId(), answerVoteDTO.getAnswerId());
    }
}