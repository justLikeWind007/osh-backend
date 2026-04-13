-- =============================================
-- 订单表扩展SQL - 添加课程ID和过期时间字段
-- 用于支持课程购买状态查询功能
-- 执行数据库: backstage (MySQL)
-- =============================================

-- 1. 为 osh_order_save 表添加 course_id 字段
ALTER TABLE `osh_order_save` 
ADD COLUMN `course_id` bigint DEFAULT NULL COMMENT '课程ID' AFTER `user_id`;

-- 2. 为 osh_order_save 表添加 expire_time 字段
ALTER TABLE `osh_order_save` 
ADD COLUMN `expire_time` datetime DEFAULT NULL COMMENT '到期时间' AFTER `type`;

-- 3. 添加复合索引（user_id, course_id, status）用于查询优化
ALTER TABLE `osh_order_save` 
ADD INDEX idx_user_course_status (`user_id`, `course_id`, `status`);

-- 4. 为 osh_user_course_progress 表添加索引（如果不存在）
ALTER TABLE `osh_user_course_progress` 
ADD INDEX idx_user_course (`user_id`, `course_id`);

-- =============================================
-- 测试数据：添加带课程ID和过期时间的订单示例
-- =============================================

-- 示例1：永久有效的订单（expire_time为NULL）
INSERT INTO `osh_order_save` (`school_id`, `user_id`, `course_id`, `no`, `status`, `price`, `total_price`, `type`, `expire_time`, `created_time`)
VALUES (11, 252, 1, '2026/03/20_permanent', 'closed', 299.00, '299.00', 'course', NULL, '2026-03-20 10:00:00');

-- 示例2：年度有效的订单（expire_time为一年后）
INSERT INTO `osh_order_save` (`school_id`, `user_id`, `course_id`, `no`, `status`, `price`, `total_price`, `type`, `expire_time`, `created_time`)
VALUES (11, 252, 2, '2026/03/20_yearly', 'closed', 199.00, '199.00', 'course', '2027-03-20 10:00:00', '2026-03-20 11:00:00');

-- 示例3：已过期的订单
INSERT INTO `osh_order_save` (`school_id`, `user_id`, `course_id`, `no`, `status`, `price`, `total_price`, `type`, `expire_time`, `created_time`)
VALUES (11, 252, 3, '2025/03/20_expired', 'closed', 99.00, '99.00', 'course', '2026-03-01 10:00:00', '2025-03-20 12:00:00');

-- =============================================
-- 验证查询
-- =============================================

-- 查看表结构
SHOW COLUMNS FROM osh_order_save LIKE '%course_id%';
SHOW COLUMNS FROM osh_order_save LIKE '%expire_time%';

-- 查看索引
SHOW INDEX FROM osh_order_save WHERE Key_name = 'idx_user_course_status';

-- 查询所有订单（带扩展字段）
SELECT id, user_id, course_id, status, type, expire_time, created_time 
FROM osh_order_save 
ORDER BY id DESC;
