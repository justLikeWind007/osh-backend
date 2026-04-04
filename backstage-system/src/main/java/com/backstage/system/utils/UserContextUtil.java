package com.backstage.system.utils;

import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.system.domain.user.User;
import com.backstage.system.mapper.user.OshUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/30
 * Time: 15:17
 */
@Component
public class UserContextUtil {
    @Autowired
    private OshUserMapper oshUserMapper;

    public User getCurrentUser() {
        return oshUserMapper.getUserInfoById(ThreadLocalUtil.get(OshUserConstants.USER_ID,Long.class));
    }
}
