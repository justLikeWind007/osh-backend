package com.backstage.common.utils.generate;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/12
 * Time: 17:09
 */
public class GenerateUtil {
    private static final DefaultIdentifierGenerator IDENTIFIER_GENERATOR = new DefaultIdentifierGenerator();

    /**
     * 生成雪花算法ID
     * 效果与 MyBatis-Plus 的 @TableId(type = IdType.ASSIGN_ID) 相同
     *
     * @return Long 类型的唯一ID
     */
    public static Long generateSnowflakeId() {
        return IDENTIFIER_GENERATOR.nextId(null);
    }
}
