package com.backstage.system.mapper.user;

import com.backstage.system.domain.vo.user.UserManageItemVO;
import com.backstage.system.domain.vo.user.UserManageQueryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户管理 Mapper
 */
@Mapper
public interface UserManageMapper {

    /**
     * 分页查询用户列表（关联角色、积分、违规次数）
     * @param query 查询条件
     * @param maxLevel 当前登录用户的等级（只能查询比自己等级低的用户）
     */
    List<UserManageItemVO> selectUserManageList(@Param("query") UserManageQueryVO query, @Param("maxLevel") Integer maxLevel);

    /**
     * 查询单个用户详情（关联最高角色、积分）
     */
    UserManageItemVO selectUserDetail(@Param("userId") Long userId);

    /**
     * 批量查询用户的所有角色（按 level 降序）
     * @param userIds 用户ID列表
     */
    List<Map<String, Object>> selectUserRolesByUserIds(@Param("userIds") List<Long> userIds);

    /**
     * 查询可分配的角色列表（level <= maxLevel）
     */
    List<Map<String, Object>> selectAssignableRoles(@Param("maxLevel") Integer maxLevel);

    /**
     * 给用户添加角色（支持有效期）
     */
    void insertUserRole(@Param("userId") Long userId, @Param("roleId") Integer roleId, @Param("operatorId") Long operatorId, @Param("expireTime") String expireTime);

    /**
     * 删除用户的某个角色
     */
    void deleteUserRole(@Param("userId") Long userId, @Param("roleId") Integer roleId);

    /**
     * 修改用户角色有效期
     */
    void updateUserRoleExpire(@Param("userId") Long userId, @Param("roleId") Integer roleId, @Param("expireTime") String expireTime);

    /**
     * 查询用户当前角色ID列表
     */
    List<Integer> selectUserRoleIds(@Param("userId") Long userId);

    /**
     * 查询用户所有违规记录（含已撤销，不受逻辑删除过滤）
     */
    List<Map<String, Object>> selectViolationListAll(@Param("userId") Long userId);
}
