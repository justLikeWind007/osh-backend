package com.backstage.system.service.audit;

import com.backstage.common.enums.ResourceTypeEnum;

import java.util.List;

/**
 * 资源审核完成回调处理器
 */
public interface ResourceAuditCallbackHandler {

    /**
     * 回调处理器支持的资源类型列表
     *
     * @return 资源类型列表
     */
    List<ResourceTypeEnum> resourceTypes();

    /**
     * 审核完成后的异步回调
     *
     * @param context 审核上下文
     */
    void handle(ResourceAuditCallbackContext context);
}
