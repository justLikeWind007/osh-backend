package org.backstage.hbase;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

class HBaseUtils {

    public static String getTableName(Class<?> clazz) {
        return clazz.getAnnotation(HBaseTable.class).tableName();
    }

    public static String getRowKey(Object obj) throws Exception {
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(HBaseRowKey.class)) {
                field.setAccessible(true);
                return field.get(obj).toString();
            }
        }
        throw new Exception("未找到@RowKey字段");
    }

    public static Map<String, byte[]> getColumnValues(Object obj) throws Exception {
        Map<String, byte[]> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        String defaultFamily = clazz.getAnnotation(HBaseTable.class).defaultFamily();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(HBaseColumn.class)) {
                HBaseColumn col = field.getAnnotation(HBaseColumn.class);
                field.setAccessible(true);
                Object val = field.get(obj);
                if (val == null) continue;

                String family = col.family().isEmpty() ? defaultFamily : col.family();
                String key = family + ":" + col.qualifier();
                map.put(key, val.toString().getBytes());
            }
        }
        return map;
    }

    public static <T> T mapToEntity(Map<String, String> data, Class<T> clazz) throws Exception {
        T entity = clazz.newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(HBaseColumn.class)) {
                HBaseColumn col = field.getAnnotation(HBaseColumn.class);
                String family = col.family().isEmpty() ? clazz.getAnnotation(HBaseTable.class).defaultFamily() : col.family();
                String key = family + ":" + col.qualifier();
                if (data.containsKey(key)) {
                    BeanUtils.setProperty(entity, field.getName(), data.get(key));
                }
            }
        }
        return entity;
    }

    public static String getQualifiedColumnName(String family, String qualifier) {
        return family + ":" + qualifier;
    }
}