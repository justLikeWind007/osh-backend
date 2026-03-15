package com.backstage.system.domain;

import com.backstage.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 电子书章节对象 osh_book_chapter
 *
 * @author backstage
 */
@Data
@EqualsAndHashCode(callSuper = true)
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
}
