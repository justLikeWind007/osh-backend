package com.backstage.system.service.assistant;

import com.backstage.system.domain.assistant.vo.AssistantAnswerVO;
import com.backstage.system.domain.assistant.vo.AssistantInitVO;

/**
 * AI 助手服务接口
 *
 * @author backstage
 */
public interface IAssistantService {

    /**
     * 获取 AI 助手初始化信息
     *
     * @param courseId  课程 ID（可选）
     * @param userId    用户 ID（可选）
     * @param userLevel 用户等级（可选）
     * @return 初始化信息
     */
    AssistantInitVO getInit(Long courseId, Long userId, Integer userLevel);

    /**
     * 站点问答
     *
     * @param question 用户提问
     * @return 问答结果
     */
    AssistantAnswerVO answerSiteQuestion(String question);

    /**
     * 课程问答
     *
     * @param courseId 课程 ID
     * @param question 用户提问
     * @return 问答结果
     */
    AssistantAnswerVO answerCourseQuestion(Long courseId, String question);
}
