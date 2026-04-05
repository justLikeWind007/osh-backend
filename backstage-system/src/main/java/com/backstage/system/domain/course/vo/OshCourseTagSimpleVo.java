package com.backstage.system.domain.course.vo;

public class OshCourseTagSimpleVo {

    private Long id;

    private String name;

    private Integer sort;

    public OshCourseTagSimpleVo() {
    }

    public OshCourseTagSimpleVo(Long id, String name, Integer sort) {
        this.id = id;
        this.name = name;
        this.sort = sort;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
