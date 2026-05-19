package org.backstage.hbase;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HBaseTable {

    /**
     * 表名
     *
     * @return 表名
     */
    String tableName();

    /**
     * 列族
     *
     * @return 列族
     */
    String defaultFamily() default "info";
}