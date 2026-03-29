package com.backstage.system.domain.course;

import java.util.Date;

/**
 * 课程资料实体对象 osh_course_material
 * 
 * @author ruoyi
 * @date 2026-03-24
 */
public class OshCourseMaterial {
    private static final long serialVersionUID = 1L;

    /** 资料 ID */
    private Long id;

    /** 课程 ID */
    private Long courseId;

    /** 章节 ID */
    private Long sectionId;

    /** 资料名称 */
    private String materialName;

    /** 文件 URL */
    private String fileUrl;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 文件类型（扩展名） */
    private String fileType;

    /** 是否仅付费可见（0-公开，1-仅付费） */
    private Integer isPayOnly;

    /** 排序 */
    private Integer sort;

    /** 下载次数 */
    private Integer downloadCount;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private Date updateTime;

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

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
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

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
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

    @Override
    public String toString() {
        return "OshCourseMaterial{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", sectionId=" + sectionId +
                ", materialName='" + materialName + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", fileSize=" + fileSize +
                ", fileType='" + fileType + '\'' +
                ", isPayOnly=" + isPayOnly +
                ", sort=" + sort +
                ", downloadCount=" + downloadCount +
                ", createBy='" + createBy + '\'' +
                ", createTime=" + createTime +
                ", updateBy='" + updateBy + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}
