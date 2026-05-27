package com.backstage.system.service.user;

import com.backstage.RuoYiApplication;
import com.backstage.system.mapper.user.UserManageMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 数据修复测试：确保所有用户都拥有普通用户角色（role_id=1）。
 * 如果用户缺少该角色则自动补上。
 *
 * 运行方式：直接执行此测试类即可完成数据修复。
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RuoYiApplication.class)
public class UserDefaultRoleFixTest {

    /** 普通用户角色ID */
    private static final int DEFAULT_ROLE_ID = 1;

    @Autowired
    private DataSource dataSource;

    @Test
    public void shouldEnsureAllUsersHaveDefaultNormalRole() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            // 1. 查询所有没有普通用户角色的用户ID
            String findSql = "SELECT u.id FROM osh_user u " +
                    "WHERE u.delete_flag = 0 " +
                    "AND u.id NOT IN (" +
                    "  SELECT ur.user_id FROM osh_user_role ur " +
                    "  WHERE ur.role_id = ? AND ur.delete_flag = 0" +
                    ")";

            List<Long> userIdsWithoutDefaultRole = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(findSql)) {
                ps.setInt(1, DEFAULT_ROLE_ID);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        userIdsWithoutDefaultRole.add(rs.getLong("id"));
                    }
                }
            }

            System.out.println("缺少普通用户角色的用户数量: " + userIdsWithoutDefaultRole.size());

            if (userIdsWithoutDefaultRole.isEmpty()) {
                System.out.println("所有用户已拥有普通用户角色，无需修复。");
                return;
            }

            // 2. 批量补上普通用户角色（使用 ON DUPLICATE KEY UPDATE 避免冲突）
            String insertSql = "INSERT INTO osh_user_role (user_id, role_id, create_time, create_by, update_time, update_by, delete_flag) " +
                    "VALUES (?, ?, NOW(), 0, NOW(), 0, 0) " +
                    "ON DUPLICATE KEY UPDATE delete_flag = 0, update_time = NOW()";

            int fixedCount = 0;
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                for (Long userId : userIdsWithoutDefaultRole) {
                    ps.setLong(1, userId);
                    ps.setInt(2, DEFAULT_ROLE_ID);
                    ps.addBatch();
                    fixedCount++;

                    // 每 500 条执行一次
                    if (fixedCount % 500 == 0) {
                        ps.executeBatch();
                    }
                }
                ps.executeBatch();
            }

            System.out.println("已修复用户数量: " + fixedCount);
            System.out.println("修复的用户ID: " + userIdsWithoutDefaultRole);

            // 3. 验证修复结果
            try (PreparedStatement ps = conn.prepareStatement(findSql)) {
                ps.setInt(1, DEFAULT_ROLE_ID);
                try (ResultSet rs = ps.executeQuery()) {
                    List<Long> remaining = new ArrayList<>();
                    while (rs.next()) {
                        remaining.add(rs.getLong("id"));
                    }
                    assertTrue(remaining.isEmpty(), "仍有用户缺少普通用户角色: " + remaining);
                }
            }
        }
    }
}
