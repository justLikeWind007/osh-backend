package com.backstage.system.mapper.user;

import com.backstage.system.domain.user.OshRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/4/7
 * Time: 16:01
 */
public interface OshRoleMapper extends BaseMapper<OshRole> {

    List<Integer> getRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 通过 userId 直接查询角色 code（跳过中间表，一步到位）
     */
    String getRoleCodeByUserId(@Param("userId") Long userId);

    int deleteUserRole(Long userId);
}
