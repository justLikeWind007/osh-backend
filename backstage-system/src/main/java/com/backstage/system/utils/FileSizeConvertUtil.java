package com.backstage.system.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 文件大小转换工具
 */
public final class FileSizeConvertUtil {

    private static final BigDecimal KB_PER_MB = BigDecimal.valueOf(1024);

    private FileSizeConvertUtil() {
    }

    public static Long convertMbToKb(BigDecimal fileSizeInMb) {
        if (fileSizeInMb == null) {
            return null;
        }
        return fileSizeInMb.multiply(KB_PER_MB)
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
    }
}
