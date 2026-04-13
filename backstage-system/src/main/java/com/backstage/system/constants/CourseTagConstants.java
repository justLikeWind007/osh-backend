package com.backstage.system.constants;

/**
 * 课程标签相关常量
 * 包括：标签默认值、排序值等
 * 
 * @author ruoyi
 */
public final class CourseTagConstants {

    /**
     * 标签默认值常量
     */
    // 默认排序值
    public static final Integer DEFAULT_SORT = 0;
    
    // 默认状态：1 表示启用，0 表示禁用
    public static final Integer DEFAULT_STATUS_ENABLED = 1;
    public static final Integer DEFAULT_STATUS_DISABLED = 0;
    
    // 默认使用次数
    public static final Integer DEFAULT_USE_COUNT = 0;

    /**
     * 标签状态常量
     */
    // 启用
    public static final Integer STATUS_ENABLED = 1;
    
    // 禁用
    public static final Integer STATUS_DISABLED = 0;

    /**
     * 内部使用的字段名常量（用于 Map 操作）
     */
    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_SORT = "sort";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_USE_COUNT = "useCount";
    public static final String FIELD_CREATE_BY = "createBy";
    public static final String FIELD_UPDATE_BY = "updateBy";
    public static final String FIELD_CREATE_TIME = "createTime";
    public static final String FIELD_UPDATE_TIME = "updateTime";

    private CourseTagConstants() {
    }
}
