package com.backstage.system.service.questionanswer.impl;

import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.enums.QAQuestionSearchType;
import com.backstage.common.enums.ResultCode;
import com.backstage.system.domain.questionanswer.Answer;
import com.backstage.system.domain.questionanswer.Question;
import com.backstage.system.domain.questionanswer.Tag;
import com.backstage.system.domain.questionanswer.vo.QueryQuestionDetailVO;
import com.backstage.system.domain.questionanswer.vo.QueryQuestionListVO;
import com.backstage.system.domain.user.User;
import com.backstage.system.mapper.questionanswer.OshQAAnswerMapper;
import com.backstage.system.mapper.questionanswer.OshQAQuestionMapper;
import com.backstage.system.mapper.questionanswer.OshQATagMapper;
import com.backstage.system.service.questionanswer.IOshQAQuestionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/24
 * Time: 21:50
 */
@Service
public class OshQAQuestionServiceImpl implements IOshQAQuestionService {

    @Autowired
    private OshQAQuestionMapper oshQaQuestionMapper;

    @Autowired
    private OshQAAnswerMapper oshQaAnswerMapper;

    @Autowired
    private OshQATagMapper oshQaTagMapper;




    @Override
    public R<String> addQuestion(User user, Long resourceNo, String resourceType, String content, Byte isPaidOnly, List<Long> tags, Byte status) {
        // todo 用户权限校验
        if ((isPaidOnly == 1) && !checkAddPermission(user.getId(), resourceNo)) return R.fail(ResultCode.FAILED_USER_PERMISSION_DENIED.getMsg());
        Question question = new Question();
        question.setUserId(user.getId());
        question.setResourceNo(resourceNo);
        question.setResourceType(resourceType);
        question.setContent(content);
        question.setIsPaidOnly(isPaidOnly);
        question.setStatus(status);
        question.setCreateBy(user.getUsername());
        question.setCreateTime(LocalDateTime.now());
        oshQaQuestionMapper.insert(question);
        for (Long tagId : tags) {
            oshQaQuestionMapper.addQuestionTags(question.getId(), tagId);
        }
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    private Boolean checkAddPermission(Long userId, Long resourceNo) {
        // todo 用户权限校验：判断用户是否购买该资源或者是不是vip用户

        return true;
    }

    @Override
    public R<String> publishQuestion(User user, Long questionId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<Question>().select(Question::getUserId).eq(Question::getId, questionId);
        Question question = oshQaQuestionMapper.selectOne(wrapper);
        if (question.getUserId() == null || !question.getUserId().equals(user.getId())) {
            return R.fail(ResultCode.FAILED_USER_PERMISSION_DENIED.getMsg());
        }
        question.setStatus((byte) 1);
        question.setUpdateBy(user.getUsername());
        question.setUpdateTime(LocalDateTime.now());
        oshQaQuestionMapper.update(question, new LambdaQueryWrapper<Question>().eq(Question::getId, questionId));
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> deleteQuestion(User user, Long questionId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<Question>().select(Question::getUserId).eq(Question::getId, questionId);
        Question question = oshQaQuestionMapper.selectOne(wrapper);
        if (question.getUserId() == null || !question.getUserId().equals(user.getId())) {
            return R.fail(ResultCode.FAILED_USER_PERMISSION_DENIED.getMsg());
        }
        question.setDelete_flag((byte) 1);
        question.setUpdateBy(user.getUsername());
        question.setUpdateTime(LocalDateTime.now());
        oshQaQuestionMapper.update(question, new LambdaQueryWrapper<Question>().eq(Question::getId, questionId));
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> followQuestion(User user, Long questionId) {
        oshQaQuestionMapper.followQuestion(user.getId(), questionId, user.getUsername());
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> cancelFollowQuestion(User user, Long questionId) {
        oshQaQuestionMapper.cancelFollowQuestion(user.getId(), questionId, user.getUsername());
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public TableDataInfo list(Long userId, Long resourceNo, String resourceType, String type, String keyword, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<Question>()
                .eq(resourceNo != null, Question::getResourceNo, resourceNo)
                .eq(resourceType != null, Question::getResourceType, resourceType)
                .like(keyword != null, Question::getContent, keyword)
                .orderByDesc(Question::getViewCount);
        if (type.equals(QAQuestionSearchType.MY_QUESTIONS.getType())) {
            wrapper.eq(Question::getUserId, userId);
        } else if (type.equals(QAQuestionSearchType.MY_FOLLOWS.getType())) {
            List<Long> followQuestionIds = oshQaQuestionMapper.getFollowQuestionIds(userId);
            if (CollectionUtils.isNotEmpty(followQuestionIds)) {
                wrapper.in(Question::getId, followQuestionIds);
            } else {
                wrapper.eq(Question::getId, -1L);
            }
        } else if (type.equals(QAQuestionSearchType.UNANSWERED.getType())) {
            wrapper.eq(Question::getStatus, (byte)1);
        } else if (type.equals(QAQuestionSearchType.ANSWERED.getType())) {
            wrapper.eq(Question::getStatus, (byte)2);
        }
        PageHelper.startPage(pageNum, pageSize);
        List<QueryQuestionListVO> queryQuestionListVOS = new ArrayList<>();
        List<Question> questions = oshQaQuestionMapper.selectList(wrapper);
        for (Question question : questions) {
            QueryQuestionListVO queryQuestionListVO = new QueryQuestionListVO();
            BeanUtils.copyProperties(question, queryQuestionListVO);
            queryQuestionListVOS.add(queryQuestionListVO);
        }
        PageInfo<QueryQuestionListVO> pageInfo = new PageInfo<>(queryQuestionListVOS);
        return new TableDataInfo(pageInfo.getList(), pageInfo.getTotal());
    }

    @Override
    public R<String> solve(User user, Long questionId, Long answerId) {
        LambdaQueryWrapper<Answer> answerWrapper = new LambdaQueryWrapper<Answer>()
                .eq(Answer::getId, answerId);
        Answer answer = oshQaAnswerMapper.selectOne(answerWrapper);
        if (answer == null) {
            return R.fail(ResultCode.FAILED_NOT_EXISTS.getMsg());
        }
        if (!answer.getQuestionId().equals(questionId)) {
            return R.fail(ResultCode.FAILED.getMsg());
        }
        LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<Question>()
                .eq(Question::getId, questionId);
        Question question = oshQaQuestionMapper.selectOne(questionWrapper);
        if (question == null) {
            return R.fail(ResultCode.FAILED_NOT_EXISTS.getMsg());
        }
        if (!question.getUserId().equals(user.getId())) {
            return R.fail(ResultCode.FAILED_USER_PERMISSION_DENIED.getMsg());
        }
        answer.setIsSolution((byte)1);
        answer.setUpdateBy(user.getUsername());
        answer.setUpdateTime(LocalDateTime.now());
        oshQaAnswerMapper.update(answer, answerWrapper);
        question.setStatus((byte)2);
        question.setUpdateBy(user.getUsername());
        question.setUpdateTime(LocalDateTime.now());
        oshQaQuestionMapper.update(question, questionWrapper);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> cancelSolve(User user, Long questionId, Long answerId) {
        LambdaQueryWrapper<Answer> answerWrapper = new LambdaQueryWrapper<Answer>()
                .eq(Answer::getId, answerId);
        Answer answer = oshQaAnswerMapper.selectOne(answerWrapper);
        if (answer == null) {
            return R.fail(ResultCode.FAILED_NOT_EXISTS.getMsg());
        }
        LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<Question>()
                .eq(Question::getId, questionId);
        Question question = oshQaQuestionMapper.selectOne(questionWrapper);
        if (question == null) {
            return R.fail(ResultCode.FAILED_NOT_EXISTS.getMsg());
        }

        answer.setIsSolution((byte)0);
        answer.setUpdateBy(user.getUsername());
        answer.setUpdateTime(LocalDateTime.now());
        oshQaAnswerMapper.update(answer, answerWrapper);
        question.setStatus((byte)1);
        question.setUpdateBy(user.getUsername());
        question.setUpdateTime(LocalDateTime.now());
        oshQaQuestionMapper.update(question, questionWrapper);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<QueryQuestionDetailVO> detail(Long id, Long questionId) {
        // todo 权限校验
        QueryQuestionDetailVO queryQuestionDetailVO = new QueryQuestionDetailVO();
        LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<Question>()
                .eq(Question::getId, questionId);
        Question question = oshQaQuestionMapper.selectOne(questionWrapper);
        if (question == null) {
            return R.fail(ResultCode.FAILED_NOT_EXISTS.getMsg());
        }
        BeanUtils.copyProperties(question, queryQuestionDetailVO);
        List<Long> tagIds = oshQaTagMapper.selectTagIdsByQuestionId(questionId);
        List<Tag> tags = null;
        if (!CollectionUtils.isEmpty(tagIds)) {
            tags = oshQaTagMapper.selectList(new LambdaQueryWrapper<Tag>().in(Tag::getId, tagIds));
        }
        if (tags != null) {
            ArrayList<String> tagNames = new ArrayList<>();
            for (Tag tag : tags) {
                tagNames.add(tag.getName());
            }
            queryQuestionDetailVO.setTags(tagNames);
        } else {
            queryQuestionDetailVO.setTags(null);
        }
        List<Answer> answers = oshQaAnswerMapper.selectList(new LambdaQueryWrapper<Answer>()
                .eq(Answer::getQuestionId, questionId)
                .orderByDesc(Answer::getIsSolution)
                .orderByDesc(Answer::getVoteCount));
        queryQuestionDetailVO.setAnswers(answers);
        question.setViewCount(question.getViewCount() + 1);
        oshQaQuestionMapper.update(question, questionWrapper);
        return R.ok(queryQuestionDetailVO);
    }

    @Override
    public R<String> vote(Long id, Long answerId) {
        LambdaQueryWrapper<Answer> answerWrapper = new LambdaQueryWrapper<Answer>()
                .eq(Answer::getId, answerId);
        Answer answer = oshQaAnswerMapper.selectOne(answerWrapper);
        if (answer == null) {
            return R.fail(ResultCode.FAILED_NOT_EXISTS.getMsg());
        }
        answer.setVoteCount(answer.getVoteCount() + 1);
        oshQaAnswerMapper.update(answer, answerWrapper);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> cancelVote(Long id, Long answerId) {
        LambdaQueryWrapper<Answer> answerWrapper = new LambdaQueryWrapper<Answer>()
                .eq(Answer::getId, answerId);
        Answer answer = oshQaAnswerMapper.selectOne(answerWrapper);
        if (answer == null) {
            return R.fail(ResultCode.FAILED_NOT_EXISTS.getMsg());
        }
        answer.setVoteCount(answer.getVoteCount() - 1);
        oshQaAnswerMapper.update(answer, answerWrapper);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }
}