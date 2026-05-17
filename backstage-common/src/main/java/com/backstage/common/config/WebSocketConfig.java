package com.backstage.common.config;

import com.backstage.common.utils.jwt.JwtUtil;
import com.backstage.common.websocket.OshWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * 原生 WebSocket 配置
 * 连接地址：ws://host/ws/connect?token=xxx
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    private OshWebSocketHandler oshWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(oshWebSocketHandler, "/ws/connect")
                .addInterceptors(new TokenHandshakeInterceptor())
                .setAllowedOriginPatterns("*");
    }

    /**
     * 握手拦截器：从 ?token=xxx 解析 userId，存入 session attributes
     */
    private static class TokenHandshakeInterceptor implements HandshakeInterceptor {

        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
            String query = request.getURI().getQuery();
            String token = extractToken(query);
            if (!StringUtils.hasText(token)) {
                log.warn("WS 握手拒绝：未携带 token");
                return false;
            }
            try {
                Long userId = JwtUtil.getUserIdByToken(token);
                if (userId == null) {
                    log.warn("WS 握手拒绝：token 无效");
                    return false;
                }
                attributes.put("userId", userId);
                log.info("WS 握手成功，userId={}", userId);
                return true;
            } catch (Exception e) {
                log.warn("WS 握手拒绝：{}", e.getMessage());
                return false;
            }
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        }

        private String extractToken(String query) {
            if (!StringUtils.hasText(query)) return null;
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) return param.substring(6);
            }
            return null;
        }
    }
}
