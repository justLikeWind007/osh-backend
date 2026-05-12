package com.backstage.system.service.websocket;

import com.backstage.common.websocket.OshWebSocketHandler;
import com.backstage.system.domain.websocket.WsNotifyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * WebSocket 推送服务
 *
 * 通用入口：send(userId, WsNotifyMessage)
 * 各业务场景提供语义化方法，封装查询和判断逻辑，业务层只需传入原始业务参数。
 */
@Service
public class WebSocketNotifyService {

    private static final int SUMMARY_MAX_LEN = 50;

    @Autowired
    private OshWebSocketHandler webSocketHandler;

    // ── 通用入口 ──────────────────────────────────────────────────────────────

    /**
     * 推送消息给指定用户（通用入口）
     */
    public void send(Long targetUserId, WsNotifyMessage message) {
        webSocketHandler.sendToUser(targetUserId, message);
    }

    /**
     * 广播消息给所有在线用户（不持久化）
     * 适用于公告、系统通知等场景
     */
    public void broadcast(WsNotifyMessage message) {
        webSocketHandler.broadcast(message);
    }

    // ── 工具方法 ──────────────────────────────────────────────────────────────

    /**
     * 截取摘要，超过最大长度时加省略号
     * 供调用方构建 WsNotifyMessage 时使用
     */
    public String truncate(String text) {
        if (text == null) return "";
        return text.length() <= SUMMARY_MAX_LEN ? text : text.substring(0, SUMMARY_MAX_LEN) + "...";
    }
}
