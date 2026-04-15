/*
 Navicat Premium Dump SQL

 Source Server         : backstage
 Source Server Type    : MySQL
 Source Server Version : 80408 (8.4.8)
 Source Host           : 43.242.200.25:3306
 Source Schema         : backstage

 Target Server Type    : MySQL
 Target Server Version : 80408 (8.4.8)
 File Encoding         : 65001

 Date: 12/04/2026 17:51:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for osh_website_user_rating
-- ----------------------------
DROP TABLE IF EXISTS `osh_website_user_rating`;
CREATE TABLE `osh_website_user_rating`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `website_id` bigint NULL DEFAULT NULL COMMENT '网站ID',
  `rating_type` tinyint NULL DEFAULT NULL COMMENT '评价类型: 1-好评, 2-中评, 3-差评',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `delete_flag` tinyint NULL DEFAULT 0 COMMENT '删除标记 0-未删除 1-已删除',
  `good_count` int NULL DEFAULT 0 COMMENT '好评数量',
  `mid_count` int NULL DEFAULT 0 COMMENT '中评数量',
  `bad_count` int NULL DEFAULT 0 COMMENT '差评数量',
  `collection_count` int NULL DEFAULT 0 COMMENT '收藏数量',
  `rating_score` decimal(10, 2) NULL DEFAULT NULL COMMENT '评分',
  `click_count` int NULL DEFAULT 0 COMMENT '点击数量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_website`(`user_id` ASC, `website_id` ASC) USING BTREE COMMENT '用户+网站唯一约束,防止重复评价',
  INDEX `idx_website_id`(`website_id` ASC) USING BTREE COMMENT '网站ID索引,用于统计某网站的评价',
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '用户ID索引,用于查询某用户的评价历史'
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '网站用户评价记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of osh_website_user_rating
-- ----------------------------
INSERT INTO `osh_website_user_rating` VALUES (1, 1, 1, 3, NULL, NULL, 0, 0, 0, 0, 0, NULL, 0, '2026-04-12 05:21:11', '2026-04-12 08:43:42');
INSERT INTO `osh_website_user_rating` VALUES (2, 2, 2, 1, NULL, NULL, 0, 0, 0, 0, 0, NULL, 0, '2026-04-12 05:21:34', '2026-04-12 05:21:34');
INSERT INTO `osh_website_user_rating` VALUES (3, 3, 3, 1, NULL, NULL, 0, 0, 0, 0, 0, NULL, 0, '2026-04-12 05:21:48', '2026-04-12 05:21:48');
INSERT INTO `osh_website_user_rating` VALUES (4, 4, 4, 1, NULL, NULL, 0, 0, 0, 0, 0, NULL, 0, '2026-04-12 05:22:00', '2026-04-12 05:22:00');
INSERT INTO `osh_website_user_rating` VALUES (5, 5, 5, 2, NULL, NULL, 0, 0, 0, 0, 0, NULL, 0, '2026-04-12 05:22:09', '2026-04-12 05:22:09');
INSERT INTO `osh_website_user_rating` VALUES (6, 1, 2, 2, NULL, NULL, 0, 0, 0, 0, 0, NULL, 0, '2026-04-12 05:22:39', '2026-04-12 05:22:39');

SET FOREIGN_KEY_CHECKS = 1;
