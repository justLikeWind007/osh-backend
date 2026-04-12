package com.backstage.system.service.questionanswer.impl;

import com.backstage.common.core.domain.R;
import com.backstage.common.enums.ResultCode;
import com.backstage.system.domain.questionanswer.Answer;
import com.backstage.system.domain.user.User;
import com.backstage.system.mapper.questionanswer.OshQAAnswerMapper;
import com.backstage.system.service.questionanswer.IOshQAAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/2
 * Time: 20:00
 */
@Service
public class OshQAAnswerServiceImpl implements IOshQAAnswerService {

    @Autowired
    private OshQAAnswerMapper oshQAAnswerMapper;
    @Override
    public R<String> answer(Long userId, Long questionId, String content) {
        Answer answer = new Answer();
        answer.setQuestionId(questionId);
        answer.setUserId(userId);
        answer.setContent(content);
        oshQAAnswerMapper.insert(answer);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }
}
