package com.backstage.system.domain.course.vo;

public class CourseSearchUserStateVo {

    private Long courseId;
    private Integer collectionFlag;
    private Integer buyFlag;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getCollectionFlag() {
        return collectionFlag;
    }

    public void setCollectionFlag(Integer collectionFlag) {
        this.collectionFlag = collectionFlag;
    }

    public Integer getBuyFlag() {
        return buyFlag;
    }

    public void setBuyFlag(Integer buyFlag) {
        this.buyFlag = buyFlag;
    }
}
