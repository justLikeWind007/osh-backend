package com.backstage.system.domain.vo.resource;

import java.io.Serializable;

/**
 * 资源链接返回 VO
 *
 * @author backstage
 */
public class ResourceLinkVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 链接ID */
    private Long id;

    /** 链接名称 */
    private String name;

    /** 跳转地址 */
    private String url;

    /** 备注 */
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
