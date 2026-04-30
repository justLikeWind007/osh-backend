package com.backstage.system.constants;

/**
 * 课程评价相关常量
 * 包括：评价类型、评分常量等
 * 
 * @author ruoyi
 */
public final class CourseReviewConstants {

    /**
     * 评价统计字段名常量
     */
    public static final String FIELD_GOOD_COUNT = "goodCount";
    public static final String FIELD_MEDIUM_COUNT = "mediumCount";
    public static final String FIELD_BAD_COUNT = "badCount";
    public static final String FIELD_TOTAL_COUNT = "totalCount";
    public static final String FIELD_AVERAGE_RATING = "averageRating";
    
    public static final String DB_FIELD_GOOD_COUNT = "good_count";
    public static final String DB_FIELD_MEDIUM_COUNT = "medium_count";
    public static final String DB_FIELD_BAD_COUNT = "bad_count";
    public static final String DB_FIELD_TOTAL_COUNT = "total_count";
    public static final String DB_FIELD_AVERAGE_RATING = "average_rating";

    /**
     * 默认评价统计值
     */
    public static final Integer DEFAULT_COUNT = 0;
    public static final Double DEFAULT_AVERAGE_RATING = 0.0;

    /**
     * 内部使用的字段名常量（用于 Map 操作）
     */
    public static final String FIELD_COURSE_ID = "courseId";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_RATING = "rating";
    public static final String FIELD_REVIEW_CONTENT = "reviewContent";

    private CourseReviewConstants() {
    }
}
