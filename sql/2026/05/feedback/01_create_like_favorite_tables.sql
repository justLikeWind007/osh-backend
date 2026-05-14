-- ============================================
-- 反馈点赞/收藏功能 - 创建表
-- 执行时间: 2026-05-07
-- 版本: V1.0.1
-- 功能：支持反馈热度排序
-- ============================================

USE backstage;

-- ============================================
-- 创建反馈点赞表
-- ============================================
CREATE TABLE IF NOT EXISTS `assistant_feedback_like` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞ID',
    `feedback_id` BIGINT NOT NULL COMMENT '反馈ID',
    `user_id` BIGINT NOT NULL COMMENT '点赞用户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY `uk_feedback_user` (`feedback_id`, `user_id`) COMMENT '唯一索引：防止重复点赞',
    INDEX `idx_user_id` (`user_id`) COMMENT '用户点赞历史查询',
    INDEX `idx_create_time` (`create_time`) COMMENT '时间范围查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反馈点赞表';

-- ============================================
-- 创建反馈收藏表
-- ============================================
CREATE TABLE IF NOT EXISTS `assistant_feedback_favorite` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    `feedback_id` BIGINT NOT NULL COMMENT '反馈ID',
    `user_id` BIGINT NOT NULL COMMENT '收藏用户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    UNIQUE KEY `uk_feedback_user` (`feedback_id`, `user_id`) COMMENT '唯一索引：防止重复收藏',
    INDEX `idx_user_id` (`user_id`) COMMENT '用户收藏历史查询',
    INDEX `idx_create_time` (`create_time`) COMMENT '时间范围查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反馈收藏表';

-- ============================================
-- 验证结果
-- ============================================
SELECT '✅ 点赞表创建完成' AS message;
SELECT '✅ 收藏表创建完成' AS message;
