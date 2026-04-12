package com.backstage.common.annotation;

import com.backstage.common.constant.KafkaConstants;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/12
 * Time: 12:02
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OshUserActionLog {
    /**
     * 业务模块
     */
    String module() default "";
    /**
     * 是否记录参数
     */
    boolean recordArgs() default true;
    /**
     * 操作类型
     */
    String actionType() default "";
    /**
     * 操作描述
     */
    String description() default "";
    /**
     * Kafka topic
     */
    String topic() default KafkaConstants.USER_ACTION_TOPIC;
}
