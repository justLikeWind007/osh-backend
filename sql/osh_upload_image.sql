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

 Date: 15/03/2026 14:25:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for osh_upload_image
-- ----------------------------
DROP TABLE IF EXISTS `osh_upload_image`;
CREATE TABLE `osh_upload_image`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `school_id` int NOT NULL COMMENT '所属网校ID（关联school表）',
  `user_id` int NOT NULL COMMENT '上传用户ID（关联user表）',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原始文件名（如：avatar.jpg）',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片访问URL（接口响应的data字段）',
  `file_size` int NOT NULL COMMENT '文件大小（字节）',
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件类型（如：image/jpeg、image/png）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '图片上传记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of osh_upload_image
-- ----------------------------
INSERT INTO `osh_upload_image` VALUES (1, 1, 1, 'a.jpg', 'https://dishaxydishait.oss-cn-beijing.aliyuncs.com/upload/2026/03/12/459c2b7822ee41228235d37db961195b.jpg', 8510549, 'image/jpeg', 1, '2026-03-12 14:58:35', '2026-03-12 14:58:35');
INSERT INTO `osh_upload_image` VALUES (2, 1, 1, 'a.jpg', 'https://dishaxydishait.oss-cn-beijing.aliyuncs.com/upload/2026/03/12/e4b2c8ee152b40a48b777d720730010d.jpg', 8510549, 'image/jpeg', 1, '2026-03-12 15:18:31', '2026-03-12 15:18:31');

SET FOREIGN_KEY_CHECKS = 1;
