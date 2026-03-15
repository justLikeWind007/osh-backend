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

 Date: 15/03/2026 14:25:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for osh_order_save
-- ----------------------------
DROP TABLE IF EXISTS `osh_order_save`;
CREATE TABLE `osh_order_save`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `school_id` bigint NULL DEFAULT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  `no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `price` decimal(10, 2) NULL DEFAULT NULL,
  `total_price` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `updated_time` datetime NULL DEFAULT NULL,
  `created_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '创建订单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of osh_order_save
-- ----------------------------
INSERT INTO `osh_order_save` VALUES (1, 11, 252, '2021617212839_w8riwidsp', 'pendding', 79.00, '99.00', 'default', '2021-06-17 13:28:40', '2021-06-17 13:28:40');
INSERT INTO `osh_order_save` VALUES (3, 11, 252, '2026/03/07_5c9d329c-', 'pendding', 20.00, '20.00', 'other', '2026-03-07 14:30:42', '2026-03-07 14:30:42');
INSERT INTO `osh_order_save` VALUES (4, 11, 252, '2026/03/07_4b4e8975-', 'pendding', 5.00, '5.00', 'course', '2026-03-07 14:36:07', '2026-03-07 14:36:07');
INSERT INTO `osh_order_save` VALUES (5, 11, 252, '2026/03/07_d4cc9fc2-', 'pendding', 5999.00, '5994.00', 'course', '2026-03-07 15:34:15', '2026-03-07 15:34:15');
INSERT INTO `osh_order_save` VALUES (6, 11, 252, '2026/03/08_6f188df1-', 'pendding', 5999.00, '5994.00', 'course', '2026-03-08 15:37:09', '2026-03-08 15:37:09');
INSERT INTO `osh_order_save` VALUES (7, 11, 252, '2026/03/10_d7ff403e-', 'pendding', 5999.00, '5994.00', 'course', '2026-03-10 11:53:17', '2026-03-10 11:53:17');
INSERT INTO `osh_order_save` VALUES (8, 11, 252, '2026/03/10_29dac831-', 'pendding', 5999.00, '5994.00', 'course', '2026-03-10 11:55:59', '2026-03-10 11:55:59');
INSERT INTO `osh_order_save` VALUES (9, 11, 252, '2026/03/10_c2ab87b6-', 'pendding', 5994.00, '5999.00', 'course', '2026-03-10 11:59:32', '2026-03-10 11:59:32');
INSERT INTO `osh_order_save` VALUES (10, 11, 252, '2026/03/10_1fe0fbac-', 'pendding', 5994.00, '5999.00', 'course', '2026-03-10 12:02:01', '2026-03-10 12:02:01');
INSERT INTO `osh_order_save` VALUES (11, 11, 252, '2026/03/10_1d6353ff-', 'pendding', 5994.00, '5999.00', 'course', '2026-03-10 12:03:25', '2026-03-10 12:03:25');
INSERT INTO `osh_order_save` VALUES (12, 11, 252, '2026/03/10_cc6d0150-', 'pendding', 5994.00, '5999.00', 'course', '2026-03-10 12:15:59', '2026-03-10 12:15:59');
INSERT INTO `osh_order_save` VALUES (13, 11, 252, '2026/03/15_54875b07-', 'pendding', 4.98, '9.98', 'course', '2026-03-15 12:40:57', '2026-03-15 12:40:57');

SET FOREIGN_KEY_CHECKS = 1;
