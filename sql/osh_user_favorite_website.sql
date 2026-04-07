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

 Date: 31/03/2026 20:08:23
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for osh_user_favorite_website
-- ----------------------------
DROP TABLE IF EXISTS `osh_user_favorite_website`;
CREATE TABLE `osh_user_favorite_website`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户 ID',
  `website_id` bigint NULL DEFAULT NULL COMMENT '网站 ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '收藏时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '备注',
  `del_flag` tinyint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_website`(`user_id` ASC, `website_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_website_id`(`website_id` ASC) USING BTREE,
  INDEX `idx_del_flag`(`del_flag` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户收藏网站表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of osh_user_favorite_website
-- ----------------------------
INSERT INTO `osh_user_favorite_website` VALUES (1, 1, 1, 'admin', '2026-03-25 13:30:57', 'admin', '2026-03-25 13:30:57', '收藏常用代码平台', 0);
INSERT INTO `osh_user_favorite_website` VALUES (2, 1, 2, 'admin', '2026-03-25 13:30:57', 'admin', '2026-03-25 13:30:57', '技术学习必备', 0);
INSERT INTO `osh_user_favorite_website` VALUES (3, 1, 3, 'admin', '2026-03-25 13:30:57', 'admin', '2026-03-25 13:30:57', '算法刷题专用', 0);
INSERT INTO `osh_user_favorite_website` VALUES (4, 2, 4, 'admin', '2026-03-25 13:30:57', 'admin', '2026-03-25 13:30:57', 'IT技术社区', 0);
INSERT INTO `osh_user_favorite_website` VALUES (5, 2, 5, 'admin', '2026-03-25 13:30:57', 'admin', '2026-03-25 13:30:57', '国际编程问答', 0);
INSERT INTO `osh_user_favorite_website` VALUES (6, 2, 6, 'admin', '2026-03-25 13:30:57', 'admin', '2026-03-25 13:30:57', '国产代码托管', 0);
INSERT INTO `osh_user_favorite_website` VALUES (7, 3, 7, 'admin', '2026-03-25 13:30:57', 'admin', '2026-03-25 13:30:57', 'IT学习教程', 0);
INSERT INTO `osh_user_favorite_website` VALUES (8, 3, 8, 'admin', '2026-03-25 13:30:57', 'admin', '2026-03-25 13:30:57', '技术资讯平台', 0);
INSERT INTO `osh_user_favorite_website` VALUES (9, 3, 9, 'admin', '2026-03-25 13:30:57', 'admin', '2026-03-25 13:30:57', '开源社区收藏', 0);
INSERT INTO `osh_user_favorite_website` VALUES (10, 4, 10, 'admin', '2026-03-25 13:30:57', 'admin', '2026-03-25 13:30:57', '技术博客平台', 0);
INSERT INTO `osh_user_favorite_website` VALUES (11, 4, 11, 'admin', '2026-03-25 13:30:57', 'admin', '2026-03-25 13:30:57', '求职刷题必备', 0);
INSERT INTO `osh_user_favorite_website` VALUES (12, 4, 12, 'admin', '2026-03-25 13:30:57', 'admin', '2026-03-25 13:30:57', '云计算学习', 0);
INSERT INTO `osh_user_favorite_website` VALUES (13, 5, 13, 'admin', '2026-03-25 13:30:57', 'admin', '2026-03-25 13:30:57', '腾讯云技术', 0);
INSERT INTO `osh_user_favorite_website` VALUES (14, 5, 14, 'admin', '2026-03-25 13:30:57', 'admin', '2026-03-25 13:30:57', '免费IT视频', 0);
INSERT INTO `osh_user_favorite_website` VALUES (15, 5, 15, 'admin', '2026-03-25 13:30:57', 'admin', '2026-03-25 13:30:57', 'Python官方文档', 0);

SET FOREIGN_KEY_CHECKS = 1;
