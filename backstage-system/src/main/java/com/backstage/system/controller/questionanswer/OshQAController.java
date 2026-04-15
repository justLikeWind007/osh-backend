package com.backstage.system.controller.questionanswer;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.OshUserActionLog;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
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

    @Anonymous
    @ApiOperation("标签录入")
    @GetMapping("/tag/search")
    public R<List<QATagVO>> searchTags(@RequestParam(value = "type",required = false) String type) {
        return IOshQATagService.searchTags(type);
    }

    @Anonymous
    @ApiOperation("新增问题")
    @PostMapping("/question/create")
    public R<String> addQuestion(
            @RequestBody AddQuestionDTO addQuestionDTO) {
        return IOshQAQuestionService.addQuestion(UserContextUtil.getCurrentUserId(), addQuestionDTO.getResourceNo(),
                addQuestionDTO.getResourceType(), addQuestionDTO.getContent(), addQuestionDTO.getIsPaidOnly(), addQuestionDTO.getTags());
    }

    @Anonymous
    @ApiOperation("发布问题")
    @PostMapping("/question/publish")
    public R<String> publishQuestion(
            @RequestBody PublishQuestionDTO publishQuestionDTO) {
        return IOshQAQuestionService.publishQuestion(UserContextUtil.getCurrentUserId(), publishQuestionDTO.getQuestionId());
    }

    @Anonymous
    @ApiOperation("我的草稿")
    @GetMapping("/question/my/draft")
    public R<List<QueryQuestionListVO>> myDraft() {
        return IOshQAQuestionService.myDraft(UserContextUtil.getCurrentUserId());
    }

    @Anonymous
    @ApiOperation("编辑问题")
    @PostMapping("/question/my/draft/edit")
    public R<String> editQuestion(
            @RequestBody EditQuestionDTO editQuestionDTO) {
        return IOshQAQuestionService.editQuestion(UserContextUtil.getCurrentUserId(), editQuestionDTO.getQuestionId(), editQuestionDTO.getResourceNo(),
                editQuestionDTO.getResourceType(), editQuestionDTO.getContent(), editQuestionDTO.getIsPaidOnly(), editQuestionDTO.getTags());
    }

    @Anonymous
    @ApiOperation("删除问题")
    @PostMapping("/question/delete")
    public R<String> deleteQuestion(
            @RequestBody DeleteQuestionDTO deleteQuestionDTO) {
        return IOshQAQuestionService.deleteQuestion(UserContextUtil.getCurrentUserId(), deleteQuestionDTO.getQuestionId());
    }

    @Anonymous
    @ApiOperation("关注问题")
    @PostMapping("/question/follow")
    public R<String> followQuestion(
            @RequestBody FollowQuestionDTO followQuestionDTO) {
        return IOshQAQuestionService.followQuestion(UserContextUtil.getCurrentUserId(), followQuestionDTO.getQuestionId());
    }

    @Anonymous
    @ApiOperation("取消关注问题")
    @PostMapping("/question/follow/cancel")
    public R<String> cancelFollowQuestion(
            @RequestBody FollowQuestionDTO followQuestionDTO) {
        return IOshQAQuestionService.cancelFollowQuestion(UserContextUtil.getCurrentUserId(), followQuestionDTO.getQuestionId());
    }

    @Anonymous
    @ApiOperation("问题列表")
    @PostMapping("/question/list")
    public TableDataInfo list(@RequestBody QueryQuestionListDTO queryQuestionListDTO) {
        return IOshQAQuestionService.list(UserContextUtil.getCurrentUserId(), queryQuestionListDTO.getResourceNo(),
                queryQuestionListDTO.getResourceType(), queryQuestionListDTO.getType(), queryQuestionListDTO.getKeyword(), queryQuestionListDTO.getPageNum(), queryQuestionListDTO.getPageSize());
    }

    @Anonymous
    @ApiOperation("回答")
    @PostMapping("/answer/createPost")
    public R<String> answer(@RequestBody AnswerDTO answerDTO) {
        return iOshQAAnswerService.answer(UserContextUtil.getCurrentUserId(), answerDTO.getQuestionId(), answerDTO.getContent());
    }

    @Anonymous
    @ApiOperation("采纳回答")
    @PostMapping("/question/solve")
    public R<String> solve(@RequestBody SolveQuestionDTO solveQuestionDTO) {
        return IOshQAQuestionService.solve(UserContextUtil.getCurrentUserId(), solveQuestionDTO.getQuestionId(), solveQuestionDTO.getAnswerId());
    }

    @Anonymous
    @ApiOperation("取消采纳回答")
    @PostMapping("/question/cancel/solve")
    public R<String> cancelSolve(@RequestBody SolveQuestionDTO solveQuestionDTO) {
        return IOshQAQuestionService.cancelSolve(UserContextUtil.getCurrentUserId(), solveQuestionDTO.getQuestionId(), solveQuestionDTO.getAnswerId());
    }

    @Anonymous
    @ApiOperation("问题详情")
    @PostMapping("/question/detail")
    @OshUserActionLog(module = "答疑模块", actionType = "查询", description = "查询问题详情")
    public R<QueryQuestionDetailVO> detail(@RequestBody QueryQuestionDetailDTO queryQuestionDetailDTO) {
        return IOshQAQuestionService.detail(UserContextUtil.getCurrentUserId(), queryQuestionDetailDTO.getQuestionId());
    }

    @Anonymous
    @ApiOperation("给回答点赞")
    @PostMapping("/answer/vote")
    public R<String> vote(@RequestBody AnswerVoteDTO answerVoteDTO) {
        return IOshQAQuestionService.vote(UserContextUtil.getCurrentUserId(), answerVoteDTO.getAnswerId());
    }

    @Anonymous
    @ApiOperation("取消给回答点赞")
    @PostMapping("/answer/vote/cancel")
    public R<String> cancelVote(@RequestBody AnswerVoteDTO answerVoteDTO) {
        return IOshQAQuestionService.cancelVote(UserContextUtil.getCurrentUserId(), answerVoteDTO.getAnswerId());
    }
}