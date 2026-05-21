-- ============================================================
-- 服务器拼团模块 - osh_group_work表新增服务器分配字段
-- 
-- 新增字段：
--   1. server_ip        服务器IP地址
--   2. ssh_port         SSH端口
--   3. ssh_username     SSH用户名
--   4. ssh_password     SSH密码（加密存储）
--
-- 目标数据库：backstage
-- MySQL 版本：8.4.8
-- 字符集：utf8mb4
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 新增服务器分配字段到 osh_group_work 表
ALTER TABLE `osh_group_work` 
    ADD COLUMN `server_ip` VARCHAR(50) DEFAULT NULL COMMENT '服务器IP地址' AFTER `server_expire_time`,
    ADD COLUMN `ssh_port` INT DEFAULT 22 COMMENT 'SSH端口' AFTER `server_ip`,
    ADD COLUMN `ssh_username` VARCHAR(50) DEFAULT NULL COMMENT 'SSH用户名' AFTER `ssh_port`,
    ADD COLUMN `ssh_password` VARCHAR(255) DEFAULT NULL COMMENT 'SSH密码（加密存储）' AFTER `ssh_username`;

-- 添加索引
ALTER TABLE `osh_group_work` 
    ADD INDEX `idx_server_ip` (`server_ip`);

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 验证脚本
-- ============================================================
-- SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT 
-- FROM INFORMATION_SCHEMA.COLUMNS 
-- WHERE TABLE_SCHEMA = 'backstage' AND TABLE_NAME = 'osh_group_work'
--   AND COLUMN_NAME IN ('server_ip', 'ssh_port', 'ssh_username', 'ssh_password');

SELECT
    id,
    group_status AS 拼团状态,  -- 应该是 1
    server_expire_time AS 到期时间,  -- 应该是 null
    custom_price AS 自定义价格,
    currentPrice
FROM osh_group_user_initiated
WHERE id = 11;