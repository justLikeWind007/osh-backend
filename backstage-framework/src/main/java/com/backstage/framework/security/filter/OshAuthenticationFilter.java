package com.backstage.framework.security.filter;

import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.core.redis.RedisCache;
import com.backstage.common.core.domain.model.OshUserDetail;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.common.utils.jwt.JwtUtil;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.mapper.user.OshUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * OSH 前台用户认证过滤器
 * 无 Token 或 Token 无效时拒绝访问（返回 401）
 * WebSocket 握手请求放行（由 WebSocket 拦截器单独处理认证）
 */
@Component
public class OshAuthenticationFilter extends OncePerRequestFilter {
    private static final int LOGIN_EXPIRE_MINUTES = 500;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private OshUserMapper oshUserMapper;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final Set<String> WHITE_LIST = new HashSet<>(Arrays.asList(
            "/pc/user/login",
            "/pc/user/register/submit",
            "/pc/user/register/verity",
            "/pc/user/forget",
            "/public/**",
            // 秒杀公告栏 & 购买动态，公开展示，无需登录
            "/pc/seckill/user/announcement/notices",
            "/pc/seckill/user/recent/orders"
            "/tool/search",
            "/tool/recommend",
            "/tool/tags",
            "/tool/detail/*",
            "/tool/view/*",
            "/pc/tool/search",
            "/pc/tool/recommend",
            "/pc/tool/tags",
            "/pc/tool/detail/*",
            "/pc/tool/view/*",
            "/public/**"
    ));

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        // 白名单放行
        if (isWhiteList(uri)) {
            chain.doFilter(request, response);
            return;
        }

        // WebSocket 握手请求放行（由 WebSocket 拦截器单独处理认证）
        if (isWebSocketHandshake(request)) {
            chain.doFilter(request, response);
            return;
        }

        // 解析 Token
        String token = request.getHeader(OshUserConstants.TOKEN);
        Long userId;
        try {
            userId = JwtUtil.getUserIdByToken(token);
        } catch (Exception e) {
            sendUnauthorized(response, "未登录或Token已过期");
            return;
        }

        if (userId == null) {
            sendUnauthorized(response, "未登录或Token已过期");
            return;
        }

        // 检查 Redis 登录态
        String loginUserKey = OshUserConstants.LOGIN_USER + userId;
        Map<String, Object> userInfoMap = redisCache.getCacheObject(loginUserKey);

        if (userInfoMap == null) {
            sendUnauthorized(response, "登录已过期，请重新登录");
            return;
        }

        // 顺延 Redis 会话 TTL
        redisCache.expire(loginUserKey, LOGIN_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 设置 ThreadLocal 上下文
        ThreadLocalUtil.set(OshUserConstants.USER_ID, userId);
        Map<String, String> role = (Map<String, String>) userInfoMap.get(OshUserConstants.ROLE);
        ThreadLocalUtil.set(OshUserConstants.LEVEL, role.get(OshUserConstants.LEVEL));
        ThreadLocalUtil.set(OshUserConstants.ROLE_CODE, role.get(OshUserConstants.ROLE_CODE));

        LambdaQueryWrapper<OshUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OshUser::getId, userId);
        OshUser oshUser = oshUserMapper.selectOne(wrapper);
        ThreadLocalUtil.set(OshUserConstants.USER_INFO, oshUser);
        ThreadLocalUtil.set(OshUserConstants.USERNAME, oshUser.getUsername());

        // 设置 Spring Security 认证上下文
        OshUserDetail oshUserDetail = new OshUserDetail();
        oshUserDetail.setUserInfoMap(userInfoMap);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                oshUserDetail,
                null,
                oshUserDetail.getAuthorities()
        );
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        try {
            chain.doFilter(request, response);
        } finally {
            ThreadLocalUtil.remove();
        }
    }

    /**
     * 返回 401 未认证响应
     */
    private void sendUnauthorized(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"msg\":\"" + msg + "\"}");
    }

    /**
     * 判断是否为 WebSocket 握手请求
     */
    private boolean isWebSocketHandshake(HttpServletRequest request) {
        String upgrade = request.getHeader("Upgrade");
        return "websocket".equalsIgnoreCase(upgrade);
    }

    private boolean isWhiteList(String uri) {
        for (String pattern : WHITE_LIST) {
            if (pathMatcher.match(pattern, uri)) {
                return true;
            }
        }
        return false;
    }
}
