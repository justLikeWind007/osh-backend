package com.backstage.system.domain.vo.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;

/**
 * OSS 操作日志 VO
 */
@TableName("osh_oss_operation_log")
@ApiModel("OSS 操作日志实体")
public class OssOperationLogVo {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键 ID")
    private Long id;

    @ApiModelProperty("OSS 文件唯一标识（key）,文件在 bucket 的位置")
    private String fileKey;

    @ApiModelProperty("文件原始名称")
    private String originalName;

    @ApiModelProperty("文件后缀（mp4,jpg,png,pdf）")
    private String fileSuffix;

    @ApiModelProperty("文件大小（kb）")
    private Long fileSize;

    @ApiModelProperty("文件类型：video/image/audio/doc/other")
    private String fileType;

    @ApiModelProperty("操作类型：UPLOAD-上传 DELETE-删除 DOWNLOAD-下载 PLAY-视频播放")
    private String operationType;

    @ApiModelProperty("操作次数（同一文件同一操作累计）")
    private Integer operationCount;

    @ApiModelProperty("OSS 桶名")
    private String bucket;

    @ApiModelProperty("操作人账号")
    private String username;

    @ApiModelProperty("操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;

    @ApiModelProperty("客户端 IP")
    private String ip;

    @ApiModelProperty("客户端浏览器/设备信息")
    private String userAgent;

    @ApiModelProperty("备注")
    private String remark;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFileKey() {
        return fileKey;
    }
    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }
    public String getOriginalName() {
        return originalName;
    }
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }
    public String getFileSuffix() {
        return fileSuffix;
    }
    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
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
    public String getOperationType() {
        return operationType;
    }
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
    public Integer getOperationCount() {
        return operationCount;
    }
    public void setOperationCount(Integer operationCount) {
        this.operationCount = operationCount;
    }
    public String getBucket() {
        return bucket;
    }
    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public LocalDateTime getCreateAt() {
        return createAt;
    }
    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
    public LocalDateTime getUpdateAt() {
        return updateAt;
    }
    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getUserAgent() {
        return userAgent;
    }
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }

}
