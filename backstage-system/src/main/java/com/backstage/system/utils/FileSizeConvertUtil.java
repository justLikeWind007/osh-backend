package com.backstage.system.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 文件大小转换工具
 */
public final class FileSizeConvertUtil {

    private static final BigDecimal BYTES_PER_KB = BigDecimal.valueOf(1024);

    private FileSizeConvertUtil() {
    }

    public static Long convertBytesToKb(BigDecimal fileSizeInBytes) {
        if (fileSizeInBytes == null) {
            return null;
        }
        return fileSizeInBytes.divide(BYTES_PER_KB, 0, RoundingMode.HALF_UP)
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
    }
}
