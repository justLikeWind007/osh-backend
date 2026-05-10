-- ============================================
-- 反馈点赞/收藏功能 - 添加冗余字段
-- 执行时间: 2026-05-07
-- 版本: V1.0.2
-- 功能：性能优化（避免 JOIN 查询）
-- ============================================

USE backstage;

-- ============================================
-- 在反馈主表新增冗余字段
-- ============================================
-- 检查并添加 like_count 字段
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'backstage' AND TABLE_NAME = 'assistant_feedback' AND COLUMN_NAME = 'like_count');

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE `assistant_feedback` ADD COLUMN `like_count` INT DEFAULT 0 COMMENT ''点赞数量'' AFTER `view_count`',
    'SELECT ''like_count 字段已存在'' AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 favorite_count 字段
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'backstage' AND TABLE_NAME = 'assistant_feedback' AND COLUMN_NAME = 'favorite_count');

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE `assistant_feedback` ADD COLUMN `favorite_count` INT DEFAULT 0 COMMENT ''收藏数量'' AFTER `like_count`',
    'SELECT ''favorite_count 字段已存在'' AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================
-- 添加热度排序索引
-- ============================================
SET @idx_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
    WHERE TABLE_SCHEMA = 'backstage' AND TABLE_NAME = 'assistant_feedback' AND INDEX_NAME = 'idx_hot_score');

SET @sql = IF(@idx_exists = 0,
    'ALTER TABLE `assistant_feedback` ADD INDEX `idx_hot_score` (`like_count` DESC, `favorite_count` DESC, `comment_count` DESC, `create_time` DESC)',
    'SELECT ''idx_hot_score 索引已存在'' AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================
-- 验证结果
-- ============================================
SELECT '✅ 冗余字段添加完成' AS message;
SELECT '✅ 热度排序索引创建完成' AS message;

-- 查看字段
DESC assistant_feedback;
