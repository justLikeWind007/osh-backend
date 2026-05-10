-- ============================================
-- 反馈点赞/收藏功能 - 回滚脚本
-- 执行时间: 2026-05-07
-- 警告：此脚本会删除所有点赞/收藏数据！
-- ============================================

USE backstage;

-- ============================================
-- 回滚步骤（按顺序执行）
-- ============================================

-- 1. 删除热度排序索引
ALTER TABLE `assistant_feedback` DROP INDEX IF EXISTS `idx_hot_score`;
ALTER TABLE `assistant_feedback` DROP INDEX IF EXISTS `idx_hot_score_v2`;

-- 2. 删除冗余字段
ALTER TABLE `assistant_feedback` DROP COLUMN IF EXISTS `hot_score`;
ALTER TABLE `assistant_feedback` DROP COLUMN IF EXISTS `favorite_count`;
ALTER TABLE `assistant_feedback` DROP COLUMN IF EXISTS `like_count`;

-- 3. 删除收藏表
DROP TABLE IF EXISTS `assistant_feedback_favorite`;

-- 4. 删除点赞表
DROP TABLE IF EXISTS `assistant_feedback_like`;

-- 5. 恢复分类图标为英文单词（可选）
UPDATE `assistant_feedback_category` SET `icon` = 'announcement' WHERE `code` = 'announcement';
UPDATE `assistant_feedback_category` SET `icon` = 'suggestion' WHERE `code` = 'suggestion';
UPDATE `assistant_feedback_category` SET `icon` = 'bug' WHERE `code` = 'bug';
UPDATE `assistant_feedback_category` SET `icon` = 'question' WHERE `code` = 'question';
UPDATE `assistant_feedback_category` SET `icon` = 'help' WHERE `code` = 'help';
UPDATE `assistant_feedback_category` SET `icon` = 'other' WHERE `code` = 'other';

-- ============================================
-- 验证结果
-- ============================================
SELECT '✅ 回滚完成' AS message;
SELECT '⚠️  所有点赞/收藏数据已删除' AS warning;
