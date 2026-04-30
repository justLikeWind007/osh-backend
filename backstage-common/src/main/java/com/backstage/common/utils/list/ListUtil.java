package com.backstage.common.utils.list;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/27
 * Time: 21:38
 */
public class ListUtil {

    /**
     * List<Long> 转 逗号分隔字符串
     */
    public static String listToString(List<Long> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return list.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    /**
     * 逗号分隔字符串 转 List<Long>
     */
    public static List<Long> stringToList(String str) {
        if (str == null || str.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(str.split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }
}
