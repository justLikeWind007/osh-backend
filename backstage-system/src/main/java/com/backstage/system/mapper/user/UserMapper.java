package com.backstage.system.mapper.user;

import com.backstage.system.domain.user.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/7
 * Time: 16:44
 */
public interface UserMapper {
    User getUserByUsername(String username);

    User getUserByUsernameOrEmail(String name);

    User getUserByEmail(String email);

    int register(@Param("username") String username,@Param("password")  String password, @Param("email")  String email);

    int addUniqueId(@Param("userId") Long userId,@Param("uniqueId") String uniqueId);

    String getUniqueIdByUserId(@Param("userId") Long userId);

    Long getUserIdByUniqueId(String uniqueId);

    int updateUniqueIdByUserId(@Param("userId") Long userId,@Param("uniqueId") String uniqueId);

    int updatePasswordByEmail(@Param("email") String email, @Param("password") String password);

    int updatePasswordById(@Param("userId") Long userId, @Param("password") String password);

    int updateUserInfoById(@Param("userId") Long userId, @Param("avatar") String avatar, @Param("nickname") String nickname, @Param("sex") String sex);

    String getPasswordById(@Param("userId") Long userId);

    User getUserInfoById(@Param("userId") Long userId);

    int updateEmailById(@Param("userId") Long userId, @Param("email") String email);

    //以下是若依代码生成

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
     * 删除用户
     *
     * @param id 用户主键
     * @return 结果
     */
    public int deleteUserById(Long id);

    /**
     * 批量删除用户
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUserByIds(Long[] ids);
}
