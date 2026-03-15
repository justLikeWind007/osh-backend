package com.backstage.system.domain.vo;

import java.io.Serializable;

/**
 * 电子书章节视图对象
 *
 * @author backstage
 */
public class BookChapterVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 章节ID */
    private Long id;

    /** 章节标题 */
    private String title;

    /** 排序 */
    private Integer orderby;

    /** 是否免费（0收费 1免费） */
    private Integer isfree;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Integer getOrderby()
    {
        return orderby;
    }

    public void setOrderby(Integer orderby)
    {
        this.orderby = orderby;
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
