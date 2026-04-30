package com.backstage.common.enums;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/2
 * Time: 17:09
 */
public enum QAQuestionSearchType {
    ALL ("all"),                       // 全部
    MY_QUESTIONS ("my_questions"),     // 我的问题
    MY_FOLLOWS ("my_follows"),         // 我关注的
    UNANSWERED ("unanswered"),          // 未回答
    ANSWERED ("answered");
    private final String type;

    QAQuestionSearchType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
