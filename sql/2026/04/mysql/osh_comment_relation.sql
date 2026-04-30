/*
 Navicat Premium Data Transfer

 Source Server         : backstage
 Source Server Type    : MySQL
 Source Server Version : 80408 (8.4.8)
 Source Host           : 43.242.200.25:3306
 Source Schema         : backstage

 Target Server Type    : MySQL
 Target Server Version : 80408 (8.4.8)
 File Encoding         : 65001

 Date: 21/03/2026 14:29:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for osh_comment_relation
-- ----------------------------
DROP TABLE IF EXISTS `osh_comment_relation`;
CREATE TABLE `osh_comment_relation` (
  `relation_id` bigint NOT NULL AUTO_INCREMENT,
  `comment_id` bigint NOT NULL COMMENT '评论 ID',
  `biz_type` tinyint NOT NULL COMMENT '业务类型：1-课程 2-商品 3-文章...',
  `biz_id` bigint NOT NULL COMMENT '业务 ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`relation_id`),
  UNIQUE KEY `uk_comment_biz` (`comment_id`,`biz_type`,`biz_id`),
  KEY `idx_biz` (`biz_type`,`biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='评论关联表';

SET FOREIGN_KEY_CHECKS = 1;
