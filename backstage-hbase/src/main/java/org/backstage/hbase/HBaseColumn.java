package org.backstage.hbase;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HBaseColumn {

    /**
     * 列族
     *
     * @return 列族
     */
    String family();

    /**
     * 列名
     *
     * @return 列名
     */
    String qualifier();
}