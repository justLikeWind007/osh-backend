package com.backstage.system.controller.user;

import com.backstage.common.annotation.OshUserLevel;
import com.backstage.common.core.domain.R;
import com.backstage.common.utils.SecurityUtils;
import com.backstage.common.utils.StringUtils;
import com.backstage.system.domain.user.OshRole;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.domain.user.OshUserAsset;
import com.backstage.system.domain.vo.user.UserManageItemVO;
import com.backstage.system.domain.vo.user.UserManageQueryVO;
import com.backstage.system.mapper.user.OshRoleMapper;
import com.backstage.system.mapper.user.OshUserAssetMapper;
import com.backstage.system.mapper.user.OshUserMapper;
import com.backstage.system.mapper.user.UserManageMapper;
import com.backstage.system.service.common.OssService;
import com.backstage.system.utils.OssUtil;
import com.backstage.system.utils.UserContextUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户管理（仅创始人 level=6 可访问）
 */
@RestController
@RequestMapping("/pc/admin/user")
public class UserManageController {

    @Resource
    private UserManageMapper userManageMapper;

    @Resource
    private OshRoleMapper oshRoleMapper;

    @Resource
    private OshUserMapper oshUserMapper;

    @Resource
    private OshUserAssetMapper oshUserAssetMapper;

    @Resource
    private OssService ossService;

    @Resource
    private OssUtil ossUtil;

    /**
     * 查询用户列表
     */
    @PostMapping("/list")
    @OshUserLevel(value = 6)
    public R list(@RequestBody UserManageQueryVO query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        // 创始人可查看所有用户，不做等级过滤
        List<UserManageItemVO> list = userManageMapper.selectUserManageList(query, null);
        PageInfo<UserManageItemVO> pageInfo = new PageInfo<>(list);

        // 批量查询所有用户的角色列表
        List<UserManageItemVO> rows = pageInfo.getList();
        if (!rows.isEmpty()) {
            List<Long> userIds = rows.stream().map(UserManageItemVO::getId).collect(Collectors.toList());
            List<Map<String, Object>> allRoles = userManageMapper.selectUserRolesByUserIds(userIds);

            // 按 userId 分组
            Map<Long, List<Map<String, Object>>> roleMap = new HashMap<>();
            for (Map<String, Object> role : allRoles) {
                Long uid = Long.valueOf(role.get("userId").toString());
                roleMap.computeIfAbsent(uid, k -> new ArrayList<>()).add(role);
            }

            // 填充到每个用户
            for (UserManageItemVO item : rows) {
                List<Map<String, Object>> userRoles = roleMap.get(item.getId());
                item.setRoles(userRoles != null ? userRoles : Collections.emptyList());
            }
        }

        // 转换头像为临时签名URL
        for (UserManageItemVO item : rows) {
            convertAvatarUrl(item);
        }

        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("total", pageInfo.getTotal());
        data.put("rows", rows);
        return R.ok(data);
    }

    /**
     * 查询用户详情
     */
    @GetMapping("/detail")
    @OshUserLevel(value = 6)
    public R detail(@RequestParam Long userId) {
        UserManageItemVO user = userManageMapper.selectUserDetail(userId);
        if (user == null) {
            return R.fail("用户不存在");
        }

        // 填充所有角色
        List<Long> userIds = Collections.singletonList(userId);
        List<Map<String, Object>> allRoles = userManageMapper.selectUserRolesByUserIds(userIds);
        user.setRoles(allRoles != null ? allRoles : Collections.emptyList());

        // 转换头像为临时签名URL
        convertAvatarUrl(user);

        // 查询违规记录
        List<Map<String, Object>> violations = userManageMapper.selectViolationListAll(userId);

        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("user", user);
        data.put("violations", violations);
        return R.ok(data);
    }

    /** 创始人可分配 level 2~5 的角色（普通开发者到核心开发者，不含普通用户和创始人） */
    private static final int MIN_ASSIGNABLE_LEVEL = 2;
    private static final int MAX_ASSIGNABLE_LEVEL = 5;

    /**
     * 查询可分配的角色列表（level 2~5）
     */
    @GetMapping("/roles/assignable")
    @OshUserLevel(value = 6)
    public R getRoleOptions() {
        List<Map<String, Object>> roles = userManageMapper.selectAssignableRoles(MAX_ASSIGNABLE_LEVEL);
        // 过滤掉普通用户角色（level=1）
        roles.removeIf(r -> {
            Object level = r.get("roleLevel");
            return level != null && Integer.parseInt(level.toString()) < MIN_ASSIGNABLE_LEVEL;
        });
        return R.ok(roles);
    }

    /**
     * 给用户添加角色
     * VIP用户(level=3)和小班用户(level=4)需要指定有效期，其他角色永久有效
     */
    @PostMapping("/roles/add")
    @OshUserLevel(value = 6)
    public R addUserRole(@RequestBody Map<String, Object> params) {
        Long targetUserId = Long.valueOf(params.get("userId").toString());
        Integer roleId = Integer.valueOf(params.get("roleId").toString());

        // 检查角色等级：不能分配普通用户角色（自带）和创始人角色
        OshRole role = findRoleById(roleId);
        if (role == null) return R.fail("角色不存在");
        if (role.getLevel() < MIN_ASSIGNABLE_LEVEL) return R.fail("普通用户角色无需手动添加，每个用户自带");
        if (role.getLevel() > MAX_ASSIGNABLE_LEVEL) return R.fail("无法分配该等级的角色");

        // VIP用户(role_id=3)和小班用户(role_id=4)需要指定有效期
        String expireTime = null;
        if (roleId == 3 || roleId == 4) {
            Object expireObj = params.get("expireTime");
            Boolean permanent = params.get("permanent") != null && Boolean.parseBoolean(params.get("permanent").toString());
            if (permanent) {
                expireTime = "2099-12-31 23:59:59";
            } else if (expireObj != null && !expireObj.toString().isEmpty()) {
                expireTime = expireObj.toString();
            } else {
                return R.fail("VIP用户和小班用户角色需要指定有效期");
            }
        }

        // 检查用户角色数量上限
        List<Integer> currentRoleIds = userManageMapper.selectUserRoleIds(targetUserId);
        if (currentRoleIds.size() >= 7) return R.fail("用户角色数量已达上限（7个）");

        // 检查角色不可重复
        if (currentRoleIds.contains(roleId)) return R.fail("用户已拥有该角色");

        Long operatorId = UserContextUtil.getCurrentUserId();
        userManageMapper.insertUserRole(targetUserId, roleId, operatorId, expireTime);
        return R.ok("角色添加成功");
    }

    /**
     * 删除用户的某个角色
     */
    @PostMapping("/roles/remove")
    @OshUserLevel(value = 6)
    public R removeUserRole(@RequestBody Map<String, Object> params) {
        Long targetUserId = Long.valueOf(params.get("userId").toString());
        Integer roleId = Integer.valueOf(params.get("roleId").toString());

        // 检查角色等级
        OshRole role = findRoleById(roleId);
        if (role == null) return R.fail("角色不存在");
        // 普通用户角色和创始人角色不可删除
        if (role.getLevel() < MIN_ASSIGNABLE_LEVEL) return R.fail("普通用户角色不可删除");
        if (role.getLevel() > MAX_ASSIGNABLE_LEVEL) return R.fail("创始人角色不可删除");

        userManageMapper.deleteUserRole(targetUserId, roleId);
        return R.ok("角色已移除");
    }

    /**
     * 修改用户角色有效期（续期/减期）
     * 仅支持 VIP用户(level=3) 和 小班用户(level=4)
     */
    @PostMapping("/roles/expire")
    @OshUserLevel(value = 6)
    public R updateRoleExpire(@RequestBody Map<String, Object> params) {
        Long targetUserId = Long.valueOf(params.get("userId").toString());
        Integer roleId = Integer.valueOf(params.get("roleId").toString());

        OshRole role = findRoleById(roleId);
        if (role == null) return R.fail("角色不存在");
        if (roleId != 3 && roleId != 4) return R.fail("仅VIP用户和小班用户角色支持修改有效期");

        String expireTime;
        Boolean permanent = params.get("permanent") != null && Boolean.parseBoolean(params.get("permanent").toString());
        if (permanent) {
            expireTime = "2099-12-31 23:59:59";
        } else {
            Object expireObj = params.get("expireTime");
            if (expireObj == null || expireObj.toString().isEmpty()) {
                return R.fail("请指定新的到期时间");
            }
            expireTime = expireObj.toString();
        }

        userManageMapper.updateUserRoleExpire(targetUserId, roleId, expireTime);
        return R.ok("有效期修改成功");
    }

    private OshRole findRoleById(Integer roleId) {
        return oshRoleMapper.selectById(roleId);
    }

    /**
     * 将头像相对路径转为临时签名URL（兼容旧数据完整URL格式）
     */
    private void convertAvatarUrl(UserManageItemVO item) {
        if (item == null || StringUtils.isEmpty(item.getAvatar())) return;
        String avatar = item.getAvatar();
        // 兼容旧数据：如果存的是完整URL（http开头），提取相对路径
        if (avatar.startsWith("http")) {
            String publicDomain = ossUtil.getOssProperties().getPublicDomain();
            if (avatar.startsWith(publicDomain)) {
                avatar = avatar.substring(publicDomain.length());
                if (avatar.startsWith("/")) {
                    avatar = avatar.substring(1);
                }
            }
        }
        item.setAvatar(ossService.getLimitedUrl(avatar, 30));
    }

    /**
     * 修改用户信息（用户名、密码、积分、性别、个人简介）
     */
    @PostMapping("/update")
    @OshUserLevel(value = 6)
    public R updateUser(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());

        OshUser user = oshUserMapper.selectById(userId);
        if (user == null) return R.fail("用户不存在");

        // 更新用户名
        if (params.containsKey("username") && params.get("username") != null) {
            String username = params.get("username").toString().trim();
            if (StringUtils.isNotEmpty(username) && !username.equals(user.getUsername())) {
                // 检查用户名是否已存在
                LambdaQueryWrapper<OshUser> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(OshUser::getUsername, username).ne(OshUser::getId, userId);
                if (oshUserMapper.selectOne(wrapper) != null) {
                    return R.fail("用户名已存在");
                }
                user.setUsername(username);
            }
        }

        // 重置密码（传入明文，后端加密存储）
        if (params.containsKey("password") && params.get("password") != null) {
            String password = params.get("password").toString().trim();
            if (StringUtils.isNotEmpty(password)) {
                user.setPassword(SecurityUtils.encryptPassword(password));
            }
        }

        // 更新性别
        if (params.containsKey("sex") && params.get("sex") != null) {
            user.setSex(params.get("sex").toString());
        }

        // 更新个人简介
        if (params.containsKey("introduction")) {
            String intro = params.get("introduction") != null ? params.get("introduction").toString() : "";
            user.setIntroduction(intro);
        }

        // 更新用户表
        oshUserMapper.updateById(user);

        // 更新积分
        if (params.containsKey("points") && params.get("points") != null) {
            Long points = Long.valueOf(params.get("points").toString());
            LambdaUpdateWrapper<OshUserAsset> assetWrapper = new LambdaUpdateWrapper<>();
            assetWrapper.eq(OshUserAsset::getUserId, userId).set(OshUserAsset::getPoints, points);
            oshUserAssetMapper.update(null, assetWrapper);
        }

        return R.ok("修改成功");
    }
}
