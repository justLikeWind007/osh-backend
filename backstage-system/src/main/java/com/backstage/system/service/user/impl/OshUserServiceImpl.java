package com.backstage.system.service.user.impl;

import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.redis.RedisCache;
import com.backstage.common.enums.ResultCode;
import com.backstage.common.enums.UploadPathEnum;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.common.utils.email.EmailUtil;
import com.backstage.common.utils.generate.GenerateUtil;
import com.backstage.common.utils.jwt.JwtUtil;
import com.backstage.common.utils.StringUtils;
import com.backstage.system.domain.user.*;
import com.backstage.system.domain.user.vo.OshUserLoginVO;
import com.backstage.system.mapper.user.*;
import com.backstage.system.request.UserListRequest;
import com.backstage.system.service.common.OssService;
import com.backstage.system.service.user.IOshUserService;
import com.backstage.system.utils.OssUtil;
import com.backstage.system.utils.UserContextUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    @Autowired
    private OshUserAssetRecordMapper oshUserAssetRecordMapper;

    @Override
    public R<OshUserLoginVO> login(String username, String password) {
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
        OshUserLoginVO userLoginVo = new OshUserLoginVO();
        userLoginVo.setToken(token);
        List<Integer> roleIds = oshRoleMapper.getRoleIdsByUserId(oshUser.getId());
        Map<String, String> asset = getAsset(oshUser.getId());
        Map<String, String> role = getRole(roleIds);
        Map<String, List<String>> permissionList = getPermission(roleIds);
        userLoginVo.setAsset(asset);
        userLoginVo.setRole(role);
        userLoginVo.setPermissionList(permissionList);
        Map<String, Object> map = new HashMap<>();
        map.put(OshUserConstants.ASSET, asset);
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
        oshUser.setDeleteFlag((byte) 0);  // 明确设置，避免拦截器过滤
        ThreadLocalUtil.set(OshUserConstants.USER_ID, userId);
        oshUserMapper.insert(oshUser);
        oshUserMapper.addUniqueId(oshUser.getId(), uniqueId);
        oshUserMapper.addRole(oshUser.getId());
        OshUserAsset oshUserAsset = new OshUserAsset();
        oshUserAsset.setPoints(188L);
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
    public R<String> updateInfo(String username, String sex, String introduction) {
        Long userId = ThreadLocalUtil.get(OshUserConstants.USER_ID,Long.class);
        LambdaQueryWrapper<OshUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OshUser::getId, userId);
        OshUser oshUser = oshUserMapper.selectOne(wrapper);
        oshUser.setUsername(username);
        oshUser.setSex(sex);
        oshUser.setIntroduction(introduction);
        oshUserMapper.update(oshUser, wrapper);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Autowired
    private OssService ossService;

    @Autowired
    private OssUtil ossUtil;

    @Override
    public R<String> uploadAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return R.fail("上传文件不能为空");
        }
        // 校验文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return R.fail("文件名不能为空");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!extension.matches("jpg|jpeg|png|gif|webp")) {
            return R.fail("仅支持 jpg/png/gif/webp 格式的图片");
        }
        // 校验文件大小（最大 3MB）
        if (file.getSize() > 3 * 1024 * 1024) {
            return R.fail("头像图片大小不能超过 3MB");
        }
        try {
            Long userId = ThreadLocalUtil.get(OshUserConstants.USER_ID, Long.class);
            // 上传到 OSS，路径：common/image/avatar/{userId}/
            String filePath = ossService.upload(file, UploadPathEnum.AVATAR, String.valueOf(userId));
            if (filePath == null || filePath.contains("不能超过")) {
                return R.fail(filePath);
            }
            // 获取完整的公开访问 URL
            String avatarUrl = ossUtil.getFullFilePath(filePath);
            // 更新用户头像字段
            LambdaQueryWrapper<OshUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OshUser::getId, userId);
            OshUser oshUser = oshUserMapper.selectOne(wrapper);
            oshUser.setAvatar(avatarUrl);
            oshUserMapper.update(oshUser, wrapper);
            return R.ok(avatarUrl);
        } catch (Exception e) {
            return R.fail("头像上传失败：" + e.getMessage());
        }
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
        if (oshUserAsset != null) {
            oshUserAsset.setDeleteFlag((byte) 1);
            oshUserAssetMapper.update(oshUserAsset, new LambdaQueryWrapper<OshUserAsset>().eq(OshUserAsset::getUserId, userId));
        }
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
        LambdaQueryWrapper<OshUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OshUser::getId, userId);
        OshUser oshUser = oshUserMapper.selectOne(wrapper);
        oshUser.setViolationCount(oshUser.getViolationCount() + 1);
        oshUserMapper.update(oshUser, wrapper);
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
        LambdaQueryWrapper<OshUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(OshUser::getId, userId);
        OshUser oshUser = oshUserMapper.selectOne(userWrapper);
        oshUser.setViolationCount(oshUser.getViolationCount() - 1);
        oshUserMapper.update(oshUser, userWrapper);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }

    @Override
    public R<String> updateAsset(Integer changeType, Integer changeSource, Integer assetType, Long changeAmount, String remark) {
        LambdaQueryWrapper<OshUserAsset> wrapper = new LambdaQueryWrapper<>();
        Long userId = UserContextUtil.getCurrentUserId();
        wrapper.eq(OshUserAsset::getUserId, userId).select(OshUserAsset::getGoldCoin, OshUserAsset::getPoints);
        OshUserAssetRecord oshUserAssetRecord = new OshUserAssetRecord();
        oshUserAssetRecord.setUserId(userId);
        oshUserAssetRecord.setChangeType(changeType);
        oshUserAssetRecord.setChangeSource(changeSource);
        oshUserAssetRecord.setAssetType(assetType);
        oshUserAssetRecord.setChangeAmount(changeAmount);
        oshUserAssetRecord.setRemark(remark);
        Map<String,Object> userMap = redisCache.getCacheObject(OshUserConstants.LOGIN_USER + userId);
        if (userMap == null) {
            return R.fail(ResultCode.FAILED_NOT_LOGIN.getMsg());
        }
        Map<String,String> asset = (Map<String,String>)userMap.get(OshUserConstants.ASSET);
        Long goldCoin = Long.valueOf(asset.get(OshUserConstants.GOLD_COIN));
        Long points = Long.valueOf(asset.get(OshUserConstants.POINTS));
        if (assetType == 0) {
            oshUserAssetRecord.setBeforeBalance(goldCoin);
            if (changeType == 0) {
                goldCoin += changeAmount;
            } else {
                goldCoin -= changeAmount;
            }
            oshUserAssetRecord.setAfterBalance(goldCoin);
        } else {
            oshUserAssetRecord.setBeforeBalance(points);
            if (changeType == 0) {
                points += changeAmount;
            } else {
                points -= changeAmount;
            }
            oshUserAssetRecord.setAfterBalance(points);
        }
        OshUserAsset oshUserAsset = new OshUserAsset();
        oshUserAsset.setGoldCoin(goldCoin);
        oshUserAsset.setPoints(points);
        oshUserAssetMapper.update(oshUserAsset, wrapper);
        oshUserAssetRecordMapper.insert(oshUserAssetRecord);
        // 更新redis
        asset.put(OshUserConstants.GOLD_COIN, String.valueOf(goldCoin));
        asset.put(OshUserConstants.POINTS, String.valueOf(points));
        userMap.put(OshUserConstants.ASSET, asset);
        redisCache.setCacheObject(OshUserConstants.LOGIN_USER + userId, userMap);
        return R.ok(ResultCode.SUCCESS.getMsg());
    }


    public String createToken(OshUser oshUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(OshUserConstants.USER_ID, oshUser.getId());
        claims.put(OshUserConstants.USERNAME, oshUser.getUsername());
        return JwtUtil.createToken(claims);
    }

    private Map<String, String> getAsset(Long id) {
        OshUserAsset asset = ensureUserAsset(id);
        HashMap<String, String> result = new HashMap<>();
        result.put(OshUserConstants.GOLD_COIN, String.valueOf(Optional.ofNullable(asset.getGoldCoin()).orElse(0L)));
        result.put(OshUserConstants.POINTS, String.valueOf(Optional.ofNullable(asset.getPoints()).orElse(0L)));
        return result;
    }

    private OshUserAsset ensureUserAsset(Long userId) {
        OshUserAsset asset = oshUserAssetMapper.selectOne(new LambdaQueryWrapper<OshUserAsset>()
                .eq(OshUserAsset::getUserId, userId));
        if (asset != null) {
            if (asset.getGoldCoin() == null) {
                asset.setGoldCoin(0L);
            }
            if (asset.getPoints() == null) {
                asset.setPoints(0L);
            }
            return asset;
        }

        OshUserAsset created = new OshUserAsset();
        LocalDateTime now = LocalDateTime.now();
        created.setUserId(userId);
        created.setGoldCoin(0L);
        created.setPoints(0L);
        created.setCreateBy(userId);
        created.setUpdateBy(userId);
        created.setCreateTime(now);
        created.setUpdateTime(now);
        created.setDeleteFlag((byte) 0);
        oshUserAssetMapper.insert(created);
        return created;
    }

    public Map<String,String> getRole(List<Integer> roleId) {
        LambdaQueryWrapper<OshRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.in(OshRole::getId, roleId).eq(OshRole::getDeleteFlag, 0)
                .select(OshRole::getRoleName, OshRole::getRoleCode, OshRole::getLevel);
        List<OshRole> oshRoleList = oshRoleMapper.selectList(roleWrapper);
        OshRole oshRole = oshRoleList.get(0);
        for (OshRole curRole : oshRoleList) {
            if (curRole.getLevel() > oshRole.getLevel()) {
                oshRole = curRole;
            }
        }
        Map<String, String> roleMap = new HashMap<>();
        roleMap.put("roleName", oshRole.getRoleName());
        roleMap.put("roleCode", oshRole.getRoleCode());
        roleMap.put("level", oshRole.getLevel().toString());
        return roleMap;
    }

    public Map<String,List<String>> getPermission(List<Integer> roleIds) {
        List<Integer> ids = oshPermissionMapper.selectPermissionIdsByRoleIds(roleIds);
        LambdaQueryWrapper<OshPermission> permissionWrapper = new LambdaQueryWrapper<>();
        permissionWrapper.in(OshPermission::getId, ids).eq(OshPermission::getDeleteFlag, 0);
        List<OshPermission> oshPermissions = oshPermissionMapper.selectList(permissionWrapper);
        Map<Integer, OshPermission> permissionMap = oshPermissions.stream()
                .collect(Collectors.toMap(OshPermission::getId, p -> p));
        Map<String, List<String>> result = new HashMap<>();
        for (OshPermission permission : oshPermissions) {
            Integer parentId = permission.getParentId();
            String currentCode = permission.getPermissionCode();
            if (currentCode == null || currentCode.isEmpty()) {
                continue;
            }
            OshPermission parent = permissionMap.get(parentId);
            if (parent != null) {
                String parentCode = parent.getPermissionCode();
                if (parentCode != null && !parentCode.isEmpty()) {
                    result.computeIfAbsent(parentCode, k -> new ArrayList<>()).add(currentCode);
                }
            } else if (parentId == null || parentId == 0) {
                result.computeIfAbsent(currentCode, k -> new ArrayList<>());
            }
        }
        result.values().forEach(Collections::sort);

        return result;
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
    /**
     * 查询用户列表
     *
     * @param req 用户
     * @return 用户
     */
    @Override
    public List<OshUser> selectUserList(UserListRequest req) {
        return oshUserMapper.selectList(Wrappers.lambdaQuery());
    }
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
