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

 Date: 15/03/2026 14:24:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for osh_order_list
-- ----------------------------
DROP TABLE IF EXISTS `osh_order_list`;
CREATE TABLE `osh_order_list`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `price` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `total_price` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `goods` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5017 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '我的订单列表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of osh_order_list
-- ----------------------------
INSERT INTO `osh_order_list` VALUES (2, '20191211194940843468', '10.00', 'pendding', '20.00', '2021-04-27 16:00:00', '[专栏]uni-app实战仿微信app开发', NULL);
INSERT INTO `osh_order_list` VALUES (7, '20191211194940843468', '10.00', 'pendding', '20.00', '2021-04-27 16:00:00', '[专栏]uni-app实战仿微信app开发', NULL);
INSERT INTO `osh_order_list` VALUES (4204, '2024419185734apoplgxf0', '50.00', 'closed', '9.98', '2024-04-19 18:57:34', '[专栏]uni-app实战仿微信app开发', 'group');
INSERT INTO `osh_order_list` VALUES (4679, '202411915176l5res4lvb', '0.20', 'closed', '0.20', '2024-11-09 15:17:06', '[图文]Vue3实战商城后台管理系统开发', 'default');
INSERT INTO `osh_order_list` VALUES (4692, '20241126104056dbitiwwwg', '0.10', 'closed', '10.00', '2024-11-26 10:40:56', '[音频]uni-app实战在线教育类app开发', 'group');
INSERT INTO `osh_order_list` VALUES (4695, '20241126194049y98ih4ku8', '90.00', 'closed', '90.00', '2024-11-26 19:40:49', '[图文]egg.js图文入门1', 'default');
INSERT INTO `osh_order_list` VALUES (4697, '20241127115047ytt5tmcpc', '9.98', 'closed', '9.98', '2024-11-27 11:50:47', '[图文]uni-app实战视频点播app小程序', 'default');
INSERT INTO `osh_order_list` VALUES (4698, '20241127131347zz2yey8ox', '90.00', 'closed', '90.00', '2024-11-27 13:13:47', '[图文]egg.js图文入门1', 'default');
INSERT INTO `osh_order_list` VALUES (4699, '202411271316174su3f7mi1', '90.00', 'closed', '90.00', '2024-11-27 13:16:17', '[图文]egg.js图文入门1', 'default');
INSERT INTO `osh_order_list` VALUES (4871, '2025551816suvz3mn8p', '0.10', 'closed', '10.00', '2025-05-05 18:01:06', '[音频]uni-app实战在线教育类app开发', 'group');
INSERT INTO `osh_order_list` VALUES (4872, '20255518445vwdrvnyim', '50.00', 'closed', '9.98', '2025-05-05 18:04:45', '[专栏]uni-app实战仿微信app开发', 'group');
INSERT INTO `osh_order_list` VALUES (5016, '20263102337268f8079z8a', '0.20', 'pendding', '0.20', '2026-03-10 23:37:26', '[图文]Vue3实战商城后台管理系统开发', 'default');

SET FOREIGN_KEY_CHECKS = 1;
