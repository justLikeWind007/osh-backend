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

 Date: 15/03/2026 14:24:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 课程购买记录表
-- 职责：记录用户对课程的购买/解锁状态，作为鉴权核心依据
-- 说明：
--   免费课程 → pay_method='free', pay_status='paid', order_no 可为空
--   付费课程 → 支付成功回调后写入，pay_status='paid'
--   重复购买保护：UNIQUE KEY uk_user_course
-- ----------------------------
DROP TABLE IF EXISTS `osh_learn`;
CREATE TABLE `osh_learn`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `school_id` bigint NULL DEFAULT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  `no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `price` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `total_price` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `pay_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `pay_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `updated_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '立即学习' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of osh_learn
-- ----------------------------
INSERT INTO `osh_learn` VALUES (31, 1, 1, '2021620174121_f8sms0jid', 'success', '0', '0', 'course', 'free', '2026-03-10T07:45:43.421Z', '2026-03-10T07:45:43.421Z', '2026-03-10T07:45:43.421Z');
INSERT INTO `osh_learn` VALUES (32, 1, 1, '2021620174121_f8sms0jid', 'success', '0.00', '0.00', 'course', 'free', '2026-03-10T07:54:15.700Z', '2026-03-10T07:54:15.700Z', '2026-03-10T07:54:15.700Z');
INSERT INTO `osh_learn` VALUES (33, 1, 1, '2021620174121_f8sms0jid', 'success', '0.00', '0.00', 'course', 'free', '2026-03-10T08:34:18.782Z', '2026-03-10T08:34:18.782Z', '2026-03-10T08:34:18.782Z');
INSERT INTO `osh_learn` VALUES (34, 1, 1, '2021620174121_f8sms0jid', 'success', '0.00', '0.00', 'course', 'free', '2026-03-10T08:34:20.947Z', '2026-03-10T08:34:20.947Z', '2026-03-10T08:34:20.947Z');
INSERT INTO `osh_learn` VALUES (35, 1, 1, '2021620174121_f8sms0jid', 'success', '0.00', '0.00', 'column', 'free', '2026-03-10T08:34:44.050Z', '2026-03-10T08:34:44.050Z', '2026-03-10T08:34:44.050Z');
INSERT INTO `osh_learn` VALUES (36, 1, 1, '2026/03/13_f6cc46a1-', 'success', '0.00', '0.00', 'course', 'free', '2026-03-13T11:08:51.234Z', '2026-03-13T11:08:51.234Z', '2026-03-13T11:08:51.234Z');

SET FOREIGN_KEY_CHECKS = 1;
