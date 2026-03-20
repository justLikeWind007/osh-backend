package com.backstage.system.domain.vo.bbs;

import java.util.List;

public class PostCommentVo {
    private Long id;
    private String content;
    private Long reply_id;
    private Integer is_top;
    private String created_time;
    private PostUserVo user;
    private List<PostCommentVo> post_comments; // 子评论

    // Getter & Setter (省略，请根据上述字段手动生成)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getReply_id() { return reply_id; }
    public void setReply_id(Long reply_id) { this.reply_id = reply_id; }
    public Integer getIs_top() { return is_top; }
    public void setIs_top(Integer is_top) { this.is_top = is_top; }
    public String getCreated_time() { return created_time; }
    public void setCreated_time(String created_time) { this.created_time = created_time; }
    public PostUserVo getUser() { return user; }
    public void setUser(PostUserVo user) { this.user = user; }
    public List<PostCommentVo> getPost_comments() { return post_comments; }
    public void setPost_comments(List<PostCommentVo> post_comments) { this.post_comments = post_comments; }
}