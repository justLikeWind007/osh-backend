package com.backstage.system.service.questionanswer.impl;

import com.backstage.common.core.domain.R;
import com.backstage.common.enums.ResultCode;
import com.backstage.system.domain.questionanswer.Answer;
import com.backstage.system.domain.questionanswer.Question;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.domain.websocket.WsNotifyMessage;
import com.backstage.system.mapper.questionanswer.OshQAAnswerMapper;
import com.backstage.system.mapper.questionanswer.OshQAQuestionMapper;
import com.backstage.system.mapper.user.OshUserMapper;
import com.backstage.system.service.questionanswer.IOshQAAnswerService;
import com.backstage.system.service.websocket.WebSocketNotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 问答回答 Service 实现
 */
@Service
public class OshQAAnswerServiceImpl implements IOshQAAnswerService {

    private static final Logger log = LoggerFactory.getLogger(OshQAAnswerServiceImpl.class);

    @Autowired
    private OshQAAnswerMapper oshQAAnswerMapper;

    @Autowired
    private OshQAQuestionMapper questionMapper;

    @Autowired
    private OshUserMapper userMapper;

    @Autowired
    private WebSocketNotifyService webSocketNotifyService;

    @Override
    public R<String> answer(Long userId, Long questionId, String content) {
        // 1. 保存回答
        Answer answer = new Answer();
        answer.setQuestionId(questionId);
        answer.setUserId(userId);
        answer.setContent(content);
        oshQAAnswerMapper.insert(answer);

        // 2. 推送通知（异常不影响主流程）
        try {
            pushAnswerNotify(userId, questionId, content);
        } catch (Exception e) {
            log.error("问答回复通知推送失败，questionId={}, answererUserId={}, 原因={}",
                    questionId, userId, e.getMessage());
        }

        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    /**
     * 构建并推送新回复通知
     * 判断规则：
     *   1. 问题不存在 → 跳过
     *   2. 回答者 == 提问者（自问自答）→ 跳过
     */
    private void pushAnswerNotify(Long answererUserId, Long questionId, String content) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            log.warn("推送通知跳过：问题不存在，questionId={}", questionId);
            return;
        }

        // 自问自答不推送
        if (answererUserId.equals(question.getUserId())) {
            return;
        }

        // 查询回答者昵称
        OshUser answerer = userMapper.selectById(answererUserId);
        String nickname = (answerer != null && answerer.getUsername() != null)
                ? answerer.getNickname() : "匿名用户";

        // 构建消息体，content 截取摘要
        WsNotifyMessage msg = new WsNotifyMessage();
        msg.setType("QA_NEW_ANSWER");
        msg.setTitle(nickname + " 回答了你的问题");
        msg.setContent(webSocketNotifyService.truncate(content));
        msg.setJumpUrl("/question_answer/detail/" + questionId);
        msg.setBizId(questionId.toString());

        webSocketNotifyService.send(question.getUserId(), msg);
    }
}
