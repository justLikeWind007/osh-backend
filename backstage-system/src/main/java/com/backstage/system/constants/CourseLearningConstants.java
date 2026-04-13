package com.backstage.system.constants;

/**
 * 课程学习进度相关常量
 * 包括：学习状态码、完成度值、播放位置等
 * 
 * @author ruoyi
 */
public final class CourseLearningConstants {

    /**
     * 学习状态码常量
     */
    // 未开始
    public static final Integer STATUS_NOT_STARTED = 0;
    
    // 学习中
    public static final Integer STATUS_LEARNING = 1;
    
    // 暂停中
    public static final Integer STATUS_PAUSED = 2;
    
    // 已完成
    public static final Integer STATUS_COMPLETED = 3;

    /**
     * 学习进度相关常量
     */
    // 完成度百分比阈值：100% 表示完成
    public static final Integer PROGRESS_COMPLETED_THRESHOLD = 100;
    
    // 初始进度
    public static final Integer INITIAL_PROGRESS = 0;
    
    // 初始播放位置
    public static final Integer INITIAL_LAST_POSITION = 0;
    
    // 初始学习时长（秒）
    public static final Integer INITIAL_LEARN_TIME = 0;

    /**
     * 完成状态标记
     */
    // 已完成标记：1 表示已完成，0 表示未完成
    public static final Integer COMPLETED_FLAG_YES = 1;
    public static final Integer COMPLETED_FLAG_NO = 0;

    /**
     * 观看次数初始值
     */
    public static final Integer INITIAL_WATCH_COUNT = 0;

    /**
     * 内部使用的字段名常量（用于 Map 操作）
     */
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_COURSE_ID = "courseId";
    public static final String FIELD_SECTION_ID = "sectionId";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_PROGRESS = "progress";
    public static final String FIELD_LAST_POSITION = "lastPosition";
    public static final String FIELD_LEARN_TIME = "learnTime";
    public static final String FIELD_IS_COMPLETED = "isCompleted";
    public static final String FIELD_UPDATE_TIME = "updateTime";
    public static final String FIELD_UPDATE_BY = "updateBy";

    private CourseLearningConstants() {
    }
}
