package com.backstage.system.domain.vo;

import java.io.Serializable;

/**
 * 电子书章节内容视图对象
 *
 * @author backstage
 */
public class BookChapterContentVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 章节标题 */
    private String title;

    /** 章节内容 */
    private String content;

    /** 是否免费（0收费 1免费） */
    private Integer isfree;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Integer getIsfree()
    {
        return isfree;
    }

    public void setIsfree(Integer isfree)
    {
        this.isfree = isfree;
    }
}
