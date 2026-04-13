/*
 * 课程模块功能增强 SQL 脚本
 * 创建时间：2026-03-27
 * 说明：为课程列表增加收藏数统计、标签显示等功能
 */

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 课程表添加收藏计数字段
-- ----------------------------
ALTER TABLE `osh_coures` 
ADD COLUMN `fava_count` int(11) NOT NULL DEFAULT 0 COMMENT '收藏数量' AFTER `bad_count`;

-- ----------------------------
-- 2. 为收藏表添加索引优化查询性能
-- ----------------------------
-- 检查索引是否存在，不存在则创建
-- 用于按类型和商品ID查询收藏数
CREATE INDEX IF NOT EXISTS `idx_type_goods` ON `osh_fava` (`type`, `goods_id`);

-- ----------------------------
-- 3. 初始化收藏数字段（基于现有收藏数据）
-- ----------------------------
UPDATE `osh_coures` c
SET `fava_count` = (
    SELECT COUNT(*) 
    FROM `osh_fava` f 
    WHERE f.`goods_id` = c.`id` AND f.`type` = 'course'
);

SET FOREIGN_KEY_CHECKS = 1;
