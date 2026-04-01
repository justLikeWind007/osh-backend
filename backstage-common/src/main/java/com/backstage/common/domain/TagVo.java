package com.backstage.common.domain;

/**
 * @Author: Hope_Lau
 * @createTime: 2026年03月31日 00:18:17
 * @version:
 * @Description:
 */
public class TagVo {

    private String tagId;
    private String tagName;

    public TagVo() {
    }

    public TagVo(String tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
