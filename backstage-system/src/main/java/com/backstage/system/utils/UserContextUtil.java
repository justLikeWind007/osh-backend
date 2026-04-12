package com.backstage.system.utils;

import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.system.domain.user.CurrentUser;
import com.backstage.system.domain.user.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/30
 * Time: 15:17
 */
@Component
public class UserContextUtil {

    public static User getCurrentUser() {
        return ThreadLocalUtil.get(OshUserConstants.USER_INFO,User.class);
    }

    public static CurrentUser getCurrentUserInfo() {
        User user = getCurrentUser();
        CurrentUser currentUser = new CurrentUser();
        BeanUtils.copyProperties(user,currentUser);
        currentUser.setRole(ThreadLocalUtil.get(OshUserConstants.ROLE,List.class));
        currentUser.setPermissionList(ThreadLocalUtil.get(OshUserConstants.PERMISSION,List.class));
        return currentUser;
    }

    public static Long getCurrentUserId() {
        return ThreadLocalUtil.get(OshUserConstants.USER_ID,Long.class);
    }
}
