package com.backstage.system.domain.vo.studyprogress;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 学习进度列表返回对象
 */
public class UserStudyProgressVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;              // 记录ID
    private String title;         // 课程/专栏/图文标题
    private String cover;         // 封面图
    private String type;          // 类型 (course, column, media)
    private BigDecimal progress;  // 学习进度 (例如 15.15)

    public UserStudyProgressVo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getProgress() { return progress; }
    public void setProgress(BigDecimal progress) { this.progress = progress; }
}