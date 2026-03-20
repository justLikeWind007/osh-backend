package com.backstage.system.service;

import com.backstage.common.core.domain.R;
import com.backstage.system.domain.user.User;
import com.backstage.system.domain.user.vo.UserLoginVo;

import javax.mail.MessagingException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/7
 * Time: 16:42
 */
public interface IUserService {
    R<UserLoginVo> login(String name, String pid);

    R<String> registerSubmit(String username, String password, String repassword, String email) throws MessagingException;

    R<String> registerVerity(String uniqueId);

    R<String> logout(String token);

    R<String> changeEmailSubmit(String token, String uniqueId, String newEmail) throws MessagingException;

    R<String> changeEmailVerity(String token, String uniqueId);

    R<String> forget(String token, String uniqueId, String password, String repassword);

    R<String> updateInfo(String avatar, String nickname, String sex, String token);

    R<String> updatePassword(String opassword, String password, String repassword, String token);

    R<User> getUserInfo(String token);




    /**
     * 查询用户
     *
     * @param id 用户主键
     * @return 用户
     */
    public User selectUserById(Long id);

    /**
     * 查询用户列表
     *
     * @param user 用户
     * @return 用户集合
     */
    public List<User> selectUserList(User user);

    /**
     * 新增用户
     *
     * @param user 用户
     * @return 结果
     */
    public int insertUser(User user);

    /**
     * 修改用户
     *
     * @param user 用户
     * @return 结果
     */
    public int updateUser(User user);

    /**
     * 批量删除用户
     *
     * @param ids 需要删除的用户主键集合
     * @return 结果
     */
    public int deleteUserByIds(Long[] ids);

    /**
     * 删除用户信息
     *
     * @param id 用户主键
     * @return 结果
     */
    public int deleteUserById(Long id);
}
