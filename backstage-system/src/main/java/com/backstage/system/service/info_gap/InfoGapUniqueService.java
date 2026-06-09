package com.backstage.system.service.info_gap;

public interface InfoGapUniqueService {

    /**
     * 生成一个数据库中不存在的唯一标签
     */
    String generateUniqueNo();

    /**
     * 为信息差记录创建唯一标签映射
     */
    String createUniqueRecord(Long infoGapId);
}
