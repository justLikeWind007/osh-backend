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

 Date: 15/03/2026 14:25:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for osh_recommend_courses
-- ----------------------------
DROP TABLE IF EXISTS `osh_recommend_courses`;
CREATE TABLE `osh_recommend_courses`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '内容ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `cover` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '封面图片URL',
  `price` decimal(10, 2) NOT NULL COMMENT '原价',
  `t_price` decimal(10, 2) NOT NULL COMMENT '优惠价',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容类型',
  `sub_count` int UNSIGNED NOT NULL COMMENT '订阅数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_show` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否显示 1=是 0=否',
  `sort` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '排序权重',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容描述',
  `original_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '原始链接',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_sort`(`sort` ASC) USING BTREE,
  INDEX `idx_is_show`(`is_show` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '推荐列表内容表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of osh_recommend_courses
-- ----------------------------
INSERT INTO `osh_recommend_courses` VALUES (6, 'VueCli 实战在线教育后台系统', 'http://demo-mp3.oss-cn-shenzhen.aliyuncs.com/egg-edu-demo/10ccf3a973f5193bec3c.png', 9.98, 20.00, 'media', 3, '2026-03-11 09:16:55', '2026-03-11 09:16:55', 1, 0, NULL, NULL);
INSERT INTO `osh_recommend_courses` VALUES (7, 'uni-app实战视频点播app小程序', 'http://demo-mp3.oss-cn-shenzhen.aliyuncs.com/egg-edu-demo/47d1aa930177515cd95e.png', 9.98, 20.00, 'media', 0, '2026-03-11 09:16:55', '2026-03-11 09:16:55', 1, 0, NULL, NULL);
INSERT INTO `osh_recommend_courses` VALUES (11, 'uni-app实战直播app全栈开发', 'http://demo-mp3.oss-cn-shenzhen.aliyuncs.com/egg-edu-demo/c948f4a7e402473337cb.png', 90.00, 100.00, 'video', 1, '2026-03-11 09:16:55', '2026-03-11 09:16:55', 1, 0, NULL, NULL);
INSERT INTO `osh_recommend_courses` VALUES (25, 'uni-app实战在线教育类app开发', 'http://demo-mp3.oss-cn-shenzhen.aliyuncs.com/egg-edu-demo/b688b9bf339a6ece9f54.png', 10.00, 20.00, 'audio', 77, '2026-03-11 09:16:55', '2026-03-11 09:16:55', 1, 0, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
