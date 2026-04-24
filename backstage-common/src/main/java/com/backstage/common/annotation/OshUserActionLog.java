package com.backstage.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户行为日志注解。
 * 当前仅用于保持编译通过，运行时如有对应切面可直接接入。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OshUserActionLog {

    String module() default "";

    String actionType() default "";

    String description() default "";
}
