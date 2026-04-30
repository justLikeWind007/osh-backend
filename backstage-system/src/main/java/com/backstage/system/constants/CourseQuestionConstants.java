package com.backstage.system.constants;

/**
 * 课程问答相关常量
 * 包括：问题状态、审核状态、类型常量等
 * 
 * @author ruoyi
 */
public final class CourseQuestionConstants {

    /**
     * 问题状态常量
     */
    // 待回答
    public static final String STATUS_PENDING = "pending";
    
    // 已回答
    public static final String STATUS_ANSWERED = "answered";
    
    // 已采纳
    public static final String STATUS_ADOPTED = "adopted";

    /**
     * 审核状态常量
     */
    // 待审核
    public static final String AUDIT_STATUS_PENDING = "pending";
    
    // 已审核通过
    public static final String AUDIT_STATUS_APPROVED = "approved";
    
    // 审核拒绝
    public static final String AUDIT_STATUS_REJECTED = "rejected";

    /**
     * 课程服务人员相关常量
     */
    // 服务人员类型：助教
    public static final String STAFF_TYPE_TEACHING_ASSISTANT = "teaching_assistant";
    
    // 服务人员类型：班主任
    public static final String STAFF_TYPE_COORDINATOR = "coordinator";

    /**
     * 收藏相关常量
     */
    // 收藏类型：课程
    public static final String FAVORITE_TYPE_COURSE = "course";

    // 收藏类型：电子书
    public static final String FAVORITE_TYPE_BOOK = "book";

    /**
     * 内部使用的字段名常量（用于 Map 操作）
     */
    public static final String FIELD_ID = "id";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_ANSWER_CONTENT = "answerContent";
    public static final String FIELD_ANSWER_USER_ID = "answerUserId";
    public static final String FIELD_ANSWER_TIME = "answerTime";
    public static final String FIELD_AUDIT_STATUS = "auditStatus";
    public static final String FIELD_AUDIT_USER_ID = "auditUserId";
    public static final String FIELD_AUDIT_REMARK = "auditRemark";
    public static final String FIELD_QUESTION_ID = "questionId";
    public static final String FIELD_LAST_REPLY_TIME = "lastReplyTime";
    public static final String FIELD_UPDATE_BY = "updateBy";

    private CourseQuestionConstants() {
    }
}
