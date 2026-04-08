package com.backstage.system.interceptor;

import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.common.utils.jwt.JwtUtil;
import com.backstage.system.domain.user.User;
import com.backstage.system.mapper.user.OshUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/30
 * Time: 14:37
 */
@Component
public class UserContextInterceptor implements HandlerInterceptor {

    @Autowired
    private OshUserMapper oshUserMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 获取请求头 token
        String token = request.getHeader(OshUserConstants.TOKEN);

        // 2. 只有 token 不为空时才尝试解析
        if (StringUtils.hasText(token)) {
            try {
                Long userId = JwtUtil.getUserIdByToken(token);
                if (userId != null) {
                    User user = oshUserMapper.getUserInfoById(userId);
                    ThreadLocalUtil.set(OshUserConstants.USER_ID, userId);
                    ThreadLocalUtil.set(OshUserConstants.USER_INFO, user);
                    ThreadLocalUtil.set(OshUserConstants.TOKEN, token);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 无论是否有 token，都继续执行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理 ThreadLocal，防止内存泄漏
        ThreadLocalUtil.remove();
    }
}
