-- ============================================
-- 反馈点赞/收藏功能 - 初始化冗余字段
-- 执行时间: 2026-05-07
-- 版本: V1.0.3
-- 功能：如果有历史数据，初始化冗余字段
-- 注意：如果数据量大，建议分批执行
-- ============================================

USE backstage;

-- ============================================
-- 初始化现有数据的冗余字段
-- ============================================
UPDATE `assistant_feedback` f
SET 
    f.like_count = IFNULL((
        SELECT COUNT(*) 
        FROM `assistant_feedback_like` l 
        WHERE l.feedback_id = f.id
    ), 0),
    f.favorite_count = IFNULL((
        SELECT COUNT(*) 
        FROM `assistant_feedback_favorite` fav 
        WHERE fav.feedback_id = f.id
    ), 0)
WHERE f.delete_flag = 0;

-- ============================================
-- 验证结果
-- ============================================
SELECT '✅ 冗余字段初始化完成' AS message;

SELECT 
    COUNT(*) AS total_feedback,
    SUM(like_count) AS total_likes,
    SUM(favorite_count) AS total_favorites
FROM assistant_feedback
WHERE delete_flag = 0;
