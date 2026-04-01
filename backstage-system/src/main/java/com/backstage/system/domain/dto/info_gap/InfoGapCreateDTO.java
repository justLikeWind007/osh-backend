package com.backstage.system.domain.dto.info_gap;

/**
 * 发布信息请求对象
 */
public class InfoGapCreateDTO {
    private String title;
    private String content;
    private String tag;

    // Getter and Setter
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
}