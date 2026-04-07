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

 Date: 31/03/2026 20:06:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for osh_practical_website
-- ----------------------------
DROP TABLE IF EXISTS `osh_practical_website`;
CREATE TABLE `osh_practical_website`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网站名称',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网站链接',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网站描述',
  `logo_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网站 Logo 地址',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态 0-待审核 1-已通过 2-已拒绝',
  `click_count` int NULL DEFAULT 0 COMMENT '点击次数',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '备注',
  `del_flag` tinyint NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  `audit_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '审核人',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `reject_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '拒绝原因',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_click_count`(`click_count` ASC) USING BTREE,
  INDEX `idx_del_flag`(`del_flag` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '实用网站表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of osh_practical_website
-- ----------------------------
INSERT INTO `osh_practical_website` VALUES (1, 'GitHub', 'https://github.com', '全球最大的开源代码托管平台，IT开发者必备', 'https://github.com/images/modules/logos_page/GitHub-Mark.png', 1, 1000, 'admin', '2026-03-25 12:50:02', 'admin', '2026-03-25 12:50:02', '代码托管神器', 0, 'admin', '2026-03-25 12:50:02', NULL);
INSERT INTO `osh_practical_website` VALUES (2, '掘金', 'https://juejin.cn', '优质的IT技术社区，前端/后端/AI技术文章分享', 'https://lf-cn-tos.bytecdntp.com/cdn/expire-100-M/juejin/logo.png', 1, 865, 'admin', '2026-03-25 12:50:02', 'admin', '2026-03-25 12:50:02', '技术干货平台', 0, 'admin', '2026-03-25 12:50:02', NULL);
INSERT INTO `osh_practical_website` VALUES (3, '力扣', 'https://leetcode.cn', '程序员算法刷题、面试备战必备平台', 'https://leetcode.cn/static/images/logo-dark-cn.png', 1, 742, 'admin', '2026-03-25 12:50:02', 'admin', '2026-03-25 12:50:02', '算法面试专用', 0, 'admin', '2026-03-25 12:50:02', NULL);
INSERT INTO `osh_practical_website` VALUES (4, 'CSDN', 'https://www.csdn.net', '国内老牌IT技术社区，教程/问答/资源齐全', 'https://g.csdnimg.cn/static/logo/favicon32.ico', 1, 633, 'admin', '2026-03-25 12:50:02', 'admin', '2026-03-25 12:50:02', '综合IT社区', 0, 'admin', '2026-03-25 12:50:02', NULL);
INSERT INTO `osh_practical_website` VALUES (5, 'Stack Overflow', 'https://stackoverflow.com', '全球顶级程序员问答社区，解决编程bug', 'https://stackoverflow.design/assets/img/logos/so/logo-stackoverflow.png', 1, 811, 'admin', '2026-03-25 12:50:02', 'admin', '2026-03-25 12:50:02', '国际编程问答', 0, 'admin', '2026-03-25 12:50:02', NULL);
INSERT INTO `osh_practical_website` VALUES (6, '码云', 'https://gitee.com', '国产开源代码托管平台，国内访问速度快', 'https://gitee.com/assets/favicon.ico', 1, 520, 'admin', '2026-03-25 12:50:02', 'admin', '2026-03-25 12:50:02', '国产GitHub', 0, 'admin', '2026-03-25 12:50:02', NULL);
INSERT INTO `osh_practical_website` VALUES (7, '慕课网', 'https://www.imooc.com', 'IT职业技能在线学习平台，编程视频教程', 'https://www.imooc.com/favicon.ico', 1, 489, 'admin', '2026-03-25 12:50:02', 'admin', '2026-03-25 12:50:02', 'IT在线教育', 0, 'admin', '2026-03-25 12:50:02', NULL);
INSERT INTO `osh_practical_website` VALUES (8, 'InfoQ', 'https://www.infoq.cn', '聚焦IT技术架构、云计算、AI的技术资讯平台', 'https://www.infoq.cn/assets/img/icons/favicon.ico', 1, 396, 'admin', '2026-03-25 12:50:02', 'admin', '2026-03-25 12:50:02', '技术资讯', 0, 'admin', '2026-03-25 12:50:02', NULL);
INSERT INTO `osh_practical_website` VALUES (9, '开源中国', 'https://www.oschina.net', '国内开源技术社区，开源项目/工具分享', 'https://www.oschina.net/assets/favicon.ico', 1, 412, 'admin', '2026-03-25 12:50:02', 'admin', '2026-03-25 12:50:02', '开源社区', 0, 'admin', '2026-03-25 12:50:02', NULL);
INSERT INTO `osh_practical_website` VALUES (10, '博客园', 'https://www.cnblogs.com', '程序员技术博客平台，原创技术文章聚集地', 'https://www.cnblogs.com/favicon.ico', 1, 357, 'admin', '2026-03-25 12:50:02', 'admin', '2026-03-25 12:50:02', '技术博客', 0, 'admin', '2026-03-25 12:50:02', NULL);
INSERT INTO `osh_practical_website` VALUES (11, '牛客网', 'https://www.nowcoder.com', 'IT求职面试、笔试刷题、校招必备平台', 'https://www.nowcoder.com/favicon.ico', 1, 689, 'admin', '2026-03-25 12:50:02', 'admin', '2026-03-25 12:50:02', '求职刷题', 0, 'admin', '2026-03-25 12:50:02', NULL);
INSERT INTO `osh_practical_website` VALUES (12, '阿里云开发者社区', 'https://developer.aliyun.com', '云计算、大数据、云原生技术学习平台', 'https://img.alicdn.com/imgextra/i4/O1CN01GkG9n2Fh1LdXXXXXmFXa-200-200.png', 1, 321, 'admin', '2026-03-25 12:50:02', 'admin', '2026-03-25 12:50:02', '云技术社区', 0, 'admin', '2026-03-25 12:50:02', NULL);
INSERT INTO `osh_practical_website` VALUES (13, '腾讯云开发者社区', 'https://cloud.tencent.com/developer', '腾讯技术生态，前端/后端/云开发资源', 'https://cloud.tencent.com/favicon.ico', 1, 298, 'admin', '2026-03-25 12:50:02', 'admin', '2026-03-25 12:50:02', '腾讯云技术', 0, 'admin', '2026-03-25 12:50:02', NULL);
INSERT INTO `osh_practical_website` VALUES (14, 'B站IT技术', 'https://www.bilibili.com', '免费IT编程、架构、AI视频教程大全', 'https://static.hdslb.com/mobile/img/bilibili.png', 1, 923, 'admin', '2026-03-25 12:50:02', 'admin', '2026-03-25 12:50:02', '免费IT视频', 0, 'admin', '2026-03-25 12:50:02', NULL);
INSERT INTO `osh_practical_website` VALUES (15, 'Python官方文档', 'https://docs.python.org', 'Python编程语言官方权威文档', 'https://docs.python.org/favicon.ico', 1, 445, 'admin', '2026-03-25 12:50:02', 'admin', '2026-03-25 12:50:02', 'Python学习', 0, 'admin', '2026-03-25 12:50:02', NULL);
INSERT INTO `osh_practical_website` VALUES (16, '百度', 'https://www.baidu.com', '全球最大的中文搜索引擎', 'https://www.baidu.com/img/bd_logo.png', 0, 0, '', NULL, '', NULL, NULL, 0, NULL, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
