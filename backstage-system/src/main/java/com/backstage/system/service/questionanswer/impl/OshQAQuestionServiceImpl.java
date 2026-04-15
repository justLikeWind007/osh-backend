package com.backstage.system.service.questionanswer.impl;

import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.enums.QAQuestionSearchType;
import com.backstage.common.enums.ResultCode;
import com.backstage.common.utils.StringUtils;
import com.backstage.system.domain.questionanswer.Answer;
import com.backstage.system.domain.questionanswer.Question;
import com.backstage.system.domain.questionanswer.Tag;
import com.backstage.system.domain.questionanswer.vo.QueryQuestionDetailVO;
import com.backstage.system.domain.questionanswer.vo.QueryQuestionListVO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public R<String> addQuestion(Long userId, Long resourceNo, String resourceType, String content, Byte isPaidOnly, List<Long> tags) {
        Question question = new Question();
        question.setUserId(userId);
        question.setResourceNo(resourceNo);
        question.setResourceType(resourceType);
        question.setContent(content);
        question.setIsPaidOnly(isPaidOnly);

        // --- 新增下面这两行，修复 Column 'create_by' cannot be null ---
        question.setCreateBy(userId);
        question.setUpdateBy(userId);
        // -------------------------------------------------------

        oshQaQuestionMapper.insert(question);

        if (tags != null && !tags.isEmpty()) {
            for (Long tagId : tags) {
                // 这里你原本就传了userId作为标签的创建者，逻辑是对的
                oshQaQuestionMapper.addQuestionTags(question.getId(), tagId, userId);
            }
        }
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> publishQuestion(Long userId, Long questionId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<Question>().select(Question::getUserId).eq(Question::getId, questionId);
        Question question = oshQaQuestionMapper.selectOne(wrapper);
        if (question.getUserId() == null || !question.getUserId().equals(userId)) {
            return R.fail(ResultCode.FAILED_USER_PERMISSION_DENIED.getMsg());
        }
        question.setStatus((byte) 1);
        oshQaQuestionMapper.update(question, new LambdaQueryWrapper<Question>().eq(Question::getId, questionId));
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<List<QueryQuestionListVO>> myDraft(Long userId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<Question>().eq(Question::getUserId, userId).eq(Question::getStatus, 0);
        List<Question> questions = oshQaQuestionMapper.selectList(wrapper);
        List<QueryQuestionListVO> queryQuestionListVOS = new ArrayList<>();
        for (Question question : questions) {
            QueryQuestionListVO queryQuestionListVO = new QueryQuestionListVO();
            BeanUtils.copyProperties(question, queryQuestionListVO);
            queryQuestionListVOS.add(queryQuestionListVO);
        }
        return R.ok(queryQuestionListVOS);
    }

    @Override
    public R<String> editQuestion(Long userId, Long questionId, Long resourceNo, String resourceType, String content, Byte isPaidOnly, List<Long> tags) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<Question>().select(Question::getUserId).eq(Question::getId, questionId);
        Question question = oshQaQuestionMapper.selectOne(wrapper);
        if (question == null) {
            return R.fail(ResultCode.FAILED_NOT_EXISTS.getMsg());
        }
        if (question.getUserId() == null || !question.getUserId().equals(userId)) {
            return R.fail(ResultCode.FAILED_USER_PERMISSION_DENIED.getMsg());
        }
        question.setResourceNo(resourceNo);
        question.setResourceType(resourceType);
        question.setContent(content);
        question.setIsPaidOnly(isPaidOnly);
        oshQaQuestionMapper.update(question, new LambdaQueryWrapper<Question>().eq(Question::getId, questionId));
        List<Long> tagIds = oshQaTagMapper.selectTagIdsByQuestionId(questionId);
        if (CollectionUtils.isNotEmpty(tagIds) && !(tagIds.equals(tags))) {
            oshQaQuestionMapper.deleteQuestionTags(questionId);
            if (CollectionUtils.isNotEmpty(tags)) {
                for (Long tagId : tags) {
                    oshQaQuestionMapper.addQuestionTags(question.getId(), tagId, userId);
                }
            }
        }
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> deleteQuestion(Long userId, Long questionId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<Question>().select(Question::getUserId).eq(Question::getId, questionId);
        Question question = oshQaQuestionMapper.selectOne(wrapper);
        if (question.getUserId() == null || !question.getUserId().equals(userId)) {
            return R.fail(ResultCode.FAILED_USER_PERMISSION_DENIED.getMsg());
        }
        question.setDelete_flag((byte) 1);
        oshQaQuestionMapper.update(question, new LambdaQueryWrapper<Question>().eq(Question::getId, questionId));
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> followQuestion(Long userId, Long questionId) {
        Integer deleteFlag = oshQaQuestionMapper.getFollowInfoByUserIdAndQuestionId(userId, questionId);
        if (deleteFlag != null && deleteFlag == 0) {
            return R.fail(ResultCode.FAILED.getMsg());
        }
        LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<Question>()
                .eq(Question::getId, questionId);
        Question question = oshQaQuestionMapper.selectOne(questionWrapper);
        if (question == null) {
            return R.fail(ResultCode.FAILED_NOT_EXISTS.getMsg());
        }
        oshQaQuestionMapper.followQuestion(userId, questionId, userId);
        question.setFollowCount(question.getFollowCount() + 1);
        oshQaQuestionMapper.update(question, questionWrapper);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> cancelFollowQuestion(Long userId, Long questionId) {
        Integer deleteFlag = oshQaQuestionMapper.getFollowInfoByUserIdAndQuestionId(userId, questionId);
        if (deleteFlag != null && deleteFlag == 0) {
            LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<Question>()
                    .eq(Question::getId, questionId);
            Question question = oshQaQuestionMapper.selectOne(questionWrapper);
            if (question == null) {
                return R.fail(ResultCode.FAILED_NOT_EXISTS.getMsg());
            }
            oshQaQuestionMapper.cancelFollowQuestion(userId, questionId ,userId);
            question.setFollowCount(question.getFollowCount() - 1);
            oshQaQuestionMapper.update(question, questionWrapper);
            return R.ok(ResultCode.SUCCESS.getMsg());
        }
        return R.fail(ResultCode.FAILED.getMsg());
    }

    @Override
    public TableDataInfo list(Long userId, Long resourceNo, String resourceType, String type, String keyword, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<Question>()
                // 1. 必须加：只查未删除的数据
                .eq(Question::getDeleteFlag, 0)
                .eq(resourceNo != null, Question::getResourceNo, resourceNo)
                .eq(StringUtils.isNotEmpty(resourceType), Question::getResourceType, resourceType)
                .like(StringUtils.isNotEmpty(keyword), Question::getContent, keyword)
                .orderByDesc(Question::getViewCount);

        // 2. 这里的 type 判空处理
        if (StringUtils.isNotEmpty(type)) {
            if (QAQuestionSearchType.MY_QUESTIONS.getType().equals(type)) {
                wrapper.eq(Question::getUserId, userId);
            } else if (QAQuestionSearchType.MY_FOLLOWS.getType().equals(type)) {
                List<Long> followQuestionIds = oshQaQuestionMapper.getFollowQuestionIds(userId);
                if (CollectionUtils.isNotEmpty(followQuestionIds)) {
                    wrapper.in(Question::getId, followQuestionIds);
                } else {
                    wrapper.eq(Question::getId, -1L); // 没关注则查不到
                }
            } else if (QAQuestionSearchType.UNANSWERED.getType().equals(type)) {
                wrapper.eq(Question::getStatus, (byte)1);
            } else if (QAQuestionSearchType.ANSWERED.getType().equals(type)) {
                wrapper.eq(Question::getStatus, (byte)2);
            }
        }

        // 3. 开始分页
        PageHelper.startPage(pageNum, pageSize);
        List<Question> questions = oshQaQuestionMapper.selectList(wrapper);

        // 4. 重要：先用原始 list 构建 PageInfo，确保 total 正确
        PageInfo<Question> entityPageInfo = new PageInfo<>(questions);

        // 5. 转换 VO
        List<QueryQuestionListVO> voList = questions.stream().map(question -> {
            QueryQuestionListVO vo = new QueryQuestionListVO();
            BeanUtils.copyProperties(question, vo);
            // 这里可以顺便处理下用户信息、时间格式等
            return vo;
        }).collect(Collectors.toList());

        // 6. 返回时手动把原 PageInfo 的 total 塞进去
        return new TableDataInfo(voList, entityPageInfo.getTotal());
    }

    @Override
    public R<String> solve(Long userId, Long questionId, Long answerId) {
        LambdaQueryWrapper<Answer> answerWrapper = new LambdaQueryWrapper<Answer>()
                .select(Answer::getQuestionId)
                .eq(Answer::getId, answerId);
        Answer answer = oshQaAnswerMapper.selectOne(answerWrapper);
        if (answer == null) {
            return R.fail(ResultCode.FAILED_NOT_EXISTS.getMsg());
        }
        if (!answer.getQuestionId().equals(questionId)) {
            return R.fail(ResultCode.FAILED.getMsg());
        }
        LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<Question>()
                .select(Question::getUserId)
                .eq(Question::getId, questionId);
        Question question = oshQaQuestionMapper.selectOne(questionWrapper);
        if (question == null) {
            return R.fail(ResultCode.FAILED_NOT_EXISTS.getMsg());
        }
        if (!question.getUserId().equals(userId)) {
            return R.fail(ResultCode.FAILED_USER_PERMISSION_DENIED.getMsg());
        }
        answer.setIsSolution((byte)1);
        oshQaAnswerMapper.update(answer, answerWrapper);
        question.setStatus((byte)2);
        oshQaQuestionMapper.update(question, questionWrapper);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> cancelSolve(Long userId, Long questionId, Long answerId) {
        LambdaQueryWrapper<Answer> answerWrapper = new LambdaQueryWrapper<Answer>()
                .select(Answer::getIsSolution)
                .eq(Answer::getId, answerId);
        Answer answer = oshQaAnswerMapper.selectOne(answerWrapper);
        if (answer == null) {
            return R.fail(ResultCode.FAILED_NOT_EXISTS.getMsg());
        }
        LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<Question>()
                .select(Question::getStatus)
                .eq(Question::getId, questionId);
        Question question = oshQaQuestionMapper.selectOne(questionWrapper);
        if (question == null) {
            return R.fail(ResultCode.FAILED_NOT_EXISTS.getMsg());
        }

        answer.setIsSolution((byte)0);
        oshQaAnswerMapper.update(answer, answerWrapper);
        question.setStatus((byte)1);
        oshQaQuestionMapper.update(question, questionWrapper);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<QueryQuestionDetailVO> detail(Long id, Long questionId) {
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
    public R<String> vote(Long userId, Long answerId) {
        Integer deleteFlag = oshQaAnswerMapper.getVoteInfoByUserIdAndAnswerId(userId, answerId);
        if (deleteFlag != null && deleteFlag == 0) {
            return R.fail(ResultCode.FAILED.getMsg());
        }
        LambdaQueryWrapper<Answer> answerWrapper = new LambdaQueryWrapper<Answer>()
                .select(Answer::getVoteCount)
                .eq(Answer::getId, answerId);
        Answer answer = oshQaAnswerMapper.selectOne(answerWrapper);
        if (answer == null) {
            return R.fail(ResultCode.FAILED_NOT_EXISTS.getMsg());
        }
        oshQaAnswerMapper.voteAnswer(userId, answerId, userId);
        answer.setVoteCount(answer.getVoteCount() + 1);
        oshQaAnswerMapper.update(answer, answerWrapper);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> cancelVote(Long userId, Long answerId) {
        Integer deleteFlag = oshQaAnswerMapper.getVoteInfoByUserIdAndAnswerId(userId, answerId);
        if (deleteFlag != null && deleteFlag == 0) {
            LambdaQueryWrapper<Answer> answerWrapper = new LambdaQueryWrapper<Answer>()
                    .select(Answer::getVoteCount)
                    .eq(Answer::getId, answerId);
            Answer answer = oshQaAnswerMapper.selectOne(answerWrapper);
            if (answer == null) {
                return R.fail(ResultCode.FAILED_NOT_EXISTS.getMsg());
            }
            oshQaAnswerMapper.cancelVoteAnswer(userId, answerId, userId);
            answer.setVoteCount(answer.getVoteCount() - 1);
            oshQaAnswerMapper.update(answer, answerWrapper);
            return R.ok(ResultCode.SUCCESS.getMsg());
        }
        return R.fail(ResultCode.FAILED.getMsg());
    }
}