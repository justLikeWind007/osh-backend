package com.backstage.system.domain.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户考试记录表对象 osh_user_exam_record
 */
@TableName("osh_user_exam_record")
public class OshUserExamRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户 ID */
    private Long user_id;

    /** 试卷模板 ID (对应你说的 12) */
    private Long exam_id;

    /** 最终得分 */
    private Integer score;

    /** 答题状态：0-考试中，1-已完成 */
    private Integer answer_status;

    /** 批阅状态：0-未批改，1-已批改 */
    private Integer read_status;

    /** 用户提交的完整答案 JSON 字符串 */
    private String answer_json;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;

    /** 删除标志：0-未删除，1-已删除 */
    @TableField("delete_flag")
    private Integer delete_flag;

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUser_id() { return user_id; }
    public void setUser_id(Long user_id) { this.user_id = user_id; }

    public Long getExam_id() { return exam_id; }
    public void setExam_id(Long exam_id) { this.exam_id = exam_id; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getAnswer_status() { return answer_status; }
    public void setAnswer_status(Integer answer_status) { this.answer_status = answer_status; }

    public Integer getRead_status() { return read_status; }
    public void setRead_status(Integer read_status) { this.read_status = read_status; }

    public String getAnswer_json() { return answer_json; }
    public void setAnswer_json(String answer_json) { this.answer_json = answer_json; }

    public Date getCreate_time() { return create_time; }
    public void setCreate_time(Date create_time) { this.create_time = create_time; }

    public Integer getDelete_flag() { return delete_flag; }
    public void setDelete_flag(Integer delete_flag) { this.delete_flag = delete_flag; }
}