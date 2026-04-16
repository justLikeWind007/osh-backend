package com.backstage.system.service.user.impl;

import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.redis.RedisCache;
import com.backstage.common.enums.ResultCode;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.common.utils.email.EmailUtil;
import com.backstage.common.utils.generate.GenerateUtil;
import com.backstage.common.utils.jwt.JwtUtil;
import com.backstage.common.utils.StringUtils;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.domain.user.OshUserAsset;
import com.backstage.system.domain.user.OshUserViolation;
import com.backstage.system.domain.user.vo.OshRoleVO;
import com.backstage.system.domain.user.vo.OshUserLoginVo;
import com.backstage.system.mapper.user.*;
import com.backstage.system.service.user.IOshUserService;
import com.backstage.system.utils.UserContextUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/3/7
 * Time: 16:42
 */
@Service
public class OshUserServiceImpl implements IOshUserService {

    @Autowired
    private OshUserMapper oshUserMapper;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private OshRoleMapper oshRoleMapper;
    @Autowired
    private OshPermissionMapper oshPermissionMapper;
    @Autowired
    private OshUserViolationMapper oshUserViolationMapper;
    @Autowired
    private OshUserAssetMapper oshUserAssetMapper;

    @Override
    public R<OshUserLoginVo> login(String username, String password) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return R.fail(ResultCode.FAILED_USER_NAME_OR_PASSWORD_EMPTY.getMsg());
        }
        LambdaQueryWrapper<OshUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OshUser::getUsername, username)
                .or()
                .eq(OshUser::getEmail, username);
        OshUser oshUser = oshUserMapper.selectOne(wrapper);
        if (oshUser == null) {
            return R.fail(ResultCode.FAILED_USER_NOT_EXISTS.getMsg());
        }
        if (oshUser.getStatus() == 1) {
            return R.fail(ResultCode.FAILED_USER_BANNED.getMsg());
        }
        if (password.length() == 50) {
            String uniqueIdByUserId = oshUserMapper.getUniqueIdByUserId(oshUser.getId());
            if (!uniqueIdByUserId.equals(password)) {
                return R.fail(ResultCode.FAILED_USER_UNIQUEID_ERROR.getMsg());
            }
        }else {
            if (!oshUser.getPassword().equals(password)) {
                return R.fail(ResultCode.FAILED_USER_PASSWORD_ERROR.getMsg());
            }
        }
        String token = createToken(oshUser);
        OshUserLoginVo userLoginVo = new OshUserLoginVo();
        BeanUtils.copyProperties(oshUser, userLoginVo);
        userLoginVo.setToken(token);
        Integer roleId = oshRoleMapper.getRoleIdByUserId(oshUser.getId());
        List<String> role = getRole(roleId);
        List<String> permissionList = getPermission(roleId);
        userLoginVo.setRole(role);
        userLoginVo.setPermissionList(permissionList);
        Map<String, Object> map = new HashMap<>();
        map.put(OshUserConstants.ROLE, role);
        map.put(OshUserConstants.PERMISSION, permissionList);
        redisCache.setCacheObject(OshUserConstants.LOGIN_USER + oshUser.getId(), map, 500, TimeUnit.MINUTES);
        return R.ok(userLoginVo);
    }

    @Override
    public R<String> registerSubmit(String username, String password, String repassword, String email) throws MessagingException {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return R.fail(ResultCode.FAILED_USER_NAME_OR_PASSWORD_EMPTY.getMsg());
        }
        if (username.length() < OshUserConstants.USERNAME_MIN_LENGTH || username.length() > OshUserConstants.USERNAME_MAX_LENGTH || !username.matches(OshUserConstants.USERNAME_PATTERN)) {
            return R.fail(ResultCode.FAILED_USER_USERNAME_NOT_IN_RANGE.getMsg());
        }
        if (password.length() < OshUserConstants.PASSWORD_MIN_LENGTH || password.length() > OshUserConstants.PASSWORD_MAX_LENGTH || !password.matches(OshUserConstants.PASSWORD_PATTERN)) {
            return R.fail(ResultCode.FAILED_USER_PASSWORD_NOT_MATCHES.getMsg());
        }
        if(!password.equals(repassword)){
            return R.fail(ResultCode.FAILED_USER_PASSWORD_NOT_MATCH.getMsg());
        }
        LambdaQueryWrapper<OshUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OshUser::getUsername, username);
        OshUser oshUser = oshUserMapper.selectOne(wrapper);
        if (oshUser != null && oshUser.getUsername().equals(username)) {
            return R.fail(ResultCode.AILED_USER_EXISTS.getMsg());
        }
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OshUser::getEmail, email);
        oshUser = oshUserMapper.selectOne(wrapper);
        if (oshUser != null && oshUser.getEmail().equals(email)) {
            return R.fail(ResultCode.FAILED_USER_EMAIL_BOUND.getMsg());
        }
        String uniqueId = emailUtil.sendEmailGetUniqueId(username, email);
        Map<String,String> userMap = new HashMap<>();
        userMap.put(OshUserConstants.USERNAME, username);
        userMap.put(OshUserConstants.PASSWORD, password);
        userMap.put(OshUserConstants.EMAIL, email);
        redisCache.setCacheObject(OshUserConstants.UNIQUE_ID + uniqueId, userMap, 500, TimeUnit.MINUTES);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> registerVerity(String uniqueId) {
        Map<String,String> userMap = redisCache.getCacheObject(OshUserConstants.UNIQUE_ID + uniqueId);
        if(userMap == null) return R.fail("唯一标识错误或已过期");
        Long userId = GenerateUtil.generateSnowflakeId();
        OshUser oshUser = new OshUser();
        oshUser.setId(userId);
        oshUser.setUsername(userMap.get(OshUserConstants.USERNAME));
        oshUser.setPassword(userMap.get(OshUserConstants.PASSWORD));
        oshUser.setEmail(userMap.get(OshUserConstants.EMAIL));
        ThreadLocalUtil.set(OshUserConstants.USER_ID, userId);
        oshUserMapper.insert(oshUser);
        oshUserMapper.addUniqueId(oshUser.getId(), uniqueId);
        oshUserMapper.addRole(oshUser.getId());
        OshUserAsset oshUserAsset = new OshUserAsset();
        oshUserAsset.setUserId(oshUser.getId());
        oshUserAssetMapper.insert(oshUserAsset);
        redisCache.deleteObject(OshUserConstants.UNIQUE_ID + uniqueId);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> logout() {
        Long userId = ThreadLocalUtil.get(OshUserConstants.USER_ID,Long.class);
        String key = OshUserConstants.LOGIN_USER + userId;
        if (redisCache.hasKey(key)) {
            redisCache.deleteObject(key);
            return R.ok(ResultCode.SUCCESS.getMsg());
        }
        return R.fail(ResultCode.FAILED_TOKEN_EXPIRED.getMsg());
    }

    @Override
    public R<String> changeEmailSubmit(String uniqueId, String newEmail) throws MessagingException {
        Long userId = ThreadLocalUtil.get(OshUserConstants.USER_ID,Long.class);
        LambdaQueryWrapper<OshUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OshUser::getId, userId);
        OshUser oshUser = oshUserMapper.selectOne(wrapper);
        if (oshUser == null) {
            return R.fail(ResultCode.FAILED_USER_NOT_EXISTS.getMsg());
        }
        String uniqueIdByUserId = oshUserMapper.getUniqueIdByUserId(userId);
        if (!uniqueIdByUserId.equals(uniqueId)) {
            return R.fail(ResultCode.FAILED_USER_UNIQUEID_ERROR.getMsg());
        }
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OshUser::getEmail, newEmail);
        OshUser emailOshUser = oshUserMapper.selectOne(wrapper);
        if (emailOshUser != null) {
            return R.fail(ResultCode.FAILED_USER_EMAIL_BOUND.getMsg());
        }
        String newUniqueId = emailUtil.sendEmailGetUniqueId(oshUser.getUsername(), newEmail);
        Map<String,String> userMap = new HashMap<>();
        userMap.put(OshUserConstants.USER_ID, userId.toString());
        userMap.put(OshUserConstants.EMAIL, newEmail);
        redisCache.setCacheObject(OshUserConstants.RE_UNIQUE_ID + newUniqueId, userMap, 500, TimeUnit.MINUTES);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> changeEmailVerity(String uniqueId) {
        Long userId = ThreadLocalUtil.get(OshUserConstants.USER_ID,Long.class);
        LambdaQueryWrapper<OshUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OshUser::getId, userId);
        OshUser oshUser = oshUserMapper.selectOne(wrapper);
        if (oshUser == null) {
            return R.fail(ResultCode.FAILED_USER_NOT_EXISTS.getMsg());
        }
        Map<String,String> userMap = redisCache.getCacheObject(OshUserConstants.RE_UNIQUE_ID + uniqueId);
        if (userMap == null) return R.fail("新的唯一标识错误或已过期");
        if (!userId.equals(Long.parseLong(userMap.get(OshUserConstants.USER_ID)))) return R.fail(ResultCode.FAILED.getMsg());
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OshUser::getId, userId);
        OshUser user = new OshUser();
        user.setEmail(userMap.get(OshUserConstants.EMAIL));
        oshUserMapper.update(user, wrapper);
        oshUserMapper.updateUniqueIdByUserId(userId, uniqueId);
        redisCache.deleteObject(OshUserConstants.RE_UNIQUE_ID + uniqueId);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> forget(String uniqueId, String password, String repassword) {
        if (password.length() < OshUserConstants.PASSWORD_MIN_LENGTH || password.length() > OshUserConstants.PASSWORD_MAX_LENGTH || !password.matches(OshUserConstants.PASSWORD_PATTERN)) {
            return R.fail(ResultCode.FAILED_USER_PASSWORD_NOT_MATCHES.getMsg());
        }
        Long userId = oshUserMapper.getUserIdByUniqueId(uniqueId);
        LambdaQueryWrapper<OshUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OshUser::getId, userId);
        OshUser oshUser = oshUserMapper.selectOne(wrapper);
        if (oshUser == null) {
            return R.fail(ResultCode.FAILED_USER_NOT_EXISTS.getMsg());
        }
        if(!password.equals(repassword)){
            return R.fail(ResultCode.FAILED_USER_PASSWORD_NOT_MATCH.getMsg());
        }
        String uniqueIdByUserId = oshUserMapper.getUniqueIdByUserId(userId);
        if (!uniqueIdByUserId.equals(uniqueId)) {
            return R.fail(ResultCode.FAILED_USER_UNIQUEID_ERROR.getMsg());
        }
        oshUser.setPassword(password);
        oshUserMapper.update(oshUser, wrapper);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> updateInfo(String avatar, String nickname, String sex) {
        Long userId = ThreadLocalUtil.get(OshUserConstants.USER_ID,Long.class);
        LambdaQueryWrapper<OshUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OshUser::getId, userId);
        OshUser oshUser = oshUserMapper.selectOne(wrapper);
        oshUser.setAvatar(avatar);
        oshUser.setNickname(nickname);
        oshUser.setSex(sex);
        oshUserMapper.update(oshUser, wrapper);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> updatePassword(String opassword, String password, String repassword) {
        Long userId = ThreadLocalUtil.get(OshUserConstants.USER_ID,Long.class);
        LambdaQueryWrapper<OshUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(OshUser::getPassword).eq(OshUser::getId, userId);
        OshUser user = oshUserMapper.selectOne(wrapper);
        if (user == null) {
            return R.fail(ResultCode.FAILED_USER_NOT_EXISTS.getMsg());
        }
        if (user.getPassword() != null && !user.getPassword().equals(opassword)) {
            return R.fail(ResultCode.FAILED_USER_PASSWORD_ERROR.getMsg());
        }
        if(!password.equals(repassword)){
            return R.fail(ResultCode.FAILED_USER_PASSWORD_NOT_MATCH.getMsg());
        }
        if (password.length() < OshUserConstants.PASSWORD_MIN_LENGTH || password.length() > OshUserConstants.PASSWORD_MAX_LENGTH || !password.matches(OshUserConstants.PASSWORD_PATTERN)) {
            return R.fail(ResultCode.FAILED_USER_PASSWORD_NOT_MATCHES.getMsg());
        }
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OshUser::getId, userId);
        user.setPassword(password);
        oshUserMapper.update(user, wrapper);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<OshUser> getUserInfo() {
        OshUser oshUser = UserContextUtil.getCurrentUser();
        return R.ok(oshUser);
    }

    @Override
    public R<String> deleteUser() {
        Long userId = ThreadLocalUtil.get(OshUserConstants.USER_ID, Long.class);
        OshUser user = oshUserMapper.selectOne(new LambdaQueryWrapper<OshUser>().eq(OshUser::getId, userId));
        if (user == null) {
            return R.fail(ResultCode.FAILED_USER_NOT_EXISTS.getMsg());
        }
        user.setDeleteFlag((byte) 1);
        oshUserMapper.update(user, new LambdaQueryWrapper<OshUser>().eq(OshUser::getId, userId));
        oshUserMapper.deleteUniqueId(userId);
        oshRoleMapper.deleteUserRole(userId);
        OshUserAsset oshUserAsset = oshUserAssetMapper.selectOne(new LambdaQueryWrapper<OshUserAsset>().eq(OshUserAsset::getUserId, userId));
        oshUserAsset.setDeleteFlag((byte) 1);
        oshUserAssetMapper.update(oshUserAsset, new LambdaQueryWrapper<OshUserAsset>().eq(OshUserAsset::getUserId, userId));
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> record(Long userId, Integer violationType, String reason) {
        Long operatorId = ThreadLocalUtil.get(OshUserConstants.USER_ID, Long.class);
        OshUserViolation record = new OshUserViolation();
        record.setUserId(userId);
        record.setViolationType(violationType);
        record.setReason(reason);
        if (operatorId != null) record.setOperatorId(operatorId);
        oshUserViolationMapper.insert(record);
        LambdaQueryWrapper<OshUserAsset> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OshUserAsset::getUserId, userId);
        OshUserAsset oshUserAsset = oshUserAssetMapper.selectOne(wrapper);
        oshUserAsset.setViolationCount(oshUserAsset.getViolationCount() + 1);
        oshUserAssetMapper.update(oshUserAsset, wrapper);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> cancelRecord(Long userId, OshUser currentOshUser) {
        LambdaQueryWrapper<OshUserViolation> wrapper = new LambdaQueryWrapper<OshUserViolation>()
                .eq(OshUserViolation::getUserId, userId);
        OshUserViolation record = oshUserViolationMapper.selectOne(wrapper);
        if (record == null) {
            return R.fail(ResultCode.FAILED_NOT_EXISTS.getMsg());
        }
        record.setDeleteFlag((byte) 1);
        oshUserViolationMapper.update(record, wrapper);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }






    public String createToken(OshUser oshUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(OshUserConstants.USER_ID, oshUser.getId());
        claims.put(OshUserConstants.USERNAME, oshUser.getUsername());
        return JwtUtil.createToken(claims);
    }

    public List<String> getRole(Integer roleId) {
        OshRoleVO oshRoleVO = oshRoleMapper.getRoleInfoByRoleId(roleId);
        List<String> role = new ArrayList<>();
        role.add(oshRoleVO.getRoleName());
        role.add(oshRoleVO.getRoleCode());
        role.add(oshRoleVO.getLevel().toString());
        return role;
    }

    public List<String> getPermission(Integer roleId) {
        List<Integer> ids = oshPermissionMapper.selectPermissionIdsByRoleId(roleId);
        return oshPermissionMapper.selectPermissionCodeByIds(ids);
    }




//    /**
//     * 查询用户
//     *
//     * @param id 用户主键
//     * @return 用户
//     */
//    @Override
//    public OshUser selectUserById(Long id)
//    {
//        return oshUserMapper.selectUserById(id);
//    }
//
//    /**
//     * 查询用户列表
//     *
//     * @param oshUser 用户
//     * @return 用户
//     */
//    @Override
//    public List<OshUser> selectUserList(OshUser oshUser)
//    {
//        return oshUserMapper.selectUserList(oshUser);
//    }
//
//    /**
//     * 新增用户
//     *
//     * @param oshUser 用户
//     * @return 结果
//     */
//    @Override
//    public int insertUser(OshUser oshUser)
//    {
//        return oshUserMapper.insertUser(oshUser);
//    }
//
//    /**
//     * 修改用户
//     *
//     * @param oshUser 用户
//     * @return 结果
//     */
//    @Override
//    public int updateUser(OshUser oshUser)
//    {
//        return oshUserMapper.updateUser(oshUser);
//    }
//
//    /**
//     * 批量删除用户
//     *
//     * @param ids 需要删除的用户主键
//     * @return 结果
//     */
//    @Override
//    public int deleteUserByIds(Long[] ids)
//    {
//        return oshUserMapper.deleteUserByIds(ids);
//    }
//
//    /**
//     * 删除用户信息
//     *
//     * @param id 用户主键
//     * @return 结果
//     */
//    @Override
//    public int deleteUserById(Long id)
//    {
//        return oshUserMapper.deleteUserById(id);
//    }
}