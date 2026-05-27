package com.backstage.system.controller.user;

import com.backstage.common.annotation.OshUserLevel;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.user.OshRole;
import com.backstage.system.domain.vo.user.UserManageItemVO;
import com.backstage.system.domain.vo.user.UserManageQueryVO;
import com.backstage.system.mapper.user.OshRoleMapper;
import com.backstage.system.mapper.user.UserManageMapper;
import com.backstage.system.utils.UserContextUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户管理（管理员 level >= 4）
 */
@RestController
@RequestMapping("/pc/admin/user")
public class UserManageController {

    @Resource
    private UserManageMapper userManageMapper;

    @Resource
    private OshRoleMapper oshRoleMapper;

    /**
     * 查询用户列表
     */
    @PostMapping("/list")
    @OshUserLevel(value = 4)
    public R list(@RequestBody UserManageQueryVO query) {
        Integer currentLevel = UserContextUtil.getCurrentLevel();

        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<UserManageItemVO> list = userManageMapper.selectUserManageList(query, currentLevel);
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

        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("total", pageInfo.getTotal());
        data.put("rows", rows);
        return R.ok(data);
    }

    /**
     * 查询用户详情
     */
    @GetMapping("/detail")
    @OshUserLevel(value = 4)
    public R detail(@RequestParam Long userId) {
        Integer currentLevel = UserContextUtil.getCurrentLevel();

        UserManageItemVO user = userManageMapper.selectUserDetail(userId);
        if (user == null) {
            return R.fail("用户不存在");
        }
        // 不能查看等级 >= 自己的用户
        if (user.getRoleLevel() != null && user.getRoleLevel() >= currentLevel) {
            return R.fail("无权查看该用户");
        }

        // 填充所有角色
        List<Long> userIds = Collections.singletonList(userId);
        List<Map<String, Object>> allRoles = userManageMapper.selectUserRolesByUserIds(userIds);
        user.setRoles(allRoles != null ? allRoles : Collections.emptyList());

        // 查询违规记录
        List<Map<String, Object>> violations = userManageMapper.selectViolationListAll(userId);

        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("user", user);
        data.put("violations", violations);
        return R.ok(data);
    }

    /** 最高可分配角色等级：小班用户 level=3 */
    private static final int MAX_ASSIGNABLE_LEVEL = 3;

    /**
     * 查询可分配的角色列表
     */
    @GetMapping("/roles/assignable")
    @OshUserLevel(value = 4)
    public R getRoleOptions() {
        List<Map<String, Object>> roles = userManageMapper.selectAssignableRoles(MAX_ASSIGNABLE_LEVEL);
        return R.ok(roles);
    }

    /**
     * 给用户添加角色
     */
    @PostMapping("/roles/add")
    @OshUserLevel(value = 4)
    public R addUserRole(@RequestBody Map<String, Object> params) {
        Long targetUserId = Long.valueOf(params.get("userId").toString());
        Integer roleId = Integer.valueOf(params.get("roleId").toString());

        // 检查角色等级不能超过上限
        OshRole role = findRoleById(roleId);
        if (role == null) return R.fail("角色不存在");
        if (role.getLevel() > MAX_ASSIGNABLE_LEVEL) return R.fail("无法分配该等级的角色");

        // 检查用户角色数量上限
        List<Integer> currentRoleIds = userManageMapper.selectUserRoleIds(targetUserId);
        if (currentRoleIds.size() >= 7) return R.fail("用户角色数量已达上限（7个）");

        // 检查角色不可重复
        if (currentRoleIds.contains(roleId)) return R.fail("用户已拥有该角色");

        Long operatorId = UserContextUtil.getCurrentUserId();
        userManageMapper.insertUserRole(targetUserId, roleId, operatorId);
        return R.ok("角色添加成功");
    }

    /**
     * 删除用户的某个角色
     */
    @PostMapping("/roles/remove")
    @OshUserLevel(value = 4)
    public R removeUserRole(@RequestBody Map<String, Object> params) {
        Long targetUserId = Long.valueOf(params.get("userId").toString());
        Integer roleId = Integer.valueOf(params.get("roleId").toString());

        // 普通用户角色（role_id=1）不可删除
        if (roleId == 1) return R.fail("普通用户角色不可删除");

        // 检查角色等级
        OshRole role = findRoleById(roleId);
        if (role == null) return R.fail("角色不存在");
        if (role.getLevel() > MAX_ASSIGNABLE_LEVEL) return R.fail("无法操作该等级的角色");

        userManageMapper.deleteUserRole(targetUserId, roleId);
        return R.ok("角色已移除");
    }

    private OshRole findRoleById(Integer roleId) {
        return oshRoleMapper.selectById(roleId);
    }
}
