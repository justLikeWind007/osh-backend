package com.backstage.system.mapper.user;

import com.backstage.system.domain.user.User;
import org.apache.ibatis.annotations.Param;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/7
 * Time: 16:44
 */
public interface UserMapper {
    User getUserByUsername(String username);
    int register(@Param("username") String username,@Param("password")  String password);

    int updatePasswordById(@Param("userId") Long userId, @Param("password") String password);

    int updateUserInfoById(@Param("userId") Long userId, @Param("avatar") String avatar, @Param("nickname") String nickname, @Param("sex") String sex);

    String getPasswordById(@Param("userId") Long userId);

    User getUserInfoById(@Param("userId") Long userId);
}
