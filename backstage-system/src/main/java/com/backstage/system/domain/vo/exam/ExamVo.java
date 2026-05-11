package com.backstage.system.domain.vo.exam;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 考试列表返回 VO
 */
public class ExamVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private Integer total_score;
    private Integer pass_score;
    private Integer expire;
    private Integer question_count;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date start_time;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date end_time;
    private Integer status;
    private String cover;
    private String description;
    private Integer collect_count;
    /** 关联资源类型 */
    private String resource_type;
    /** 关联资源ID */
    private Long resource_id;
    /** 标签列表 */
    private List<String> tags;
    /** 当前用户是否已考过（answer_status=1） */
    private Boolean is_test;
    /** 当前用户是否已收藏 */
    private Boolean is_collected;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getTotal_score() { return total_score; }
    public void setTotal_score(Integer total_score) { this.total_score = total_score; }
    public Integer getPass_score() { return pass_score; }
    public void setPass_score(Integer pass_score) { this.pass_score = pass_score; }
    public Integer getExpire() { return expire; }
    public void setExpire(Integer expire) { this.expire = expire; }
    public Integer getQuestion_count() { return question_count; }
    public void setQuestion_count(Integer question_count) { this.question_count = question_count; }
    public Date getStart_time() { return start_time; }
    public void setStart_time(Date start_time) { this.start_time = start_time; }
    public Date getEnd_time() { return end_time; }
    public void setEnd_time(Date end_time) { this.end_time = end_time; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getCollect_count() { return collect_count; }
    public void setCollect_count(Integer collect_count) { this.collect_count = collect_count; }
    public String getResource_type() { return resource_type; }
    public void setResource_type(String resource_type) { this.resource_type = resource_type; }
    public Long getResource_id() { return resource_id; }
    public void setResource_id(Long resource_id) { this.resource_id = resource_id; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public Boolean getIs_test() { return is_test; }
    public void setIs_test(Boolean is_test) { this.is_test = is_test; }
    public Boolean getIs_collected() { return is_collected; }
    public void setIs_collected(Boolean is_collected) { this.is_collected = is_collected; }
}
