package com.backstage.system.service.impl;

import com.backstage.common.exception.ServiceException;
import com.backstage.system.component.CommentForbiddenWordFilter;
import com.backstage.system.domain.course.OshCourseQuestion;
import com.backstage.system.domain.course.vo.CourseQuestionAnswerItemVo;
import com.backstage.system.domain.course.vo.CourseQuestionListItemVo;
import com.backstage.system.mapper.course.OshCourseMapper;
import com.backstage.system.mapper.course.OshCourseQuestionMapper;
import com.backstage.system.request.CourseQuestionAnswerRequest;
import com.backstage.system.request.CourseQuestionPageRequest;
import com.backstage.system.request.CourseSectionQuestionRequest;
import com.backstage.system.service.IOshCourseQuestionService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OshCourseQuestionServiceImpl implements IOshCourseQuestionService {

    private static final int RECORD_TYPE_QUESTION = 1;
    private static final int RECORD_TYPE_ANSWER = 2;

    @Autowired
    private OshCourseQuestionMapper oshCourseQuestionMapper;

    @Autowired
    private OshCourseMapper oshCourseMapper;

    @Autowired
    private CommentForbiddenWordFilter commentForbiddenWordFilter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitQuestion(Long userId, String operator, CourseSectionQuestionRequest request) {
        validateQuestionContent(request.getTitle(), "问题标题");
        validateQuestionContent(request.getContent(), "问题内容");

        Integer sectionCount = oshCourseMapper.countCourseSectionInCourse(request.getCourseId(), request.getSectionId());
        if (sectionCount == null || sectionCount <= 0) {
            throw new ServiceException("章节不存在或不属于当前课程");
        }

        OshCourseQuestion question = new OshCourseQuestion();
        Date now = new Date();
        question.setCourseId(request.getCourseId());
        question.setSectionId(request.getSectionId());
        question.setUserId(userId);
        question.setQuestionId(0L);
        question.setParentId(0L);
        question.setRecordType(RECORD_TYPE_QUESTION);
        question.setTitle(request.getTitle());
        question.setContent(request.getContent());
        question.setSolveStatus(0);
        question.setAcceptedAnswerId(0L);
        question.setReplyCount(0);
        question.setStatus(1);
        question.setDeleteFlag(0);
        question.setCreateBy(operator);
        question.setCreateTime(now);
        question.setUpdateBy(operator);
        question.setUpdateTime(now);

        if (oshCourseQuestionMapper.insertCourseQuestion(question) <= 0) {
            throw new ServiceException("提交问题失败");
        }
        oshCourseMapper.increaseQuestionCount(request.getCourseId());
        return question.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long answerQuestion(Long userId, String operator, CourseQuestionAnswerRequest request) {
        OshCourseQuestion question = oshCourseQuestionMapper.selectQuestionById(request.getQuestionId());
        if (question == null || !isQuestionRecord(question.getRecordType())) {
            throw new ServiceException("问题不存在");
        }

        OshCourseQuestion answer = new OshCourseQuestion();
        Date now = new Date();
        answer.setCourseId(question.getCourseId());
        answer.setSectionId(question.getSectionId());
        answer.setUserId(userId);
        answer.setQuestionId(question.getId());
        answer.setParentId(question.getId());
        answer.setRecordType(RECORD_TYPE_ANSWER);
        answer.setContent(request.getContent());
        answer.setSolveStatus(0);
        answer.setAcceptedAnswerId(0L);
        answer.setReplyCount(0);
        answer.setStatus(1);
        answer.setDeleteFlag(0);
        answer.setCreateBy(operator);
        answer.setCreateTime(now);
        answer.setUpdateBy(operator);
        answer.setUpdateTime(now);

        if (oshCourseQuestionMapper.insertCourseQuestion(answer) <= 0) {
            throw new ServiceException("提交回答失败");
        }
        oshCourseQuestionMapper.updateQuestionReplyMeta(question.getId(), now, operator);
        return answer.getId();
    }

    @Override
    public List<CourseQuestionListItemVo> listSectionQuestions(CourseQuestionPageRequest request) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        return oshCourseQuestionMapper.selectSectionQuestionPage(request);
    }

    @Override
    public List<CourseQuestionAnswerItemVo> listQuestionAnswers(Long questionId) {
        OshCourseQuestion question = oshCourseQuestionMapper.selectQuestionById(questionId);
        if (question == null || !isQuestionRecord(question.getRecordType())) {
            throw new ServiceException("问题不存在");
        }
        return oshCourseQuestionMapper.selectQuestionAnswers(questionId);
    }

    private boolean isQuestionRecord(Integer recordType) {
        return recordType != null && recordType == RECORD_TYPE_QUESTION;
    }

    private void validateQuestionContent(String content, String fieldName) {
        if (!commentForbiddenWordFilter.containsForbiddenWord(content)) {
            return;
        }
        String forbiddenWord = commentForbiddenWordFilter.matchForbiddenWord(content);
        if (forbiddenWord == null) {
            throw new ServiceException(fieldName + "包含违规内容");
        }
        throw new ServiceException(fieldName + "包含违规内容：" + forbiddenWord);
    }
}
