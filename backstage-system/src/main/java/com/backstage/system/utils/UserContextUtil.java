package com.backstage.system.utils;

import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.system.domain.user.CurrentUser;
import com.backstage.system.domain.user.OshUser;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/3/30
 * Time: 15:17
 */
@Component
public class UserContextUtil {

    public static OshUser getCurrentUser() {
        return ThreadLocalUtil.get(OshUserConstants.USER_INFO, OshUser.class);
    }

    public static CurrentUser getCurrentUserInfo() {
        OshUser oshUser = getCurrentUser();
        CurrentUser currentUser = new CurrentUser();
        BeanUtils.copyProperties(oshUser,currentUser);
        return currentUser;
    }

    public static Long getCurrentUserId() {
        return ThreadLocalUtil.get(OshUserConstants.USER_ID,Long.class);
    }

    public static Integer getCurrentLevel() {
        return Integer.parseInt(ThreadLocalUtil.get(OshUserConstants.LEVEL,String.class));
    }

    public static Boolean hasPermission(Integer level) {
        return getCurrentLevel() >= level;
    }
}
