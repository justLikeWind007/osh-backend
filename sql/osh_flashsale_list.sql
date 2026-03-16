/*
 Navicat Premium Data Transfer

 Source Server         : 100Gsever
 Source Server Type    : MySQL
 Source Server Version : 80408
 Source Host           : 43.242.200.25:3306
 Source Schema         : backstage

 Target Server Type    : MySQL
 Target Server Version : 80408
 File Encoding         : 65001

 Date: 15/03/2026 14:22:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for osh_flashsale_list
-- ----------------------------
DROP TABLE IF EXISTS `osh_flashsale_list`;
CREATE TABLE `osh_flashsale_list`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `school_id` bigint NULL DEFAULT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  `no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `price` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `total_price` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `flashsale_id` bigint NULL DEFAULT NULL,
  `updated_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '创建秒杀订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of osh_flashsale_list
-- ----------------------------
INSERT INTO `osh_flashsale_list` VALUES (1, 1, 1, '2026/03/12_24a68b57-', 'pending', '0.0', '0.0', 'flashsale', 12, '2026-03-12T03:49:35.553Z', '2026-03-12T03:49:35.553Z');
INSERT INTO `osh_flashsale_list` VALUES (2, 1, 1, '2026/03/12_72f8b070-', 'pending', '0.0', '0.0', 'flashsale', 1, '2026-03-12T03:52:41.581Z', '2026-03-12T03:52:41.581Z');
INSERT INTO `osh_flashsale_list` VALUES (3, 1, 1, '2026/03/12_96c320ac-', 'pending', '999', '200', 'flashsale', 1, '2026-03-12T04:40:50.826Z', '2026-03-12T04:40:50.826Z');
INSERT INTO `osh_flashsale_list` VALUES (4, 1, 1, '2026/03/12_9cb51198-', 'pending', '999', '200', 'flashsale', 1, '2026-03-12T04:42:37.521Z', '2026-03-12T04:42:37.521Z');
INSERT INTO `osh_flashsale_list` VALUES (5, 1, 1, '2026/03/12_5f5d0ed9-', 'pending', '999', '200', 'flashsale', 1, '2026-03-12T04:44:55.072Z', '2026-03-12T04:44:55.072Z');
INSERT INTO `osh_flashsale_list` VALUES (6, 1, 1, '2026/03/12_407f1d2e-', 'pending', '8999', '0.00', 'flashsale', 2, '2026-03-12T04:45:10.582Z', '2026-03-12T04:45:10.582Z');
INSERT INTO `osh_flashsale_list` VALUES (7, 1, 1, '2026/03/12_df13b7c2-', 'pending', '8999', '0.00', 'flashsale', 2, '2026-03-12T04:46:13.647Z', '2026-03-12T04:46:13.647Z');
INSERT INTO `osh_flashsale_list` VALUES (8, 1, 1, '2026/03/12_f0b227e1-', 'pending', '8999', '0.00', 'flashsale', 2, '2026-03-12T09:07:11.287Z', '2026-03-12T09:07:11.287Z');
INSERT INTO `osh_flashsale_list` VALUES (9, 1, 1, '2026/03/13_c1e214ce-', 'pending', '8999.00', '222.00', 'flashsale', 2, '2026-03-13T06:08:43.505Z', '2026-03-13T06:08:43.505Z');
INSERT INTO `osh_flashsale_list` VALUES (10, 1, 1, '2026/03/13_d8b2403c-', 'pending', '8999.00', '222.00', 'flashsale', 2, '2026-03-13T11:05:15.678Z', '2026-03-13T11:05:15.678Z');

SET FOREIGN_KEY_CHECKS = 1;
