package com.backstage.common.websocket;

/**
 * WebSocket 消息持久化回调接口
 * 由 backstage-system 模块实现并注入到 OshWebSocketHandler
 * 保持模块依赖方向：common 不依赖 system
 */
public interface WsMessagePersistHandler {

    /**
     * 持久化推送消息
     *
     * @param targetUserId 接收消息的用户 ID
     * @param payload      消息对象（WsNotifyMessage）
     */
    void persist(Long targetUserId, Object payload);
}
