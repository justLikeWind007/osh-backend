package com.backstage.system.domain.website;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * 实用网站标签表
 * @TableName osh_website_tag
 */
@TableName(value ="osh_website_tag")
public class OshWebsiteTag {
    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签编码
     */
    private String tagCode;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标志：0-正常，1-删除
     */
    private Integer deleteFlag;

    /**
     * 主键 ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 主键 ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 标签名称
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * 标签名称
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    /**
     * 标签编码
     */
    public String getTagCode() {
        return tagCode;
    }

    /**
     * 标签编码
     */
    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    /**
     * 排序顺序
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * 排序顺序
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * 创建者
     */
    public String getCreateBy() {
        return createBy;
    }

    /**
     * 创建者
     */
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    /**
     * 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 更新者
     */
    public String getUpdateBy() {
        return updateBy;
    }

    /**
     * 更新者
     */
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 删除标志：0-正常，1-删除
     */
    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    /**
     * 删除标志：0-正常，1-删除
     */
    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
    @Override
    public String toString() {
        return "OshWebsiteTag{" +
                "id=" + id +
                ", tagName='" + tagName + '\'' +
                ", tagCode='" + tagCode + '\'' +
                ", sortOrder=" + sortOrder +
                ", createBy='" + createBy + '\'' +
                ", createTime=" + createTime +
                ", updateBy='" + updateBy + '\'' +
                ", updateTime=" + updateTime +
                ", deleteFlag=" + deleteFlag +
                '}';
    }


}