package com.backstage.system.mapper.user;

import com.backstage.system.domain.user.OshRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/4/7
 * Time: 16:01
 */
public interface OshRoleMapper extends BaseMapper<OshRole> {

    Integer getRoleIdByUserId(@Param("userId") Long userId);

    int deleteUserRole(Long userId);
}
