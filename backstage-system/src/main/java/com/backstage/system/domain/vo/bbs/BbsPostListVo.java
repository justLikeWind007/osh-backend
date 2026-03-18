package com.backstage.system.domain.vo.bbs;

import java.util.List;

public class BbsPostListVo {
    private Long id;
    private Long bbs_id;
    private PostDescVo desc;
    private Long user_id;
    private Integer comment_count;
    private Integer support_count;
    private Integer is_top;
    private String created_time;
    private PostUserVo user;
    private Boolean issupport;

    // 手动生成 Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBbs_id() { return bbs_id; }
    public void setBbs_id(Long bbs_id) { this.bbs_id = bbs_id; }
    public PostDescVo getDesc() { return desc; }
    public void setDesc(PostDescVo desc) { this.desc = desc; }
    public Long getUser_id() { return user_id; }
    public void setUser_id(Long user_id) { this.user_id = user_id; }
    public Integer getComment_count() { return comment_count; }
    public void setComment_count(Integer comment_count) { this.comment_count = comment_count; }
    public Integer getSupport_count() { return support_count; }
    public void setSupport_count(Integer support_count) { this.support_count = support_count; }
    public Integer getIs_top() { return is_top; }
    public void setIs_top(Integer is_top) { this.is_top = is_top; }
    public String getCreated_time() { return created_time; }
    public void setCreated_time(String created_time) { this.created_time = created_time; }
    public PostUserVo getUser() { return user; }
    public void setUser(PostUserVo user) { this.user = user; }
    public Boolean getIssupport() { return issupport; }
    public void setIssupport(Boolean issupport) { this.issupport = issupport; }
}