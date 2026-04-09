package com.backstage.system.controller.questionanswer;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.system.domain.questionanswer.dto.*;
import com.backstage.system.domain.questionanswer.vo.QATagVO;
import com.backstage.system.domain.questionanswer.vo.QueryQuestionDetailVO;
import com.backstage.system.domain.questionanswer.vo.ResourceVO;
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
    private UserContextUtil userContextUtil;
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
        return IOshQAQuestionService.addQuestion(userContextUtil.getCurrentUser(), addQuestionDTO.getResourceNo(),
                addQuestionDTO.getResourceType(), addQuestionDTO.getContent(), addQuestionDTO.getIsPaidOnly(), addQuestionDTO.getTags(), addQuestionDTO.getStatus());
    }

    @Anonymous
    @ApiOperation("发布问题")
    @PostMapping("/question/publish")
    public R<String> publishQuestion(
            @RequestBody PublishQuestionDTO publishQuestionDTO) {
        return IOshQAQuestionService.publishQuestion(userContextUtil.getCurrentUser(), publishQuestionDTO.getQuestionId());
    }

    @Anonymous
    @ApiOperation("删除问题")
    @PostMapping("/question/delete")
    public R<String> deleteQuestion(
            @RequestBody DeleteQuestionDTO deleteQuestionDTO) {
        return IOshQAQuestionService.deleteQuestion(userContextUtil.getCurrentUser(), deleteQuestionDTO.getQuestionId());
    }

    @Anonymous
    @ApiOperation("关注问题")
    @PostMapping("/question/follow")
    public R<String> followQuestion(
            @RequestBody FollowQuestionDTO followQuestionDTO) {
        return IOshQAQuestionService.followQuestion(userContextUtil.getCurrentUser(), followQuestionDTO.getQuestionId());
    }

    @Anonymous
    @ApiOperation("取消关注问题")
    @PostMapping("/question/cancel/follow")
    public R<String> cancelFollowQuestion(
            @RequestBody FollowQuestionDTO followQuestionDTO) {
        return IOshQAQuestionService.cancelFollowQuestion(userContextUtil.getCurrentUser(), followQuestionDTO.getQuestionId());
    }

    @Anonymous
    @ApiOperation("问题列表")
    @GetMapping("/question/list")
    public TableDataInfo list(@RequestBody QueryQuestionListDTO queryQuestionListDTO) {
        return IOshQAQuestionService.list(userContextUtil.getCurrentUser().getId(), queryQuestionListDTO.getResourceNo(),
                queryQuestionListDTO.getResourceType(), queryQuestionListDTO.getType(), queryQuestionListDTO.getKeyword(), queryQuestionListDTO.getPageNum(), queryQuestionListDTO.getPageSize());
    }

    @Anonymous
    @ApiOperation("回答")
    @PostMapping("/answer/createPost")
    public R<String> answer(@RequestBody AnswerDTO answerDTO) {
        return iOshQAAnswerService.answer(userContextUtil.getCurrentUser(), answerDTO.getQuestionId(), answerDTO.getContent());
    }

    @Anonymous
    @ApiOperation("采纳回答")
    @PostMapping("/question/solve")
    public R<String> solve(@RequestBody SolveQuestionDTO solveQuestionDTO) {
        return IOshQAQuestionService.solve(userContextUtil.getCurrentUser(), solveQuestionDTO.getQuestionId(), solveQuestionDTO.getAnswerId());
    }

    @Anonymous
    @ApiOperation("取消采纳回答")
    @PostMapping("/question/cancel/solve")
    public R<String> cancelSolve(@RequestBody SolveQuestionDTO solveQuestionDTO) {
        return IOshQAQuestionService.cancelSolve(userContextUtil.getCurrentUser(), solveQuestionDTO.getQuestionId(), solveQuestionDTO.getAnswerId());
    }

    @Anonymous
    @ApiOperation("问题详情")
    @GetMapping("/question/detail")
    public R<QueryQuestionDetailVO> detail(@RequestBody QueryQuestionDetailDTO queryQuestionDetailDTO) {
        return IOshQAQuestionService.detail(userContextUtil.getCurrentUser().getId(), queryQuestionDetailDTO.getQuestionId());
    }

    @Anonymous
    @ApiOperation("给回答点赞")
    @PostMapping("/answer/vote")
    public R<String> vote(@RequestBody AnswerVoteDTO answerVoteDTO) {
        return IOshQAQuestionService.vote(userContextUtil.getCurrentUser(), answerVoteDTO.getAnswerId());
    }

    @Anonymous
    @ApiOperation("取消给回答点赞")
    @PostMapping("/answer/cancel/vote")
    public R<String> cancelVote(@RequestBody AnswerVoteDTO answerVoteDTO) {
        return IOshQAQuestionService.cancelVote(userContextUtil.getCurrentUser(), answerVoteDTO.getAnswerId());
    }
}