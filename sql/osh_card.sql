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

 Date: 15/03/2026 14:22:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for osh_card
-- ----------------------------
DROP TABLE IF EXISTS `osh_card`;
CREATE TABLE `osh_card`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '标题',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '类型（course 课程/other 其他）',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `goods_id` bigint NULL DEFAULT NULL COMMENT '商品 ID',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `used` tinyint(1) NULL DEFAULT 0 COMMENT '是否使用（0 未使用 1 已使用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2118 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '卡券信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of osh_card
-- ----------------------------
INSERT INTO `osh_card` VALUES (1, 'uni-app实战直播app全栈开发', 'course', 5.00, 1, '2021-06-29 01:58:01', '2035-07-29 00:00:00', 1, 'admin', '2026-03-05 14:31:52', '', NULL, '');
INSERT INTO `osh_card` VALUES (2, 'Spring Boot 企业级开发实战', 'course', 10.00, 2, '2021-07-01 00:00:00', '2035-12-31 00:00:00', 0, 'admin', '2026-03-05 14:31:52', '', NULL, '');
INSERT INTO `osh_card` VALUES (3, 'React 从入门到精通', 'course', 8.00, 3, '2021-08-01 00:00:00', '2035-12-31 00:00:00', 0, 'admin', '2026-03-05 14:31:52', '', NULL, '');
INSERT INTO `osh_card` VALUES (4, 'Node.js 全栈开发指南', 'course', 12.00, 4, '2021-09-01 00:00:00', '2035-12-31 00:00:00', 0, 'admin', '2026-03-05 14:31:52', '', NULL, '');
INSERT INTO `osh_card` VALUES (5, '通用满减券', 'other', 20.00, 5, '2021-10-01 00:00:00', '2035-12-31 00:00:00', 0, 'admin', '2026-03-05 14:31:52', '', NULL, '');
INSERT INTO `osh_card` VALUES (6, 'JavaScript 高级程序设计', 'course', 15.00, 6, '2021-05-01 00:00:00', '2035-12-31 00:00:00', 0, 'admin', '2026-03-05 14:31:52', '', NULL, '');
INSERT INTO `osh_card` VALUES (7, 'CSS 权威指南', 'course', 9.00, 7, '2021-06-01 00:00:00', '2035-12-31 00:00:00', 0, 'admin', '2026-03-05 14:31:52', '', NULL, '');
INSERT INTO `osh_card` VALUES (8, 'HTML5 与 CSS3 实战', 'course', 11.00, 8, '2021-07-01 00:00:00', '2035-12-31 00:00:00', 0, 'admin', '2026-03-05 14:31:52', '', NULL, '');
INSERT INTO `osh_card` VALUES (9, 'TypeScript 从入门到精通', 'course', 13.00, 9, '2021-08-01 00:00:00', '2035-12-31 00:00:00', 0, 'admin', '2026-03-05 14:31:52', '', NULL, '');
INSERT INTO `osh_card` VALUES (10, '前端性能优化指南', 'column', 8.00, 10, '2021-09-01 00:00:00', '2035-12-31 00:00:00', 0, 'admin', '2026-03-05 14:31:52', '', NULL, '');
INSERT INTO `osh_card` VALUES (11, 'Webpack 构建优化', 'course', 10.00, 11, '2021-10-01 00:00:00', '2035-12-31 00:00:00', 0, 'admin', '2026-03-05 14:31:52', '', NULL, '');
INSERT INTO `osh_card` VALUES (12, 'Vite 快速上手', 'course', 7.00, 12, '2021-11-01 00:00:00', '2035-12-31 00:00:00', 0, 'admin', '2026-03-05 14:31:52', '', NULL, '');
INSERT INTO `osh_card` VALUES (13, 'Vue3 源码解析', 'course', 18.00, 13, '2021-12-01 00:00:00', '2035-12-31 00:00:00', 0, 'admin', '2026-03-05 14:31:52', '', NULL, '');
INSERT INTO `osh_card` VALUES (14, 'React Hooks 实战', 'course', 14.00, 14, '2022-01-01 00:00:00', '2035-12-31 00:00:00', 0, 'admin', '2026-03-05 14:31:52', '', NULL, '');
INSERT INTO `osh_card` VALUES (15, '小程序开发指南', 'column', 12.00, 15, '2022-02-01 00:00:00', '2035-12-31 00:00:00', 0, 'admin', '2026-03-05 14:31:52', '', NULL, '');
INSERT INTO `osh_card` VALUES (866, '中级经济法-知识点精讲课', 'course', 4.00, 1636, '2022-07-15 00:00:00', '2025-08-23 00:00:00', 0, '', NULL, '', NULL, NULL);
INSERT INTO `osh_card` VALUES (2114, 'unicloud商城全栈开发1', 'course', 5.00, 12, '2021-05-16 01:02:00', '2032-06-30 00:00:00', 0, '', NULL, '', NULL, NULL);
INSERT INTO `osh_card` VALUES (2115, 'VueCli 实战商城后台管理系统', 'column', 8.90, 184, '2021-09-10 00:00:00', '2034-10-17 00:00:00', 0, '', NULL, '', NULL, NULL);
INSERT INTO `osh_card` VALUES (2116, 'VueCli 实战在线教育后台系统', 'course', 5.00, 6, '2021-06-29 01:58:01', '2035-07-29 00:00:00', 0, '', NULL, '', NULL, NULL);
INSERT INTO `osh_card` VALUES (2117, 'uni-app实战直播app全栈开发', 'course', 50.00, 11, '2021-05-16 01:03:00', '2032-06-30 00:00:00', 0, '', NULL, '', NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
