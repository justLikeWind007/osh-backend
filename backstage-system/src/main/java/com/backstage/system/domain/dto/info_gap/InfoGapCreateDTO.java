package com.backstage.system.domain.dto.info_gap;

import java.util.List;

/**
 * 发布信息请求对象
 */
public class InfoGapCreateDTO {
    private String title;
    private String content;
    private String tag;
    private List<String> tags;

    // Getter and Setter
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "InfoGapCreateDTO{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", tag='" + tag + '\'' +
                ", tags=" + tags +
                '}';
    }
}