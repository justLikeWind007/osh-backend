package com.backstage.system.domain.resource;

import com.backstage.common.core.domain.entity.OSHBaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 链接实体
 *
 * @author backstage
 */
@TableName("osh_link")
public class Link extends OSHBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 链接ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 链接名称
     */
    @TableField("name")
    private String name;

    /**
     * 跳转地址
     */
    @TableField("url")
    private String url;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
