package com.backstage.system.domain.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 标签 VO
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
@ApiModel(description = "标签信息")
public class TagVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /** 标签 ID */
    @ApiModelProperty("标签 ID")
    private Long id;

    /** 标签名称 */
    @ApiModelProperty("标签名称")
    private String name;

    /** 使用次数（关联课程数） */
    @ApiModelProperty("使用次数")
    private Integer useCount;

    public TagVO() {
    }

    public TagVO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public TagVO(Long id, String name, Integer useCount) {
        this.id = id;
        this.name = name;
        this.useCount = useCount;
    }

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

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }
}
