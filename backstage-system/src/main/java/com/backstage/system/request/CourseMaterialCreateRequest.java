package com.backstage.system.request;

import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * 课程资料创建请求
 */
public class CourseMaterialCreateRequest {

    /** 已有资料的 ID（编辑时保留已有资料，传此字段则跳过重建） */
    private Long materialId;

    @NotBlank(message = "资料文件名称不能为空")
    private String fileName;

    private String fileUrl;

    private String fileType;

    //  字节转KB
    private BigDecimal fileSize;

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = StringUtils.trimToNull(fileName);
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = StringUtils.trimToNull(fileUrl);
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = StringUtils.trimToNull(fileType);
    }

    public BigDecimal getFileSize() {
        return fileSize;
    }

    public void setFileSize(BigDecimal fileSize) {
        this.fileSize = fileSize;
    }
}
