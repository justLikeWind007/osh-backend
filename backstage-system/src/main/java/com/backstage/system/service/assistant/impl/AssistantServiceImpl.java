package com.backstage.system.service.assistant.impl;

import cn.hutool.core.util.StrUtil;
import com.backstage.system.domain.vo.CoursePurchaseStatusVO;
import com.backstage.system.domain.assistant.vo.AssistantAnswerVO;
import com.backstage.system.domain.assistant.vo.AssistantInitVO;
import com.backstage.system.service.assistant.IAssistantService;
import com.backstage.system.service.course.ICourseManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * AI 助手服务实现
 *
 * @author backstage
 */
@Service
@RequiredArgsConstructor
public class AssistantServiceImpl implements IAssistantService {

    private static final int VIP_LEVEL = 3;

    private final ICourseManageService courseManageService;

    @Override
    public AssistantInitVO getInit(Long courseId, Long userId, Integer userLevel) {
        AssistantInitVO vo = new AssistantInitVO();
        boolean loggedIn = userId != null;
        vo.setLoggedIn(loggedIn);
        vo.setFeedbackEnabled(loggedIn);
        vo.setFeedbackMessage(loggedIn ? "登录后可查看和提交反馈" : "请先登录后提交反馈");
        vo.setCourseId(courseId);

        if (courseId == null) {
            vo.setCourseQaEnabled(false);
            vo.setCourseQaReason("当前页面未绑定课程上下文，请进入课程详情页或学习页后再提问");
            return vo;
        }

        CoursePurchaseStatusVO purchaseStatus = courseManageService.getPurchaseStatus(courseId, userId);
        vo.setCourseName(purchaseStatus.getCourseName());

        if (userLevel != null && userLevel >= VIP_LEVEL) {
            vo.setCourseQaEnabled(true);
            vo.setCourseQaReason("VIP 用户可直接使用课程问答");
            return vo;
        }

        if (Boolean.TRUE.equals(purchaseStatus.getIsPurchased())) {
            vo.setCourseQaEnabled(true);
            vo.setCourseQaReason("已购课用户可使用课程问答");
            return vo;
        }

        vo.setCourseQaEnabled(false);
        vo.setCourseQaReason(StrUtil.isNotBlank(purchaseStatus.getReason())
                ? purchaseStatus.getReason()
                : "您暂未购买该课程或未开通 VIP");
        return vo;
    }

    @Override
    public AssistantAnswerVO answerSiteQuestion(String question) {
        AssistantAnswerVO vo = new AssistantAnswerVO();
        vo.setMode("site");
        vo.setQuestion(question);
        vo.setAnswerType("mock");
        vo.setAnswer(resolveSiteAnswer(question));
        return vo;
    }

    @Override
    public AssistantAnswerVO answerCourseQuestion(Long courseId, String question) {
        AssistantAnswerVO vo = new AssistantAnswerVO();
        vo.setMode("course");
        vo.setCourseId(courseId);
        vo.setQuestion(question);
        vo.setAnswerType("mock");
        CoursePurchaseStatusVO purchaseStatus = courseManageService.getPurchaseStatus(courseId, null);
        vo.setCourseName(purchaseStatus.getCourseName());
        vo.setAnswer(resolveCourseAnswer(question, purchaseStatus.getCourseName()));
        return vo;
    }

    /**
     * 解析站点问答（模拟回答）
     */
    private String resolveSiteAnswer(String question) {
        String normalized = normalize(question);
        if (containsAny(normalized, "登录", "注册", "账号", "密码")) {
            return "当前为固定演示回答：您可以先通过页面右上角的登录入口完成登录；如果忘记密码，请使用找回密码功能重置账号信息。";
        }
        if (containsAny(normalized, "购买", "下单", "支付", "订单")) {
            return "当前为固定演示回答：您可以在课程详情页点击\"立即学习\"或购买按钮完成下单；支付完成后会自动解锁对应课程内容。";
        }
        if (containsAny(normalized, "优惠券", "优惠", "折扣")) {
            return "当前为固定演示回答：如果您的账户中有可用优惠券，系统会在下单时自动展示可选优惠信息。";
        }
        if (containsAny(normalized, "积分")) {
            return "当前为固定演示回答：当订单支持积分抵扣时，系统会优先按规则扣减积分，您无需额外手动选择支付方式。";
        }
        if (containsAny(normalized, "客服", "微信", "联系", "人工")) {
            return "当前为固定演示回答：如需进一步协助，您可以先通过问题反馈模块提交诉求，后续可在反馈状态中查看处理进度。";
        }
        return "当前为固定演示回答：暂未匹配到更具体的网站使用答案，您可以换个说法继续提问，或通过\"问题反馈\"告诉我们您的具体场景。";
    }

    /**
     * 解析课程问答（模拟回答）
     */
    private String resolveCourseAnswer(String question, String courseName) {
        String normalized = normalize(question);
        String courseLabel = StrUtil.isNotBlank(courseName) ? "《" + courseName + "》" : "当前课程";
        if (containsAny(normalized, "适合", "人群", "基础")) {
            return "当前为固定演示回答：" + courseLabel + "适合希望系统学习该主题的用户，建议先从课程目录和前几节内容了解整体结构。";
        }
        if (containsAny(normalized, "大纲", "目录", "章节")) {
            return "当前为固定演示回答：您可以先查看课程详情页中的课程大纲，按章节顺序学习；后续这里会接入真实课程知识库回答更细的问题。";
        }
        if (containsAny(normalized, "资料", "讲义", "源码", "下载")) {
            return "当前为固定演示回答：课程相关资料通常会在课程详情页或学习页的资料区域提供，后续也会在课程问答中支持更精确的资料定位。";
        }
        if (containsAny(normalized, "学习", "开始", "怎么看")) {
            return "当前为固定演示回答：购买成功后，您可以通过课程详情页进入学习中心，按章节逐步学习视频、文档和配套资料。";
        }
        return "当前为固定演示回答：" + courseLabel + "的智能问答能力仍在建设中，后续接入 RAG 后会支持更细粒度的课程知识问答。";
    }

    /**
     * 检查字符串是否包含任意关键词
     */
    private boolean containsAny(String source, String... keywords) {
        return Arrays.stream(keywords).anyMatch(source::contains);
    }

    /**
     * 标准化问题文本
     */
    private String normalize(String question) {
        return StrUtil.isNotBlank(question) ? question.trim().toLowerCase() : "";
    }
}
