package com.backstage.framework.security.filter;

import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.core.redis.RedisCache;
import com.backstage.common.utils.jwt.JwtUtil;
import com.backstage.framework.config.properties.PermitAllUrlProperties;
import com.backstage.system.domain.user.OshPermission;
import com.backstage.system.domain.user.OshRole;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.mapper.user.OshPermissionMapper;
import com.backstage.system.mapper.user.OshRoleMapper;
import com.backstage.system.mapper.user.OshUserMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OshAuthenticationFilterRiskFixTest {

    private static final Long USER_ID = 2002L;

    @InjectMocks
    private TestableOshAuthenticationFilter filter;

    @Mock
    private RedisCache redisCache;

    @Mock
    private OshUserMapper oshUserMapper;

    @Mock
    private OshRoleMapper oshRoleMapper;

    @Mock
    private OshPermissionMapper oshPermissionMapper;

    @Mock
    private PermitAllUrlProperties permitAllUrl;

    @Before
    public void setUp() {
        new JwtUtil().setSecret("abcdefghijklmnopqrstuvwxyz1234567890");
        when(permitAllUrl.getUrls()).thenReturn(Collections.emptyList());
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void rejectsStaleTokenBeforeRefreshingContext() throws Exception {
        String requestToken = token();
        Map<String, Object> cache = new HashMap<>();
        cache.put(OshUserConstants.TOKEN, "different-token");
        when(redisCache.getCacheObject(OshUserConstants.LOGIN_USER + USER_ID)).thenReturn(cache);

        MockHttpServletResponse response = doFilter(requestToken);

        assertEquals(401, response.getStatus());
        verify(oshRoleMapper, never()).getRoleIdsByUserId(any());
    }

    @Test
    public void refreshesPermissionsFromEffectiveRoles() throws Exception {
        String requestToken = token();
        Map<String, Object> cache = new HashMap<>();
        cache.put(OshUserConstants.TOKEN, requestToken);
        cache.put(OshUserConstants.PERMISSION, stalePermission());
        when(redisCache.getCacheObject(OshUserConstants.LOGIN_USER + USER_ID)).thenReturn(cache);
        when(oshRoleMapper.getRoleIdsByUserId(USER_ID)).thenReturn(Collections.singletonList(3));
        when(oshRoleMapper.selectList(any(Wrapper.class))).thenReturn(Collections.singletonList(role(3, "VIP用户", "vip", 3)));
        when(oshPermissionMapper.selectPermissionIdsByRoleIds(Collections.singletonList(3))).thenReturn(Collections.singletonList(101));
        when(oshPermissionMapper.selectList(any(Wrapper.class))).thenReturn(Collections.singletonList(permission(101, "user:info:update", 0)));
        when(oshUserMapper.selectOne(any(Wrapper.class))).thenReturn(user());

        MockHttpServletResponse response = doFilter(requestToken);

        assertEquals(200, response.getStatus());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toList())
                .contains("user:info:update"));
        assertFalse(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toList())
                .contains("user:asset:update"));
        verify(redisCache).setCacheObject(eq(OshUserConstants.LOGIN_USER + USER_ID), eq(cache), eq(500), eq(TimeUnit.MINUTES));
        assertEquals("vip", ((Map<String, String>) cache.get(OshUserConstants.ROLE)).get("roleCode"));
    }

    private MockHttpServletResponse doFilter(String token) throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/pc/user/getinfo");
        request.addHeader(OshUserConstants.TOKEN, token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilterInternal(request, response, new MockFilterChain());
        return response;
    }

    private String token() {
        Map<String, Object> claims = new HashMap<>();
        claims.put(OshUserConstants.USER_ID, USER_ID);
        claims.put(OshUserConstants.USERNAME, "tester");
        return JwtUtil.createToken(claims);
    }

    private Map<String, java.util.List<String>> stalePermission() {
        Map<String, java.util.List<String>> permission = new HashMap<>();
        permission.put("user", Arrays.asList("user:asset:update"));
        return permission;
    }

    private OshRole role(Integer id, String name, String code, Integer level) {
        OshRole role = new OshRole();
        role.setId(id);
        role.setRoleName(name);
        role.setRoleCode(code);
        role.setLevel(level);
        return role;
    }

    private OshPermission permission(Integer id, String code, Integer parentId) {
        OshPermission permission = new OshPermission();
        permission.setId(id);
        permission.setPermissionCode(code);
        permission.setParentId(parentId);
        return permission;
    }

    private OshUser user() {
        OshUser user = new OshUser();
        user.setId(USER_ID);
        user.setUsername("tester");
        return user;
    }

    static class TestableOshAuthenticationFilter extends OshAuthenticationFilter {
        @Override
        public void doFilterInternal(javax.servlet.http.HttpServletRequest request,
                                     javax.servlet.http.HttpServletResponse response,
                                     javax.servlet.FilterChain chain) throws ServletException, IOException {
            super.doFilterInternal(request, response, chain);
        }
    }
}
