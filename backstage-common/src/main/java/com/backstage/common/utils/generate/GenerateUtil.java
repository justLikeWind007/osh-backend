package com.backstage.common.utils.generate;

import com.backstage.common.enums.ResourceCodePrefixEnum;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/12
 * Time: 17:09
 */
public class GenerateUtil {
    private static final DefaultIdentifierGenerator IDENTIFIER_GENERATOR = new DefaultIdentifierGenerator();

    private static final char[] LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final char[] DIGITS = "0123456789".toCharArray();

    /**
     * 生成雪花算法ID
     * 效果与 MyBatis-Plus 的 @TableId(type = IdType.ASSIGN_ID) 相同
     *
     * @return Long 类型的唯一ID
     */
    public static Long generateSnowflakeId() {
        return IDENTIFIER_GENERATOR.nextId(null);
    }

    /**
     * 生成资源编号。
     * 格式：前缀(2位小写字母) + 2位字母(大小写) + 2位数字 + 2位字母(大小写)
     * 示例：ebAb34dF（eb 为资源类型前缀，Ab34dF 为随机部分）
     *
     * @param resourceType 资源类型枚举
     * @return 8位资源编号
     */
    public static String generateResourceCode(ResourceCodePrefixEnum resourceType) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder(8);

        // 前缀：2位小写字母（来自枚举）
        sb.append(resourceType.getPrefix());

        // 后6位：2字母 + 2数字 + 2字母
        sb.append(LETTERS[random.nextInt(LETTERS.length)]);
        sb.append(LETTERS[random.nextInt(LETTERS.length)]);
        sb.append(DIGITS[random.nextInt(DIGITS.length)]);
        sb.append(DIGITS[random.nextInt(DIGITS.length)]);
        sb.append(LETTERS[random.nextInt(LETTERS.length)]);
        sb.append(LETTERS[random.nextInt(LETTERS.length)]);

        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(generateResourceCode(ResourceCodePrefixEnum.COURSE));
    }
}
