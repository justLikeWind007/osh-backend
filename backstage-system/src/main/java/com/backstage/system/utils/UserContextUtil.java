package com.backstage.system.utils;

import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.system.domain.user.OshUser;
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

    public static Long getCurrentUserId() {
        return ThreadLocalUtil.get(OshUserConstants.USER_ID,Long.class);
    }

    public static Integer getCurrentLevel() {
        return Integer.parseInt(ThreadLocalUtil.get(OshUserConstants.LEVEL,String.class));
    }
}
