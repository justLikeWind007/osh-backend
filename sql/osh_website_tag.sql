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

 Date: 31/03/2026 20:08:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for osh_website_tag
-- ----------------------------
DROP TABLE IF EXISTS `osh_website_tag`;
CREATE TABLE `osh_website_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '标签名称',
  `tag_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '标签编码',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序顺序',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tag_code`(`tag_code` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  INDEX `idx_del_flag`(`del_flag` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '实用网站标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of osh_website_tag
-- ----------------------------
INSERT INTO `osh_website_tag` VALUES (1, '前端开发', 'frontend', 0, 'admin', '2026-03-25 13:21:36', 'admin', '2026-03-25 13:21:36', 0);
INSERT INTO `osh_website_tag` VALUES (2, '后端开发', 'backend', 1, 'admin', '2026-03-25 13:21:36', 'admin', '2026-03-25 13:21:36', 0);
INSERT INTO `osh_website_tag` VALUES (3, '数据库', 'database', 2, 'admin', '2026-03-25 13:21:36', 'admin', '2026-03-25 13:21:36', 0);
INSERT INTO `osh_website_tag` VALUES (4, '云原生', 'cloud_native', 3, 'admin', '2026-03-25 13:21:36', 'admin', '2026-03-25 13:21:36', 0);
INSERT INTO `osh_website_tag` VALUES (5, '算法刷题', 'algorithm', 4, 'admin', '2026-03-25 13:21:36', 'admin', '2026-03-25 13:21:36', 0);
INSERT INTO `osh_website_tag` VALUES (6, '人工智能', 'ai', 5, 'admin', '2026-03-25 13:21:36', 'admin', '2026-03-25 13:21:36', 0);
INSERT INTO `osh_website_tag` VALUES (7, '软件测试', 'test', 6, 'admin', '2026-03-25 13:21:36', 'admin', '2026-03-25 13:21:36', 0);
INSERT INTO `osh_website_tag` VALUES (8, '运维开发', 'devops', 7, 'admin', '2026-03-25 13:21:36', 'admin', '2026-03-25 13:21:36', 0);
INSERT INTO `osh_website_tag` VALUES (9, '编程语言', 'program_lang', 8, 'admin', '2026-03-25 13:21:36', 'admin', '2026-03-25 13:21:36', 0);
INSERT INTO `osh_website_tag` VALUES (10, '开发工具', 'dev_tool', 9, 'admin', '2026-03-25 13:21:36', 'admin', '2026-03-25 13:21:36', 0);
INSERT INTO `osh_website_tag` VALUES (11, '大数据', 'big_data', 10, 'admin', '2026-03-25 13:21:36', 'admin', '2026-03-25 13:21:36', 0);
INSERT INTO `osh_website_tag` VALUES (12, '移动端开发', 'mobile', 11, 'admin', '2026-03-25 13:21:36', 'admin', '2026-03-25 13:21:36', 0);
INSERT INTO `osh_website_tag` VALUES (13, '低代码', 'low_code', 12, 'admin', '2026-03-25 13:21:36', 'admin', '2026-03-25 13:21:36', 0);
INSERT INTO `osh_website_tag` VALUES (14, '网络安全', 'security', 13, 'admin', '2026-03-25 13:21:36', 'admin', '2026-03-25 13:21:36', 0);
INSERT INTO `osh_website_tag` VALUES (15, '开源社区', 'open_source', 14, 'admin', '2026-03-25 13:21:36', 'admin', '2026-03-25 13:21:36', 0);

SET FOREIGN_KEY_CHECKS = 1;
