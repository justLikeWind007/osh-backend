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

 Date: 19/03/2026 17:31:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for osh_column
-- ----------------------------
DROP TABLE IF EXISTS `osh_column`;
CREATE TABLE `osh_column` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '专栏ID',
  `title` varchar(200) COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '专栏标题',
  `cover` varchar(500) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '封面图',
  `col_try` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '试看描述/摘要',
  `content` text COLLATE utf8mb4_0900_as_cs COMMENT '专栏介绍详情',
  `price` decimal(10,2) DEFAULT '0.00' COMMENT '原价',
  `t_price` decimal(10,2) DEFAULT '0.00' COMMENT '划线价',
  `isend` tinyint(1) DEFAULT '0' COMMENT '是否完结 (0否 1是)',
  `sub_count` int DEFAULT '0' COMMENT '订阅人数',
  `buy_flag` tinyint(1) DEFAULT '0' COMMENT '是否购买标记',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `type` varchar(64) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='专栏表';

SET FOREIGN_KEY_CHECKS = 1;
