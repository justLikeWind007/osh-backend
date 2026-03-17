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

 Date: 15/03/2026 14:23:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for osh_group_order
-- ----------------------------
DROP TABLE IF EXISTS `osh_group_order`;
CREATE TABLE `osh_group_order`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '订单ID（主键）',
  `school_id` int NOT NULL COMMENT '所属网校ID',
  `user_id` int NOT NULL COMMENT '下单用户ID',
  `no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号（唯一）',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单状态：pendding待支付/paid已支付/cancel已取消',
  `price` decimal(10, 2) NOT NULL COMMENT '实际支付价格',
  `total_price` decimal(10, 2) NOT NULL COMMENT '商品原价',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单类型：group拼团',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of osh_group_order
-- ----------------------------
INSERT INTO `osh_group_order` VALUES (1, 1, 1, '2026/03/12_70cd3d0a-', 'pendding', 299.00, 50.00, 'group', '2026-03-12 19:25:30', '2026-03-12 19:25:30');
INSERT INTO `osh_group_order` VALUES (2, 1, 1, '2026/03/12_703a22eb-', 'pendding', 299.00, 50.00, 'group', '2026-03-12 19:28:08', '2026-03-12 19:28:08');
INSERT INTO `osh_group_order` VALUES (3, 1, 1, '2026/03/13_03713729-', 'pendding', 299.00, 50.00, 'group', '2026-03-13 14:14:10', '2026-03-13 14:14:10');
INSERT INTO `osh_group_order` VALUES (4, 1, 1, '2026/03/13_1c4607ca-', 'pendding', 299.00, 50.00, 'group', '2026-03-13 14:14:25', '2026-03-13 14:14:25');
INSERT INTO `osh_group_order` VALUES (5, 1, 1, '2026/03/13_b507ebe9-', 'pendding', 199.00, 100.00, 'group', '2026-03-13 14:14:44', '2026-03-13 14:14:44');
INSERT INTO `osh_group_order` VALUES (6, 1, 1, '20260313_ccad81f7-', 'pendding', 199.00, 100.00, 'group', '2026-03-13 14:56:01', '2026-03-13 14:56:01');

SET FOREIGN_KEY_CHECKS = 1;
