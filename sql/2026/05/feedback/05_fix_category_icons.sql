-- ============================================
-- 反馈分类图标优化
-- 执行时间: 2026-05-07
-- 版本: V1.2
-- 功能：修复字符集并更新为 Emoji 图标
-- ============================================

USE backstage;

-- ============================================
-- 修改表字符集为 utf8mb4（支持 Emoji）
-- ============================================
ALTER TABLE `assistant_feedback_category` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- 确保 icon 字段使用 utf8mb4
-- ============================================
ALTER TABLE `assistant_feedback_category` 
MODIFY COLUMN `icon` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '分类图标（Emoji）';

-- ============================================
-- 更新所有分类的图标为 Emoji
-- ============================================
-- 注意：先设置为 NULL 再更新，避免字符编码冲突
UPDATE `assistant_feedback_category` SET `icon` = NULL WHERE `code` = 'announcement';
UPDATE `assistant_feedback_category` SET `icon` = '📢' WHERE `code` = 'announcement';

UPDATE `assistant_feedback_category` SET `icon` = NULL WHERE `code` = 'suggestion';
UPDATE `assistant_feedback_category` SET `icon` = '💡' WHERE `code` = 'suggestion';

UPDATE `assistant_feedback_category` SET `icon` = NULL WHERE `code` = 'bug';
UPDATE `assistant_feedback_category` SET `icon` = '🐛' WHERE `code` = 'bug';

UPDATE `assistant_feedback_category` SET `icon` = NULL WHERE `code` = 'question';
UPDATE `assistant_feedback_category` SET `icon` = '❓' WHERE `code` = 'question';

UPDATE `assistant_feedback_category` SET `icon` = NULL WHERE `code` = 'help';
UPDATE `assistant_feedback_category` SET `icon` = '🆘' WHERE `code` = 'help';

UPDATE `assistant_feedback_category` SET `icon` = NULL WHERE `code` = 'other';
UPDATE `assistant_feedback_category` SET `icon` = '📝' WHERE `code` = 'other';

-- ============================================
-- 验证结果
-- ============================================
SELECT 
    '✅ 反馈分类图标优化完成' AS message,
    COUNT(*) AS total_categories
FROM `assistant_feedback_category`;

SELECT 
    `code`,
    `name`,
    `icon`,
    HEX(`icon`) AS `icon_hex`,
    LENGTH(`icon`) AS `icon_length`,
    `sort_order`
FROM `assistant_feedback_category`
ORDER BY `sort_order`;
