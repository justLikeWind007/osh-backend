-- ============================================================
-- 拼团模块性能优化脚本
-- 创建时间: 2026-05-08
-- 说明: 为 osh_group_order 表添加索引,优化用户发起拼团列表查询性能
-- ============================================================

-- 1. 检查索引是否已存在
SELECT 
    INDEX_NAME, 
    COLUMN_NAME, 
    SEQ_IN_INDEX 
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'osh_group_order' 
  AND COLUMN_NAME = 'group_activity_id';

-- 2. 添加索引(如果不存在)
-- 说明: 优化 osh_group_user_initiated 关联 osh_group_activity 的查询性能
-- 影响: 提升 selectUserInitiatedActivityList 查询速度
ALTER TABLE `osh_group_order` 
ADD INDEX `idx_group_activity_id` (`group_activity_id`) 
COMMENT '优化用户发起拼团列表查询性能';

-- 3. 验证索引创建成功
SHOW INDEX FROM `osh_group_order` WHERE Key_name = 'idx_group_activity_id';

-- ============================================================
-- 可选优化: 在 osh_group_user_initiated 表添加 activity_id 字段
-- 说明: 这是长期优化方案,可以减少一次JOIN操作
-- 注意: 需要同时修改 Java 代码,在创建拼团时保存 activity_id
-- ============================================================

-- 4. 添加 activity_id 字段(可选,需评估影响)
-- ALTER TABLE `osh_group_user_initiated` 
-- ADD COLUMN `activity_id` bigint NOT NULL DEFAULT 0 COMMENT '关联活动模板ID → osh_group_activity.id' 
-- AFTER `user_id`;

-- 5. 为 activity_id 添加索引(可选)
-- ALTER TABLE `osh_group_user_initiated` 
-- ADD INDEX `idx_activity_id` (`activity_id`) 
-- COMMENT '优化用户发起拼团列表查询性能';

-- 6. 回填历史数据(如果添加了 activity_id 字段)
-- UPDATE osh_group_user_initiated gui
-- INNER JOIN osh_group_order go ON gui.order_id = go.id
-- SET gui.activity_id = go.group_activity_id
-- WHERE gui.activity_id = 0;

-- ============================================================
-- 性能测试查询
-- 说明: 使用 EXPLAIN 分析查询执行计划,验证索引是否生效
-- ============================================================

EXPLAIN 
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
LEFT JOIN osh_group_order go ON gui.order_id = go.id
LEFT JOIN osh_group_activity ga ON go.group_activity_id = ga.id
WHERE gui.delete_flag = 0
ORDER BY gui.initiate_time DESC;

-- 预期结果:
-- - osh_group_order 表应使用 idx_group_activity_id 索引
-- - type 列应为 ref 或 eq_ref(而非 ALL)
-- - rows 列应显示较少的扫描行数
