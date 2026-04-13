package com.backstage.system.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 课程资料 VO
 * 
 * @author ruoyi
 * @date 2026-03-24
 */
@ApiModel(description = "课程资料 VO")
public class CourseMaterialVO {
    
    /** 资料 ID */
    @ApiModelProperty("资料 ID")
    private Long id;
    
    /** 资料名称 */
    @ApiModelProperty("资料名称")
    private String materialName;
    
    /** 文件 URL */
    @ApiModelProperty("文件 URL")
    private String fileUrl;
    
    /** 文件大小 */
    @ApiModelProperty("文件大小 (字节)")
    private Long fileSize;
    
    /** 下载次数 */
    @ApiModelProperty("下载次数")
    private Integer downloadCount;
    
    /** 是否可下载 */
    @ApiModelProperty("是否可下载")
    private Boolean isDownloadable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Boolean getIsDownloadable() {
        return isDownloadable;
    }

    public void setIsDownloadable(Boolean isDownloadable) {
        this.isDownloadable = isDownloadable;
    }
}
