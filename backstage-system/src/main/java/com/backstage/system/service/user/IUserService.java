package com.backstage.system.service.user;

import com.backstage.common.core.domain.R;
import com.backstage.system.domain.user.User;
import com.backstage.system.domain.user.vo.UserRegisterVO;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/7
 * Time: 16:42
 */
public interface IUserService {
    R<String> login(String username, String password);

    R<UserRegisterVO> register(String username, String password, String repassword);

    R<String> updateInfo(String avatar, String nickname, String sex, String token);

    R<String> forget(String phone, String code, String password, String repassword);

    R<String> updatePassword(String opassword, String password, String repassword, String token);

    R<User> getUserInfo(String token);
}
