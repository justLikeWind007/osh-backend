package com.backstage.system.domain.site;

import com.backstage.common.core.domain.entity.OSHBaseEntity;
import com.baomidou.mybatisplus.annotation.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 网站负责人对象 osh_site_maintainer
 *
 * @author backstage
 */
@TableName("osh_site_maintainer")
public class OshSiteMaintainer extends OSHBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id", updateStrategy = FieldStrategy.NEVER)
    private Long id;

    /**
     * 关联网站ID
     */
    @NotNull(message = "网站ID不能为空")
    @TableField(value = "site_id", updateStrategy = FieldStrategy.NOT_NULL)
    private Long siteId;

    /**
     * 负责人用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @TableField(value = "user_id", updateStrategy = FieldStrategy.NOT_NULL)
    private Long userId;

    /**
     * 负责人姓名
     */
    @TableField(exist = false)
    private String userName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
