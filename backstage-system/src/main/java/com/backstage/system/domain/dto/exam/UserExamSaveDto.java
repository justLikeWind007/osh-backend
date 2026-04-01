package com.backstage.system.domain.dto.exam;

import java.io.Serializable;
import java.util.List;

/**
 * 考试提交 DTO 对象
 */
public class UserExamSaveDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 考试记录ID (osh_user_exam_record 表的 ID) */
    private Long user_test_id;

    /** 试卷模板ID (osh_examination 表的 ID) */
    private Long exam_id;

    /** 用户提交的答案，包含字符串、数字、数组等 */
    private List<Object> value;

    // --- Getters and Setters ---

    public Long getUser_test_id() {
        return user_test_id;
    }

    public void setUser_test_id(Long user_test_id) {
        this.user_test_id = user_test_id;
    }

    public Long getExam_id() {
        return exam_id;
    }

    public void setExam_id(Long exam_id) {
        this.exam_id = exam_id;
    }

    public List<Object> getValue() {
        return value;
    }

    public void setValue(List<Object> value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "UserExamSaveDto{" +
                "user_test_id=" + user_test_id +
                ", exam_id=" + exam_id +
                ", value=" + value +
                '}';
    }
}