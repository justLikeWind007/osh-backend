package com.backstage.system.request;

import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * 课程资料创建请求
 */
public class CourseMaterialCreateRequest {

    @NotBlank(message = "资料文件名称不能为空")
    private String fileName;

    @NotBlank(message = "资料文件地址不能为空")
    private String fileUrl;

    @NotBlank(message = "资料文件类型不能为空")
    @Pattern(regexp = "(?i)zip|rar|7z", message = "资料文件类型必须是压缩包类型")
    private String fileType;

    //  字节转KB
    @NotNull(message = "资料文件大小不能为空")
    @DecimalMin(value = "0.00", inclusive = false, message = "资料文件大小必须大于0")
    private BigDecimal fileSize;

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
