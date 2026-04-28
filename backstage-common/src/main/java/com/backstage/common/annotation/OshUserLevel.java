package com.backstage.common.annotation;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/18
 * Time: 14:37
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OshUserLevel {
    /**
     * 所需等级
     */
    int value() default 1;
}
