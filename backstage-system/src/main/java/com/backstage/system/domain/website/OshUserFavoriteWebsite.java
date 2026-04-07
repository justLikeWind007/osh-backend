package com.backstage.system.domain.website;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * 用户收藏网站表
 * @TableName osh_user_favorite_website
 */
@TableName(value ="osh_user_favorite_website")
public class OshUserFavoriteWebsite {
    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 网站 ID
     */
    private Long websiteId;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 收藏时间
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
     * 备注
     */
    private String remark;

    /**
     * 
     */
    private Integer delFlag;

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
     * 用户 ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 用户 ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 网站 ID
     */
    public Long getWebsiteId() {
        return websiteId;
    }

    /**
     * 网站 ID
     */
    public void setWebsiteId(Long websiteId) {
        this.websiteId = websiteId;
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
     * 收藏时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 收藏时间
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
     * 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 
     */
    public Integer getDelFlag() {
        return delFlag;
    }

    /**
     * 
     */
    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
}