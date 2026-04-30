package com.backstage.common.enums;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/23
 * Time: 18:26
 */
public enum ResourceType {
    COURSE("course","osh_course"),
    QA_QUESTION("qa_question","osh_question_answer_question"),
    QA_ANSWER("qa_answer","osh_question_answer_answer"),
    BOOK("book","osh_book"),
    ;
    private final String type;
    private final String tableName;

    ResourceType(String type, String tableName) {
        this.type = type;
        this.tableName = tableName;
    }

    public String getType() {
        return type;
    }

    public String getTableName() {
        return tableName;
    }

    public static ResourceType fromTypeCode(String type) {
        for (ResourceType curType : values()) {
            if (curType.getType().equals(type)) {
                return curType;
            }
        }
        throw new IllegalArgumentException("未知的资源类型: " + type);
    }

    public static final String COURSE_TYPE = "course";
    public static final String QA_QUESTION_TYPE = "qa_question";
    public static final String QA_ANSWER_TYPE = "qa_answer";
    public static final String QA_TAG_TYPE = "qa_tag";
    public static final String BOOK_TYPE = "book";
}