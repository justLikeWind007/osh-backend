package com.backstage.system.service.user;

import com.backstage.common.core.domain.R;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.domain.user.vo.OshUserLoginVo;
import com.backstage.system.request.UserListRequest;

import javax.mail.MessagingException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/3/7
 * Time: 16:42
 */
public interface IOshUserService {
    R<OshUserLoginVo> login(String username, String password);

    R<String> registerSubmit(String username, String password, String repassword, String email) throws MessagingException;

    R<String> registerVerity(String uniqueId);

    R<String> logout();

    R<String> changeEmailSubmit(String uniqueId, String newEmail) throws MessagingException;

    R<String> changeEmailVerity(String uniqueId);

    R<String> forget(String uniqueId, String password, String repassword);

    R<String> updateInfo(String avatar, String nickname, String sex);

    R<String> updatePassword(String opassword, String password, String repassword);

    R<OshUser> getUserInfo();

    R<String> deleteUser();

    R<String> record(Long userId, Integer violationType, String reason);

    R<String> cancelRecord(Long userId, OshUser currentOshUser);


//    /**
//     * 查询用户
//     *
//     * @param id 用户主键
//     * @return 用户
//     */
//    public OshUser selectUserById(Long id);
//
    /**
     * 查询用户列表
     *
     * @param req 参数
     * @return 用户集合
     */
    List<OshUser> selectUserList(UserListRequest req);
//
//    /**
//     * 新增用户
//     *
//     * @param oshUser 用户
//     * @return 结果
//     */
//    public int insertUser(OshUser oshUser);
//
//    /**
//     * 修改用户
//     *
//     * @param oshUser 用户
//     * @return 结果
//     */
//    public int updateUser(OshUser oshUser);
//
//    /**
//     * 批量删除用户
//     *
//     * @param ids 需要删除的用户主键集合
//     * @return 结果
//     */
//    public int deleteUserByIds(Long[] ids);
//
//    /**
//     * 删除用户信息
//     *
//     * @param id 用户主键
//     * @return 结果
//     */
//    public int deleteUserById(Long id);
}
