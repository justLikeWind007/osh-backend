package com.backstage.web.controller.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 单元测试：给所有拥有 VIP用户(role_id=3) 和 小班用户(role_id=4) 角色的用户
 * 设置有效期为 2026-08-01 00:00:00
 */
@SpringBootTest
public class UserRoleExpireTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 给所有现有的 VIP用户(role_id=3) 和 小班用户(role_id=4) 角色记录
     * 设置 expire_time = 2026-08-01 00:00:00
     * 仅更新 expire_time 为 NULL 的记录（未设置过有效期的）
     */
    @Test
    public void setExpireTimeForVipAndSmallClassRoles() {
        String sql = "UPDATE osh_user_role SET expire_time = '2026-08-01 00:00:00', update_time = NOW() " +
                     "WHERE role_id IN (3, 4) AND delete_flag = 0 AND expire_time IS NULL";
        int updated = jdbcTemplate.update(sql);
        System.out.println("已更新 " + updated + " 条 VIP/小班用户角色记录，有效期设为 2026-08-01 00:00:00");
    }
}
