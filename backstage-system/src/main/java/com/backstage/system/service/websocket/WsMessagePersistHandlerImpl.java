package com.backstage.system.service.websocket;

import com.backstage.common.websocket.WsMessagePersistHandler;
import com.backstage.system.domain.websocket.WsNotifyMessage;
import com.backstage.system.mapper.websocket.OshWsNotificationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * WebSocket 消息持久化实现
 * 复制一份新对象写库，不修改原始 payload，避免影响序列化推送
 */
@Component
public class WsMessagePersistHandlerImpl implements WsMessagePersistHandler {

    private static final Logger log = LoggerFactory.getLogger(WsMessagePersistHandlerImpl.class);

    @Autowired
    private OshWsNotificationMapper notificationMapper;

    @Override
    public void persist(Long targetUserId, Object payload) {
        if (!(payload instanceof WsNotifyMessage)) {
            log.warn("WS 持久化跳过：payload 类型不是 WsNotifyMessage，实际类型={}",
                    payload.getClass().getName());
            return;
        }
        WsNotifyMessage src = (WsNotifyMessage) payload;

        // 复制新对象写库，不污染原始 payload
        WsNotifyMessage record = new WsNotifyMessage();
        record.setTargetUserId(targetUserId);
        record.setType(src.getType());
        record.setTitle(src.getTitle());
        record.setContent(src.getContent());
        record.setJumpUrl(src.getJumpUrl());
        record.setBizId(src.getBizId());

        notificationMapper.insert(record);
        log.debug("WS 消息已持久化，targetUserId={}, type={}", targetUserId, src.getType());
    }
}
