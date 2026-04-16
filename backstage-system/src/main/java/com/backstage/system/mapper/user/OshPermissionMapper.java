package com.backstage.system.mapper.user;

import com.backstage.system.domain.user.OshPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/4/7
 * Time: 16:02
 */
public interface OshPermissionMapper extends BaseMapper<OshPermission> {

    List<Integer> selectPermissionIdsByRoleId(@Param("roleId") Integer roleId);

    List<String> selectPermissionCodeByIds(@Param("permissionIds") List<Integer> permissionIds);
}
