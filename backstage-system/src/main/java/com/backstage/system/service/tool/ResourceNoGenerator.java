package com.backstage.system.service.tool;

import com.backstage.common.enums.ResourceCodePrefixEnum;

/**
 * 资源编号生成器
 */
public interface ResourceNoGenerator {

    /**
     * 生成数据库中不存在的资源编号
     *
     * @param prefixEnum 前缀枚举
     * @param duplicateChecker 判重检查器
     * @return 唯一资源编号
     */
    String generateUniqueNo(ResourceCodePrefixEnum prefixEnum, ResourceNoDuplicateChecker duplicateChecker);
}
