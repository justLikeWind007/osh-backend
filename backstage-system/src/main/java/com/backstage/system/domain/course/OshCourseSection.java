package com.backstage.system.domain.course;

import io.swagger.annotations.ApiModel;

/**
 * @Author:
 * @createTime: 2026年03月29日 20:55:05
 * @version:
 * @Description:
 */
public class OshCourseSection {
    private Long id;

    private Long courseId;

    private Long parentId;

    private String title;

    private Integer sort;

    private Integer isFree;

    private Integer duration;

    private String mediaUrl;

    private String type;

    private Integer status;

    private Long examId;

    private Integer deleteFlag;


}
