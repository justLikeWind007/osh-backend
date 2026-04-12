package com.backstage.system.utils;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class FileSizeConvertUtilTest {

    @Test
    public void shouldConvertBytesToKbWithHalfUpRounding() {
        Assert.assertEquals(Long.valueOf(2L), FileSizeConvertUtil.convertBytesToKb(new BigDecimal("1536")));
        Assert.assertEquals(Long.valueOf(1L), FileSizeConvertUtil.convertBytesToKb(new BigDecimal("1024")));
        Assert.assertEquals(Long.valueOf(1L), FileSizeConvertUtil.convertBytesToKb(new BigDecimal("512")));
        Assert.assertNull(FileSizeConvertUtil.convertBytesToKb(null));
    }
}
