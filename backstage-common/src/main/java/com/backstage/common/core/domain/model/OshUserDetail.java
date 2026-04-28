package com.backstage.common.core.domain.model;

import com.backstage.common.constant.OshUserConstants;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/12
 * Time: 22:24
 */
@Component
public class OshUserDetail implements UserDetails {
    private Map<String, Object> userInfoMap;

    public void setUserInfoMap(Map<String, Object> userInfoMap) {
        this.userInfoMap = userInfoMap;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (userInfoMap == null) {
            return new ArrayList<>();
        }
        Map<String, List<String>> permission = (Map<String, List<String>>) userInfoMap.get(OshUserConstants.PERMISSION);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (permission != null) {
            for (Map.Entry<String, List<String>> entry : permission.entrySet()) {
                // 1. 添加父权限
                String parentCode = entry.getKey();
                if ("@type".equals(parentCode)) {
                    continue;
                }
                if (StringUtils.hasText(parentCode)) {
                    authorities.add(new SimpleGrantedAuthority(parentCode));
                }

                // 2. 添加子权限
                List<String> childCodes = entry.getValue();
                if (childCodes != null && !childCodes.isEmpty()) {
                    for (String childCode : childCodes) {
                        if (StringUtils.hasText(childCode)) {
                            authorities.add(new SimpleGrantedAuthority(childCode));
                        }
                    }
                }
            }
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        if (userInfoMap != null) {
            return (String) userInfoMap.get(OshUserConstants.USERNAME);
        }
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
