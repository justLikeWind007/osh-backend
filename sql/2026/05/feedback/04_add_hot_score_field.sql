-- ============================================
-- 反馈热度分功能 - 添加 hot_score 字段
-- 执行时间: 2026-05-07
-- 版本: V1.1
-- 功能：支持新的热度算法（互动分锚点防刷机制）
-- ============================================

USE backstage;

-- ============================================
-- 添加 hot_score 字段
-- ============================================
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'backstage' AND TABLE_NAME = 'assistant_feedback' AND COLUMN_NAME = 'hot_score');

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE `assistant_feedback` ADD COLUMN `hot_score` INT DEFAULT 0 COMMENT ''热度分：互动分×4 + 有效浏览×1，其中超互动20倍浏览只算10%'' AFTER `favorite_count`',
    'SELECT ''hot_score 字段已存在'' AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================
-- 初始化热度分数据
-- ============================================
-- 算法：
-- 1. 互动分 = like_count × 4 + favorite_count × 3 + comment_count × 2
-- 2. 有效浏览 = min(view_count, 互动分×20) + max(0, view_count-互动分×20)/10
-- 3. 热度分 = 互动分 × 4 + 有效浏览 × 1

UPDATE `assistant_feedback`
SET `hot_score` = (
    -- 互动分 × 4
    (IFNULL(`like_count`, 0) * 4 + IFNULL(`favorite_count`, 0) * 3 + IFNULL(`comment_count`, 0) * 2) * 4
    +
    -- 有效浏览 × 1
    (
        LEAST(IFNULL(`view_count`, 0),
              (IFNULL(`like_count`, 0) * 4 + IFNULL(`favorite_count`, 0) * 3 + IFNULL(`comment_count`, 0) * 2) * 20
        )
        +
        GREATEST(0, IFNULL(`view_count`, 0) - (IFNULL(`like_count`, 0) * 4 + IFNULL(`favorite_count`, 0) * 3 + IFNULL(`comment_count`, 0) * 2) * 20) / 10
    )
)
WHERE `delete_flag` = 0;

-- ============================================
-- 更新热度排序索引（使用 hot_score 字段）
-- ============================================
-- 删除旧索引（如果存在）
SET @idx_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'backstage' AND TABLE_NAME = 'assistant_feedback' AND INDEX_NAME = 'idx_hot_score');

SET @sql = IF(@idx_exists > 0,
    'ALTER TABLE `assistant_feedback` DROP INDEX `idx_hot_score`',
    'SELECT ''idx_hot_score 索引不存在，无需删除'' AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 创建新索引（基于 hot_score）
SET @idx_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'backstage' AND TABLE_NAME = 'assistant_feedback' AND INDEX_NAME = 'idx_hot_score_v2');

SET @sql = IF(@idx_exists = 0,
    'ALTER TABLE `assistant_feedback` ADD INDEX `idx_hot_score_v2` (`hot_score` DESC, `create_time` DESC)',
    'SELECT ''idx_hot_score_v2 索引已存在'' AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================
-- 验证结果
-- ============================================
SELECT
    '✅ hot_score 字段添加完成' AS message,
    COUNT(*) AS total_count,
    AVG(`hot_score`) AS avg_hot_score,
    MAX(`hot_score`) AS max_hot_score
FROM `assistant_feedback`
WHERE `delete_flag` = 0;
