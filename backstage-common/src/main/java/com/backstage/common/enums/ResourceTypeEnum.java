package com.backstage.common.enums;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/23
 * Time: 18:26
 */
public enum ResourceTypeEnum {
    COURSE("course","osh_course"),
    QA_QUESTION("qa_question","osh_question_answer_question"),
    QA_ANSWER("qa_answer","osh_question_answer_answer"),
    BOOK("book","osh_book"),
    TOOL("tool","osh_tool"),
    ;
    private final String type;
    private final String tableName;

    ResourceTypeEnum(String type, String tableName) {
        this.type = type;
        this.tableName = tableName;
    }

    public String getType() {
        return type;
    }

    public String getTableName() {
        return tableName;
    }

    public static ResourceTypeEnum fromTypeCode(String type) {
        for (ResourceTypeEnum curType : values()) {
            if (curType.getType().equals(type)) {
                return curType;
            }
        }
        throw new IllegalArgumentException("未知的资源类型: " + type);
    }
}