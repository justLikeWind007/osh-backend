package com.backstage.system.domain;

import com.backstage.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;

/**
 * 电子书章节对象 osh_book_chapter
 *
 * @author backstage
 */
@TableName("osh_book_chapter")
public class BookChapter extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 章节ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 电子书ID */
    private Long bookId;

    /** 章节标题 */
    private String title;

    /** 章节内容 */
    private String content;

    /** 排序 */
    private Integer orderby;

    /** 是否免费（0收费 1免费） */
    private Integer isfree;

    /** 删除标志（0代表存在 2代表删除） */
    @TableLogic
    private String delFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getOrderby() {
        return orderby;
    }

    public void setOrderby(Integer orderby) {
        this.orderby = orderby;
    }

    public Integer getIsfree() {
        return isfree;
    }

    public void setIsfree(Integer isfree) {
        this.isfree = isfree;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }
}
