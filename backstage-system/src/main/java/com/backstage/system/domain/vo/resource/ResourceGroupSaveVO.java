package com.backstage.system.domain.vo.resource;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 资源组保存/更新 请求 VO
 *
 * @author backstage
 */
public class ResourceGroupSaveVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 资源组ID，新增时为空 */
    private Long id;

    /** 资源组名称 */
    @NotBlank(message = "资源组名称不能为空")
    @Size(max = 128, message = "资源组名称长度不能超过128")
    private String name;

    /** 资源组描述 */
    @Size(max = 512, message = "资源组描述长度不能超过512")
    private String description;

    /** 备注 */
    @Size(max = 255, message = "备注长度不能超过255")
    private String remark;

    /** 关联的资源ID列表 */
    private List<Long> resourceIds;

    /** 关联的链接ID列表 */
    private List<Long> linkIds;

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

    public List<Long> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(List<Long> resourceIds) {
        this.resourceIds = resourceIds;
    }

    public List<Long> getLinkIds() {
        return linkIds;
    }

    public void setLinkIds(List<Long> linkIds) {
        this.linkIds = linkIds;
    }
}
