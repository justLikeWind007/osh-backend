package com.backstage.system.domain.vo.resource;

import java.io.Serializable;

/**
 * 资源返回 VO
 *
 * @author backstage
 */
public class ResourceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资源ID
     */
    private Long id;

    /**
     * 资源名称
     */
    private String name;

    /**
     * 资源类型
     */
    private String type;

    /**
     * 备注
     */
    private String remark;

    /**
     * 文件路径
     */
    private String filePath;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
