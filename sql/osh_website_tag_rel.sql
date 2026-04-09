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

 Date: 07/04/2026 23:52:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for osh_website_tag_rel
-- ----------------------------
DROP TABLE IF EXISTS `osh_website_tag_rel`;
CREATE TABLE `osh_website_tag_rel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `website_id` bigint NULL DEFAULT NULL COMMENT '网站 ID',
  `tag_id` bigint NULL DEFAULT NULL COMMENT '标签 ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_flag` tinyint NULL DEFAULT NULL COMMENT '删除标志（0-未删除 1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_website_tag`(`website_id` ASC, `tag_id` ASC) USING BTREE,
  INDEX `idx_website_id`(`website_id` ASC) USING BTREE,
  INDEX `idx_tag_id`(`tag_id` ASC) USING BTREE,
  INDEX `idx_del_flag`(`delete_flag` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '网站与标签关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of osh_website_tag_rel
-- ----------------------------
INSERT INTO `osh_website_tag_rel` VALUES (1, 1, 1, 'admin', '2026-03-26 12:22:07', 'admin', '2026-03-26 12:22:07', 0);
INSERT INTO `osh_website_tag_rel` VALUES (2, 1, 9, 'admin', '2026-03-26 12:22:07', 'admin', '2026-03-26 12:22:07', 0);
INSERT INTO `osh_website_tag_rel` VALUES (3, 2, 1, 'admin', '2026-03-26 12:22:07', 'admin', '2026-03-26 12:22:07', 0);
INSERT INTO `osh_website_tag_rel` VALUES (4, 2, 10, 'admin', '2026-03-26 12:22:07', 'admin', '2026-03-26 12:22:07', 0);
INSERT INTO `osh_website_tag_rel` VALUES (5, 3, 5, 'admin', '2026-03-26 12:22:07', 'admin', '2026-03-26 12:22:07', 0);
INSERT INTO `osh_website_tag_rel` VALUES (6, 4, 1, 'admin', '2026-03-26 12:22:07', 'admin', '2026-03-26 12:22:07', 0);
INSERT INTO `osh_website_tag_rel` VALUES (7, 4, 2, 'admin', '2026-03-26 12:22:07', 'admin', '2026-03-26 12:22:07', 0);
INSERT INTO `osh_website_tag_rel` VALUES (8, 5, 2, 'admin', '2026-03-26 12:22:07', 'admin', '2026-03-26 12:22:07', 0);
INSERT INTO `osh_website_tag_rel` VALUES (9, 6, 9, 'admin', '2026-03-26 12:22:07', 'admin', '2026-03-26 12:22:07', 0);
INSERT INTO `osh_website_tag_rel` VALUES (10, 7, 10, 'admin', '2026-03-26 12:22:07', 'admin', '2026-03-26 12:22:07', 0);
INSERT INTO `osh_website_tag_rel` VALUES (11, 8, 4, 'admin', '2026-03-26 12:22:07', 'admin', '2026-03-26 12:22:07', 0);
INSERT INTO `osh_website_tag_rel` VALUES (12, 9, 15, 'admin', '2026-03-26 12:22:07', 'admin', '2026-03-26 12:22:07', 0);
INSERT INTO `osh_website_tag_rel` VALUES (13, 10, 2, 'admin', '2026-03-26 12:22:07', 'admin', '2026-03-26 12:22:07', 0);
INSERT INTO `osh_website_tag_rel` VALUES (14, 11, 5, 'admin', '2026-03-26 12:22:07', 'admin', '2026-03-26 12:22:07', 0);
INSERT INTO `osh_website_tag_rel` VALUES (15, 12, 4, 'admin', '2026-03-26 12:22:07', 'admin', '2026-03-26 12:22:07', 0);

SET FOREIGN_KEY_CHECKS = 1;
