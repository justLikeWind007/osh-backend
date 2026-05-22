-- ============================================================
-- 拼团模块表结构修复脚本(方案A - 最小改动)
-- 创建时间: 2026-05-08
-- 说明: 为 osh_group_user_initiated 表添加 activity_id 字段
-- ============================================================

-- ==================== 执行前准备 ====================

-- 1. 检查表结构
DESC osh_group_user_initiated;

-- 2. 检查是否已存在 activity_id 字段
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    IS_NULLABLE, 
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'osh_group_user_initiated' 
  AND COLUMN_NAME = 'activity_id';

-- 3. 备份数据(重要!)
-- 在执行此脚本前,请先执行:
-- mysqldump -u root -p your_database osh_group_user_initiated > backup_osh_group_user_initiated_$(date +%Y%m%d_%H%M%S).sql

-- ==================== 第一步:添加字段和索引 ====================

-- 4. 添加 activity_id 字段
ALTER TABLE `osh_group_user_initiated` 
ADD COLUMN `activity_id` bigint NOT NULL DEFAULT 0 COMMENT '关联活动模板ID → osh_group_activity.id' 
AFTER `user_id`;

-- 5. 为 activity_id 添加索引
ALTER TABLE `osh_group_user_initiated` 
ADD INDEX `idx_activity_id` (`activity_id`) 
COMMENT '优化活动模板关联查询';

-- 6. 为 osh_group_order 表添加索引
ALTER TABLE `osh_group_order` 
ADD INDEX `idx_group_activity_id` (`group_activity_id`) 
COMMENT '优化用户发起拼团列表查询性能';

-- ==================== 第二步:验证字段添加 ====================

-- 7. 验证 activity_id 字段已添加
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    IS_NULLABLE, 
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'osh_group_user_initiated' 
  AND COLUMN_NAME = 'activity_id';

-- 预期结果:
-- COLUMN_NAME: activity_id
-- DATA_TYPE: bigint
-- IS_NULLABLE: NO
-- COLUMN_DEFAULT: 0
-- COLUMN_COMMENT: 关联活动模板ID → osh_group_activity.id

-- 8. 验证索引已创建
SHOW INDEX FROM `osh_group_user_initiated` WHERE Key_name = 'idx_activity_id';
SHOW INDEX FROM `osh_group_order` WHERE Key_name = 'idx_group_activity_id';

-- ==================== 第三步:回填历史数据 ====================

-- 9. 查看需要回填的数据量
SELECT COUNT(*) AS total_records
FROM osh_group_user_initiated
WHERE activity_id = 0 AND delete_flag = 0;

-- 10. 回填 activity_id(通过 order 表关联)
UPDATE osh_group_user_initiated gui
INNER JOIN osh_group_order go ON gui.order_id = go.id
SET gui.activity_id = go.group_activity_id
WHERE gui.activity_id = 0 
  AND go.group_activity_id IS NOT NULL;

-- 11. 查看回填结果
SELECT 
    activity_id,
    COUNT(*) AS count
FROM osh_group_user_initiated
WHERE delete_flag = 0
GROUP BY activity_id
ORDER BY count DESC;

-- ==================== 第四步:数据完整性验证 ====================

-- 12. 验证数据一致性(activity_id 与 order.group_activity_id 是否一致)
SELECT 
    gui.id AS initiated_id,
    gui.activity_id,
    go.group_activity_id,
    CASE 
        WHEN gui.activity_id = go.group_activity_id THEN '✅ 一致'
        WHEN gui.activity_id = 0 AND go.group_activity_id IS NULL THEN '⚠️ 订单无活动ID'
        ELSE '❌ 不一致'
    END AS status
FROM osh_group_user_initiated gui
LEFT JOIN osh_group_order go ON gui.order_id = go.id
WHERE gui.delete_flag = 0
LIMIT 100;

-- 13. 查找不一致的记录(如果有,需要人工处理)
SELECT 
    gui.id AS initiated_id,
    gui.user_id,
    gui.activity_id,
    go.group_activity_id,
    gui.order_id
FROM osh_group_user_initiated gui
LEFT JOIN osh_group_order go ON gui.order_id = go.id
WHERE gui.delete_flag = 0
  AND gui.activity_id != 0
  AND (go.group_activity_id IS NULL OR gui.activity_id != go.group_activity_id);

-- 14. 查找 order_id 为 NULL 的记录(无法回填 activity_id)
SELECT 
    id,
    user_id,
    order_id,
    activity_id,
    initiate_time
FROM osh_group_user_initiated
WHERE delete_flag = 0
  AND order_id IS NULL
  AND activity_id = 0;

-- ==================== 第五步:查询性能测试 ====================

-- 15. 优化前的查询(需要两次JOIN)
EXPLAIN 
SELECT 
    gui.id,
    COALESCE(ga.title, '未知活动') AS title,
    COALESCE(ga.cpu, '待定') AS cpu,
    gui.custom_price AS base_price,
    gui.current_num,
    gui.min_num AS group_min_num,
    CASE 
        WHEN gui.group_status = 0 THEN 1
        WHEN gui.group_status = 1 THEN 2
        WHEN gui.group_status = 2 THEN 3
        ELSE 1
    END AS status
FROM osh_group_user_initiated gui
LEFT JOIN osh_group_order go ON gui.order_id = go.id
LEFT JOIN osh_group_activity ga ON go.group_activity_id = ga.id
WHERE gui.delete_flag = 0
ORDER BY gui.initiate_time DESC;

-- 16. 优化后的查询(直接关联 activity_id)
EXPLAIN 
SELECT 
    gui.id,
    COALESCE(ga.title, '未知活动') AS title,
    COALESCE(ga.cpu, '待定') AS cpu,
    gui.custom_price AS base_price,
    gui.current_num,
    gui.min_num AS group_min_num,
    CASE 
        WHEN gui.group_status = 0 THEN 1
        WHEN gui.group_status = 1 THEN 2
        WHEN gui.group_status = 2 THEN 3
        ELSE 1
    END AS status
FROM osh_group_user_initiated gui
LEFT JOIN osh_group_activity ga ON gui.activity_id = ga.id
WHERE gui.delete_flag = 0
ORDER BY gui.initiate_time DESC;

-- 预期结果对比:
-- 优化前: 需要扫描 osh_group_order 和 osh_group_activity 两张表
-- 优化后: 只需扫描 osh_group_activity 一张表,使用 idx_activity_id 索引

-- ==================== 第六步:实际查询测试 ====================

-- 17. 测试完整查询(优化后)
SELECT 
    gui.id,
    COALESCE(ga.title, '未知活动') AS title,
    COALESCE(ga.cpu, '待定') AS cpu,
    COALESCE(ga.memory, '待定') AS memory,
    COALESCE(ga.storage, '待定') AS storage,
    gui.custom_price AS base_price,
    gui.duration AS total_duration,
    gui.current_num,
    gui.min_num AS group_min_num,
    gui.max_num AS group_max_num,
    CASE 
        WHEN gui.group_status = 0 THEN 1
        WHEN gui.group_status = 1 THEN 2
        WHEN gui.group_status = 2 THEN 3
        ELSE 1
    END AS status,
    gui.initiate_time AS start_time,
    gui.server_start_time,
    gui.server_expire_time AS server_end_time,
    ga.server_tutorial_url,
    ga.cover
FROM osh_group_user_initiated gui
LEFT JOIN osh_group_activity ga ON gui.activity_id = ga.id
WHERE gui.delete_flag = 0
ORDER BY gui.initiate_time DESC
LIMIT 10;

-- ==================== 第七步:统计信息 ====================

-- 18. 查看各活动的发起拼团数量
SELECT 
    ga.id AS activity_id,
    ga.title,
    COUNT(gui.id) AS initiated_count,
    SUM(CASE WHEN gui.group_status = 0 THEN 1 ELSE 0 END) AS recruiting_count,
    SUM(CASE WHEN gui.group_status = 1 THEN 1 ELSE 0 END) AS success_count,
    SUM(CASE WHEN gui.group_status = 2 THEN 1 ELSE 0 END) AS cancelled_count
FROM osh_group_activity ga
LEFT JOIN osh_group_user_initiated gui ON ga.id = gui.activity_id AND gui.delete_flag = 0
WHERE ga.delete_flag = 0
GROUP BY ga.id, ga.title
ORDER BY initiated_count DESC;

-- 19. 查看用户发起拼团统计
SELECT 
    gui.user_id,
    COUNT(*) AS total_initiated,
    SUM(CASE WHEN gui.group_status = 0 THEN 1 ELSE 0 END) AS recruiting_count,
    SUM(CASE WHEN gui.group_status = 1 THEN 1 ELSE 0 END) AS success_count,
    AVG(gui.current_num) AS avg_current_num
FROM osh_group_user_initiated gui
WHERE gui.delete_flag = 0
GROUP BY gui.user_id
ORDER BY total_initiated DESC
LIMIT 20;

-- ==================== 执行完成 ====================

-- 20. 最终验证
SELECT 
    '表结构修复完成' AS message,
    (SELECT COUNT(*) FROM osh_group_user_initiated WHERE activity_id > 0) AS records_with_activity_id,
    (SELECT COUNT(*) FROM osh_group_user_initiated WHERE activity_id = 0) AS records_without_activity_id,
    (SELECT COUNT(*) FROM osh_group_user_initiated WHERE delete_flag = 0) AS total_active_records;

-- ==================== 回滚脚本(仅在出现问题时执行) ====================

-- 如果出现严重问题,可以执行以下脚本回滚:

-- 回滚 Step 1: 删除索引
-- ALTER TABLE `osh_group_user_initiated` DROP INDEX `idx_activity_id`;
-- ALTER TABLE `osh_group_order` DROP INDEX `idx_group_activity_id`;

-- 回滚 Step 2: 删除字段
-- ALTER TABLE `osh_group_user_initiated` DROP COLUMN `activity_id`;

-- 回滚 Step 3: 恢复备份数据
-- mysql -u root -p your_database < backup_osh_group_user_initiated_20260508.sql

-- ============================================================
-- 脚本执行完毕
-- 请检查以上验证步骤的输出,确认数据一致性
-- 如有异常,请参考回滚脚本
-- ============================================================
