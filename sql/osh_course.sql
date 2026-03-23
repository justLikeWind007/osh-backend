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

 Date: 19/03/2026 17:31:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for osh_course
-- ----------------------------
DROP TABLE IF EXISTS `osh_course`;
CREATE TABLE `osh_course` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '课程 ID',
  `title` varchar(200) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '课程标题',
  `cover` varchar(500) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '课程封面图 URL',
  `try` text COLLATE utf8mb4_0900_as_cs COMMENT '课程介绍/试看内容',
  `price` decimal(10,2) DEFAULT '0.00' COMMENT '当前价格',
  `t_price` decimal(10,2) DEFAULT '0.00' COMMENT '原价/市场价',
  `type` varchar(50) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '课程类型：media-视频课，live-直播课，text-图文课',
  `sub_count` int DEFAULT '0' COMMENT '章节数量',
  `column_id` int DEFAULT '0' COMMENT '所属专栏 ID',
  `appid` varchar(100) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '网校 appid',
  `isbuy` tinyint(1) DEFAULT '0' COMMENT '是否已购买：0-否，1-是',
  `isfava` tinyint(1) DEFAULT '0' COMMENT '是否已收藏：0-否，1-是',
  `ext1` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '备用字段 1',
  `ext2` varchar(255) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '备用字段 2',
  `ext3` text COLLATE utf8mb4_0900_as_cs COMMENT '备用字段 3',
  `ext4` text COLLATE utf8mb4_0900_as_cs COMMENT '备用字段 4',
  `ext5` int DEFAULT NULL COMMENT '备用字段 5',
  `ext6` decimal(10,2) DEFAULT NULL COMMENT '备用字段 6',
  `ext7` datetime DEFAULT NULL COMMENT '备用字段 7',
  `ext8` tinyint(1) DEFAULT NULL COMMENT '备用字段 8',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(64) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_column_id` (`column_id`) COMMENT '专栏 ID 索引',
  KEY `idx_appid` (`appid`) COMMENT '网校 appid 索引',
  KEY `idx_title` (`title`) COMMENT '课程标题索引（模糊查询）',
  KEY `idx_type` (`type`) COMMENT '课程类型索引',
  KEY `idx_price` (`price`) COMMENT '价格索引（排序查询）',
  KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引（时间排序）',
  KEY `idx_isbuy` (`isbuy`) COMMENT '购买状态索引',
  KEY `idx_column_appid` (`column_id`,`appid`) COMMENT '联合索引：专栏+appID'
) ENGINE=InnoDB AUTO_INCREMENT=932 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='课程信息表';

SET FOREIGN_KEY_CHECKS = 1;
