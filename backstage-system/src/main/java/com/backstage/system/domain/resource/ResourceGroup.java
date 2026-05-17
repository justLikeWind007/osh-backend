package com.backstage.system.domain.resource;

import com.backstage.common.core.domain.entity.OSHBaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 内部资源组实体
 *
 * @author backstage
 */
@TableName("osh_resource_group")
public class ResourceGroup extends OSHBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资源组ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 资源组名称
     */
    @TableField("name")
    private String name;

    /**
     * 资源组描述
     */
    @TableField("description")
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
