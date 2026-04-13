package com.backstage.system.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 章节资料DTO
 *
 * @author ruoyi
 * @date 2026-03-27
 */
@ApiModel(description = "章节资料DTO")
public class SectionMaterialDTO {

    @ApiModelProperty("资料名称")
    private String name;

    @ApiModelProperty("资料文件URL")
    private String url;

    @ApiModelProperty("文件大小（字节）")
    private Long fileSize;

    @ApiModelProperty("文件类型")
    private String fileType;

    @ApiModelProperty("是否仅购买后可下载：0-否 1-是")
    private Integer isPayOnly = 1;

    @ApiModelProperty("排序")
    private Integer sort = 0;

    // ==================== Getter/Setter ====================

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

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Integer getIsPayOnly() {
        return isPayOnly;
    }

    public void setIsPayOnly(Integer isPayOnly) {
        this.isPayOnly = isPayOnly;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}