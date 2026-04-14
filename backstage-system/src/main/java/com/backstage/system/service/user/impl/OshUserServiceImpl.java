package com.backstage.system.service.user.impl;

import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.redis.RedisCache;
import com.backstage.common.enums.ResultCode;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.common.utils.email.EmailUtil;
import com.backstage.common.utils.jwt.JwtUtil;
import com.backstage.common.utils.StringUtils;
import com.backstage.system.domain.user.OshPermission;
import com.backstage.system.domain.user.OshRole;
import com.backstage.system.domain.user.OshUserViolationRecord;
import com.backstage.system.domain.user.User;
import com.backstage.system.domain.user.vo.OshRoleVO;
import com.backstage.system.domain.user.vo.UserLoginVo;
import com.backstage.system.mapper.user.OshPermissionMapper;
import com.backstage.system.mapper.user.OshRoleMapper;
import com.backstage.system.mapper.user.OshUserMapper;
import com.backstage.system.mapper.user.OshUserViolationRecordMapper;
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
 * User: 九转苍翎
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
    private OshUserViolationRecordMapper oshUserViolationRecordMapper;

    @Override
    public R<UserLoginVo> login(String username, String password) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return R.fail(ResultCode.FAILED_USER_NAME_OR_PASSWORD_EMPTY.getMsg());
        }
        User user = oshUserMapper.getUserByUsernameOrEmail(username);
        if (user == null) {
            return R.fail(ResultCode.FAILED_USER_NOT_EXISTS.getMsg());
        }
        if (user.getStatus() == 0) {
            return R.fail(ResultCode.FAILED_USER_BANNED.getMsg());
        }
        if (password.length() == 50) {
            String uniqueIdByUserId = oshUserMapper.getUniqueIdByUserId(user.getId());
            if (!uniqueIdByUserId.equals(password)) {
                return R.fail(ResultCode.FAILED_USER_UNIQUEID_ERROR.getMsg());
            }
        }else {
            if (!user.getPassword().equals(password)) {
                return R.fail(ResultCode.FAILED_USER_PASSWORD_ERROR.getMsg());
            }
        }
        String token = createToken(user);
        UserLoginVo userLoginVo = new UserLoginVo();
        BeanUtils.copyProperties(user, userLoginVo);
        userLoginVo.setToken(token);
        Integer roleId = oshRoleMapper.getRoleIdsByUserId(user.getId());
        List<String> role = getRole(roleId);
        List<String> permissionList = getPermission(roleId);
        userLoginVo.setRole(role);
        userLoginVo.setPermissionList(permissionList);
        Map<String, Object> map = new HashMap<>();
        map.put(OshUserConstants.ROLE, role);
        map.put(OshUserConstants.PERMISSION, permissionList);
        redisCache.setCacheObject(OshUserConstants.LOGIN_USER + user.getId(), map, 500, TimeUnit.MINUTES);
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
        User user = oshUserMapper.getUserByUsername(username);
        if (user != null && user.getUsername().equals(username)) {
            return R.fail(ResultCode.AILED_USER_EXISTS.getMsg());
        }
        user = oshUserMapper.getUserByEmail(email);
        if (user != null && user.getEmail().equals(email)) {
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
        oshUserMapper.register(userMap.get(OshUserConstants.USERNAME), userMap.get(OshUserConstants.PASSWORD), userMap.get(OshUserConstants.EMAIL));
        User user = oshUserMapper.getUserByUsername(userMap.get(OshUserConstants.USERNAME));
        oshUserMapper.addUniqueId(user.getId(), uniqueId);
        oshUserMapper.addRole(user.getId());
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
        User user = oshUserMapper.selectUserById(userId);
        if (user == null) {
            return R.fail(ResultCode.FAILED_USER_NOT_EXISTS.getMsg());
        }
        String uniqueIdByUserId = oshUserMapper.getUniqueIdByUserId(userId);
        if (!uniqueIdByUserId.equals(uniqueId)) {
            return R.fail(ResultCode.FAILED_USER_UNIQUEID_ERROR.getMsg());
        }
        User emailUser = oshUserMapper.getUserByEmail(newEmail);
        if (emailUser != null && user.getEmail().equals(newEmail)) {
            return R.fail(ResultCode.FAILED_USER_EMAIL_BOUND.getMsg());
        }
        String newUniqueId = emailUtil.sendEmailGetUniqueId(user.getUsername(), newEmail);
        Map<String,String> userMap = new HashMap<>();
        userMap.put(OshUserConstants.USER_ID, userId.toString());
        userMap.put(OshUserConstants.EMAIL, newEmail);
        redisCache.setCacheObject(OshUserConstants.RE_UNIQUE_ID + newUniqueId, userMap, 500, TimeUnit.MINUTES);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> changeEmailVerity(String uniqueId) {
        Long userId = ThreadLocalUtil.get(OshUserConstants.USER_ID,Long.class);
        User user = oshUserMapper.selectUserById(userId);
        if (user == null) {
            return R.fail(ResultCode.FAILED_USER_NOT_EXISTS.getMsg());
        }
        Map<String,String> userMap = redisCache.getCacheObject(OshUserConstants.RE_UNIQUE_ID + uniqueId);
        if (userMap == null) return R.fail("新的唯一标识错误或已过期");
        if (!userId.equals(Long.parseLong(userMap.get(OshUserConstants.USER_ID)))) return R.fail(ResultCode.FAILED.getMsg());
        oshUserMapper.updateEmailById(userId, userMap.get(OshUserConstants.EMAIL));
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
        User user = oshUserMapper.selectUserById(userId);
        if (user == null) {
            return R.fail(ResultCode.FAILED_USER_NOT_EXISTS.getMsg());
        }
        if(!password.equals(repassword)){
            return R.fail(ResultCode.FAILED_USER_PASSWORD_NOT_MATCH.getMsg());
        }
        String uniqueIdByUserId = oshUserMapper.getUniqueIdByUserId(userId);
        if (!uniqueIdByUserId.equals(uniqueId)) {
            return R.fail(ResultCode.FAILED_USER_UNIQUEID_ERROR.getMsg());
        }
        oshUserMapper.updatePasswordById(userId, password);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> updateInfo(String avatar, String nickname, String sex) {
        Long userId = ThreadLocalUtil.get(OshUserConstants.USER_ID,Long.class);
        oshUserMapper.updateUserInfoById(userId, avatar, nickname, sex);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> updatePassword(String opassword, String password, String repassword) {
        Long userId = ThreadLocalUtil.get(OshUserConstants.USER_ID,Long.class);
        String passwordById = oshUserMapper.getPasswordById(userId);
        if (passwordById != null && !passwordById.equals(opassword)) {
            return R.fail(ResultCode.FAILED_USER_PASSWORD_ERROR.getMsg());
        }
        if(!password.equals(repassword)){
            return R.fail(ResultCode.FAILED_USER_PASSWORD_NOT_MATCH.getMsg());
        }
        oshUserMapper.updatePasswordById(userId, password);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<User> getUserInfo() {
        User user = UserContextUtil.getCurrentUser();
        return R.ok(user);
    }

    @Override
    public R<String> record(Long userId, Integer violationType, String reason, Long operatorId) {
        OshUserViolationRecord record = new OshUserViolationRecord();
        record.setUserId(userId);
        record.setViolationType(violationType);
        record.setReason(reason);
        if (operatorId != null) record.setOperatorId(operatorId);
        oshUserViolationRecordMapper.insert(record);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> cancelRecord(Long userId, User currentUser) {
        LambdaQueryWrapper<OshUserViolationRecord> wrapper = new LambdaQueryWrapper<OshUserViolationRecord>()
                .eq(OshUserViolationRecord::getUserId, userId);
        OshUserViolationRecord record = oshUserViolationRecordMapper.selectOne(wrapper);
        if (record == null) {
            return R.fail(ResultCode.FAILED_NOT_EXISTS.getMsg());
        }
        record.setDelete_flag((byte) 1);
        oshUserViolationRecordMapper.update(record, wrapper);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    public String createToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(OshUserConstants.USER_ID, user.getId());
        claims.put(OshUserConstants.USERNAME, user.getUsername());
        return JwtUtil.createToken(claims);
    }

    public List<String> getRole(Integer roleId) {
        OshRoleVO oshRoleVO = oshRoleMapper.getRoleNameByRoleId(roleId);
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




    /**
     * 查询用户
     *
     * @param id 用户主键
     * @return 用户
     */
    @Override
    public User selectUserById(Long id)
    {
        return oshUserMapper.selectUserById(id);
    }

    /**
     * 查询用户列表
     *
     * @param user 用户
     * @return 用户
     */
    @Override
    public List<User> selectUserList(User user)
    {
        return oshUserMapper.selectUserList(user);
    }

    /**
     * 新增用户
     *
     * @param user 用户
     * @return 结果
     */
    @Override
    public int insertUser(User user)
    {
        return oshUserMapper.insertUser(user);
    }

    /**
     * 修改用户
     *
     * @param user 用户
     * @return 结果
     */
    @Override
    public int updateUser(User user)
    {
        return oshUserMapper.updateUser(user);
    }

    /**
     * 批量删除用户
     *
     * @param ids 需要删除的用户主键
     * @return 结果
     */
    @Override
    public int deleteUserByIds(Long[] ids)
    {
        return oshUserMapper.deleteUserByIds(ids);
    }

    /**
     * 删除用户信息
     *
     * @param id 用户主键
     * @return 结果
     */
    @Override
    public int deleteUserById(Long id)
    {
        return oshUserMapper.deleteUserById(id);
    }
}