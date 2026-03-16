package com.backstage.system.domain;

import com.backstage.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户电子书对象 user_book
 *
 * @author backstage
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("osh_user_book")
public class UserBook extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户电子书ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 电子书ID */
    private Long bookId;

    /** 删除标志（0代表存在 2代表删除） */
    @TableLogic
    private String delFlag;
}
