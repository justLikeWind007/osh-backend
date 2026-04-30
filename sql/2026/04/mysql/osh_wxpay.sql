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

 Date: 15/03/2026 14:25:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for osh_wxpay
-- ----------------------------
DROP TABLE IF EXISTS `osh_wxpay`;
CREATE TABLE `osh_wxpay`  (
  `no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `price` decimal(10, 2) NULL DEFAULT NULL,
  `code_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`no`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of osh_wxpay
-- ----------------------------
INSERT INTO `osh_wxpay` VALUES ('2021617212839_w8riwidsp', 79.00, 'weixin:asddasd');

SET FOREIGN_KEY_CHECKS = 1;
