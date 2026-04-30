package com.backstage.system.domain.order;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.backstage.common.annotation.Excel;
import com.backstage.common.core.domain.BaseEntity;

/**
 * 图片上传记录对象 osh_upload_image
 * 
 * @author ruoyi
 * @date 2026-03-11
 */
@ApiModel("图片上传实体")
@TableName("osh_upload_image")
public class OshUploadImage extends BaseEntity
{

    /** 主键ID */
    @ApiModelProperty("id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属网校ID（关联school表） */
    @Excel(name = "所属网校ID", readConverterExp = "关=联school表")
    private Long schoolId;

    /** 上传用户ID（关联user表） */
    @Excel(name = "上传用户ID", readConverterExp = "关=联user表")
    private Long userId;

    /** 原始文件名（如：avatar.jpg） */
    @Excel(name = "原始文件名", readConverterExp = "如=：avatar.jpg")
    private String fileName;

    /** 图片访问URL（接口响应的data字段） */
    @Excel(name = "图片访问URL", readConverterExp = "接=口响应的data字段")
    private String filePath;

    /** 文件大小（字节） */
    @Excel(name = "文件大小", readConverterExp = "字=节")
    private Long fileSize;

    /** 文件类型（如：image/jpeg、image/png） */
    @Excel(name = "文件类型", readConverterExp = "如=：image/jpeg、image/png")
    private String fileType;

    /** 状态：1正常 0禁用 */
    @Excel(name = "状态：1正常 0禁用")
    private Long status;

    /** 上传时间 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "上传时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    private Date createdTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setSchoolId(Long schoolId) 
    {
        this.schoolId = schoolId;
    }

    public Long getSchoolId() 
    {
        return schoolId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setFileName(String fileName) 
    {
        this.fileName = fileName;
    }

    public String getFileName() 
    {
        return fileName;
    }

    public void setFilePath(String filePath) 
    {
        this.filePath = filePath;
    }

    public String getFilePath() 
    {
        return filePath;
    }

    public void setFileSize(Long fileSize) 
    {
        this.fileSize = fileSize;
    }

    public Long getFileSize() 
    {
        return fileSize;
    }

    public void setFileType(String fileType) 
    {
        this.fileType = fileType;
    }

    public String getFileType() 
    {
        return fileType;
    }

    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    public void setCreatedTime(Date createdTime) 
    {
        this.createdTime = createdTime;
    }

    public Date getCreatedTime() 
    {
        return createdTime;
    }

    public void setUpdatedTime(Date updatedTime) 
    {
        this.updatedTime = updatedTime;
    }

    public Date getUpdatedTime() 
    {
        return updatedTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("schoolId", getSchoolId())
            .append("userId", getUserId())
            .append("fileName", getFileName())
            .append("filePath", getFilePath())
            .append("fileSize", getFileSize())
            .append("fileType", getFileType())
            .append("status", getStatus())
            .append("createdTime", getCreatedTime())
            .append("updatedTime", getUpdatedTime())
            .toString();
    }
}
