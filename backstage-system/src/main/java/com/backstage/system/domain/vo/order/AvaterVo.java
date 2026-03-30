package com.backstage.system.domain.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel("头像返回实体")
public class AvaterVo {

    @ApiModelProperty("头像地址")
    private String url;

    @ApiModelProperty("头像名称")
    private String fileName;

    @ApiModelProperty("图片大小")
    private String fileSize;

    @ApiModelProperty("图片类型")
    private String fileType;

    @ApiModelProperty("url")
    private String data;

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFileSize() {
        return fileSize;
    }
    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
    public String getFileType() {
        return fileType;
    }
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }

}
