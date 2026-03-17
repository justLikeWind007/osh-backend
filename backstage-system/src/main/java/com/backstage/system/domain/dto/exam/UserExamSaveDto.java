package com.backstage.system.domain.dto.exam;

import java.io.Serializable;
import java.util.List;

public class UserExamSaveDto implements Serializable {
    private Long user_test_id; // 考试记录ID
    private List<Object> value; // 用户提交的答案，包含字符串、数字、数组等

    public Long getUser_test_id() { return user_test_id; }
    public void setUser_test_id(Long user_test_id) { this.user_test_id = user_test_id; }
    public List<Object> getValue() { return value; }
    public void setValue(List<Object> value) { this.value = value; }
}