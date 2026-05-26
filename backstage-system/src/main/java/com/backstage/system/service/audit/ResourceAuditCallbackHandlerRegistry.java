package com.backstage.system.service.audit;

import com.backstage.common.enums.ResourceTypeEnum;
import com.backstage.common.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 资源审核完成回调处理器注册表
 */
@Component
public class ResourceAuditCallbackHandlerRegistry {

    private static final Logger log = LoggerFactory.getLogger(ResourceAuditCallbackHandlerRegistry.class);

    private final Map<ResourceTypeEnum, ResourceAuditCallbackHandler> handlerMap;

    public ResourceAuditCallbackHandlerRegistry(List<ResourceAuditCallbackHandler> handlers) {
        this.handlerMap = new EnumMap<>(ResourceTypeEnum.class);
        for (ResourceAuditCallbackHandler handler : handlers) {
            List<ResourceTypeEnum> resourceTypes = handler.resourceTypes();
            if (resourceTypes == null || resourceTypes.isEmpty()) {
                throw new ServiceException("资源审核回调处理器资源类型不能为空");
            }
            for (ResourceTypeEnum resourceType : resourceTypes) {
                handlerMap.put(resourceType, handler);
                log.info("注册资源审核回调处理器成功, resourceType={}, handler={}",
                        resourceType.getType(), handler.getClass().getSimpleName());
            }
        }
    }

    public ResourceAuditCallbackHandler getHandler(ResourceTypeEnum resourceType) {
        ResourceAuditCallbackHandler handler = handlerMap.get(resourceType);
        if (handler == null) {
            throw new ServiceException("未找到资源审核回调处理器, resourceType=" + resourceType.getType());
        }
        return handler;
    }

    public void handle(ResourceAuditCallbackContext context) {
        getHandler(context.getResourceType()).handle(context);
    }
}
