package com.backstage.system.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;

//@Component // 🚩 想跑的时候加上这个注解，启动项目即自动执行
public class DbSchemaTool implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 开始检查数据库表结构...");
        autoAddDeleteFlag();
        System.out.println("🏁 数据库表结构检查完成！");
    }

    public void autoAddDeleteFlag() {
        // 1. 查询所有以 osh_ 开头且没有 delete_flag 字段的表
        String findTablesSql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                "WHERE TABLE_SCHEMA = DATABASE() " +
                "AND TABLE_NAME LIKE 'osh_%' " +
                "AND TABLE_NAME NOT IN (" +
                "  SELECT TABLE_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                "  WHERE TABLE_SCHEMA = DATABASE() AND COLUMN_NAME = 'delete_flag'" +
                ")";

        List<String> tableNames = jdbcTemplate.queryForList(findTablesSql, String.class);

        if (tableNames.isEmpty()) {
            System.out.println("✨ 所有 osh_ 开头的表都已经包含 delete_flag 字段，无需操作。");
            return;
        }

        // 2. 循环执行 ALTER 语句
        for (String tableName : tableNames) {
            try {
                // 💡 建议顺便把索引加上，否则你的拦截器在大数据量下会拖慢速度
                String alterSql = String.format(
                    "ALTER TABLE %s ADD COLUMN delete_flag tinyint(1) NOT NULL DEFAULT 0 " +
                    "COMMENT '逻辑删除标记(0未删,1已删)', ADD INDEX idx_delete_flag(delete_flag)", 
                    tableName
                );
                jdbcTemplate.execute(alterSql);
                System.out.println("✅ 成功为表 [" + tableName + "] 添加字段及索引");
            } catch (Exception e) {
                System.err.println("❌ 表 [" + tableName + "] 添加失败: " + e.getMessage());
            }
        }
    }
}