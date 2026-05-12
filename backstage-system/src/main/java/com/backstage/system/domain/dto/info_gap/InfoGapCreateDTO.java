package com.backstage.system.domain.dto.info_gap;

import java.util.List;

/**
 * 发布信息请求对象
 */
public class InfoGapCreateDTO {
    private String title;
    private String content;
    private String tag;
    private List<Long> tagIds;

    // Getter and Setter
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    @Override
    public String toString() {
        return "InfoGapCreateDTO{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", tag='" + tag + '\'' +
                ", tagIds=" + tagIds +
                '}';
    }
}