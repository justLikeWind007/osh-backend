package com.backstage.system.utils;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

public class IpUtils {

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

    /**
     * 无需参数，直接获取客户端真实IP
     */
    public static String getClientIp() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }
        return getClientIp(request);
    }

    /**
     * 带request参数的版本（保留兼容性）
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = null;

        String[] ipHeaders = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String header : ipHeaders) {
            ip = request.getHeader(header);
            if (isValidIp(ip)) {
                ip = getFirstIp(ip);
                if (isValidIp(ip)) {
                    return ip;
                }
            }
        }

        ip = request.getRemoteAddr();
        return LOCALHOST_IPV6.equals(ip) ? LOCALHOST_IPV4 : ip;
    }

    /**
     * 获取当前请求对象
     */
    private static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private static boolean isValidIp(String ip) {
        return StringUtils.hasText(ip) && !UNKNOWN.equalsIgnoreCase(ip);
    }

    private static String getFirstIp(String ip) {
        if (ip != null && ip.contains(",")) {
            String[] ips = ip.split(",");
            for (String i : ips) {
                String trimmedIp = i.trim();
                if (isValidIp(trimmedIp)) {
                    return trimmedIp;
                }
            }
        }
        return ip != null ? ip.trim() : null;
    }
}