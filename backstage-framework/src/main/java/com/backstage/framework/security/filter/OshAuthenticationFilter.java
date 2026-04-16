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

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/4/13
 * Time: 14:41
 */
@Component
public class OshAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private RedisCache redisCache;

    @Autowired
    private OshUserMapper oshUserMapper;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final Set<String> WHITE_LIST = new HashSet<>(Arrays.asList(
            "/pc/user/login",
            "/pc/user/register/submit",
            "/pc/user/register/verity",
            "/pc/user/forget"
    ));


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (isWhiteList(uri)) {
            chain.doFilter(request, response);
            return;
        }
        String token = request.getHeader(OshUserConstants.TOKEN);
        Long userId;
        // 开发阶段，无论登录成功与否，都放行
        try {
            userId = JwtUtil.getUserIdByToken(token);
        } catch (Exception e) {
            chain.doFilter(request, response);
            return;
        }


        Map<String, Object> userInfoMap = redisCache.getCacheObject(OshUserConstants.LOGIN_USER + userId);

        if (userInfoMap != null) {
            if (userId != null) {
                ThreadLocalUtil.set(OshUserConstants.USER_ID, userId);
                ThreadLocalUtil.set(OshUserConstants.LEVEL, ((List)userInfoMap.get(OshUserConstants.ROLE)).get(2));
                LambdaQueryWrapper<OshUser> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(OshUser::getId, userId);
                OshUser oshUser = oshUserMapper.selectOne(wrapper);
                ThreadLocalUtil.set(OshUserConstants.USER_INFO, oshUser);
                OshUserDetail oshUserDetail = new OshUserDetail();
                oshUserDetail.setUserInfoMap(userInfoMap);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        oshUserDetail,
                        null,
                        oshUserDetail.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            ThreadLocalUtil.remove();
        }
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
