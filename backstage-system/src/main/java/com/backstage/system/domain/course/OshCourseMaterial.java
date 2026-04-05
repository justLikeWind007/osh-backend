package com.backstage.system.domain.course;

import java.util.Date;

/**
 * @Author: hope
 * @createTime: 2026年04月01日 18:42:52
 * @version:
 * @Description:
 */
public class OshCourseMaterial {

    private Long id;
    private Long courseId;
    private String name;
    private String url;

    private Long fileSize;
    private String fileType;
    private Integer downloadCount;
    private Integer payOnly;

    private Integer sort;
    private Integer deleteFlag;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;


    public OshCourseMaterial() {
    }

    public OshCourseMaterial(Long id, Long courseId, String name, String url, Long fileSize, String fileType, Integer downloadCount, Integer payOnly, Integer sort, Integer deleteFlag, String createBy, Date createTime, String updateBy, Date updateTime) {
        this.id = id;
        this.courseId = courseId;
        this.name = name;
        this.url = url;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.downloadCount = downloadCount;
        this.payOnly = payOnly;
        this.sort = sort;
        this.deleteFlag = deleteFlag;
        this.createBy = createBy;
        this.createTime = createTime;
        this.updateBy = updateBy;
        this.updateTime = updateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

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

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Integer getPayOnly() {
        return payOnly;
    }

    public void setPayOnly(Integer payOnly) {
        this.payOnly = payOnly;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
