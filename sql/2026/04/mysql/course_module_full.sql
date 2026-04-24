/*
 * 课程模块完整 SQL 脚本
 * 创建时间：2026-03-24
 * 说明：包含课程、章节、资料、问答、标签、学习进度、服务人员、评价等 8 张表
 */

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 课程章节表
-- ----------------------------
DROP TABLE IF EXISTS `osh_course_section`;
CREATE TABLE `osh_course_section` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '章节 ID',
  `course_id` int NOT NULL COMMENT '课程 ID',
  `section_title` varchar(200) COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '章节标题',
  `section_content` text COLLATE utf8mb4_0900_as_cs COMMENT '章节内容 (视频 URL/图文内容)',
  `section_type` varchar(20) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '章节类型：video-视频，text-图文，live-直播',
  `duration` int DEFAULT '0' COMMENT '时长 (秒)',
  `sort_order` int DEFAULT '0' COMMENT '排序序号',
  `is_free` tinyint(1) DEFAULT '0' COMMENT '是否免费试听：0-否，1-是',
  `is_published` tinyint(1) DEFAULT '0' COMMENT '是否已发布：0-未发布，1-已发布',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`) USING BTREE,
  KEY `idx_sort` (`course_id`,`sort_order`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='课程章节表';

-- ----------------------------
-- 2. 课程资料表
-- ----------------------------
DROP TABLE IF EXISTS `osh_course_material`;
CREATE TABLE `osh_course_material` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '资料 ID',
  `course_id` int NOT NULL COMMENT '课程 ID',
  `material_name` varchar(200) COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '资料名称',
  `file_url` varchar(500) COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '文件下载 URL',
  `file_type` varchar(50) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '文件类型：zip,rar,7z 等压缩包',
  `file_size` bigint DEFAULT '0' COMMENT '文件大小 (字节)',
  `download_count` int DEFAULT '0' COMMENT '下载次数',
  `is_downloadable` tinyint(1) DEFAULT '0' COMMENT '是否允许下载：0-否，1-是',
  `sort_order` int DEFAULT '0' COMMENT '排序序号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='课程资料表';

-- ----------------------------
-- 3. 课程问答表
-- ----------------------------
DROP TABLE IF EXISTS `osh_course_question`;
CREATE TABLE `osh_course_question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '问题 ID',
  `course_id` int DEFAULT NULL COMMENT '课程 ID(非课程问答则为 NULL)',
  `section_id` bigint DEFAULT NULL COMMENT '章节 ID',
  `user_id` bigint NOT NULL COMMENT '提问者用户 ID',
  `question_title` varchar(200) COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '问题标题',
  `question_content` text COLLATE utf8mb4_0900_as_cs COMMENT '问题详细内容',
  `answer_content` text COLLATE utf8mb4_0900_as_cs COMMENT '回答内容',
  `answer_user_id` bigint DEFAULT NULL COMMENT '回答者用户 ID(课程服务人员)',
  `answer_time` datetime DEFAULT NULL COMMENT '回答时间',
  `status` varchar(20) COLLATE utf8mb4_0900_as_cs DEFAULT 'pending' COMMENT '状态：pending-待回答，answered-已回答，resolved-已解决',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `is_top` tinyint(1) DEFAULT '0' COMMENT '是否置顶：0-否，1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '提问时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`) USING BTREE,
  KEY `idx_section_id` (`section_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='课程问答表';

-- ----------------------------
-- 4. 课程标签表
-- ----------------------------
DROP TABLE IF EXISTS `osh_course_tag`;
CREATE TABLE `osh_course_tag` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '标签 ID',
  `tag_name` varchar(50) COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '标签名称',
  `tag_code` varchar(50) COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '标签编码：opensource-开源项目，ai-AI,enterprise-企业刚需',
  `usage_count` int DEFAULT '0' COMMENT '使用次数 (用于排序)',
  `sort_order` int DEFAULT '0' COMMENT '排序序号',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用：0-否，1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_code` (`tag_code`) USING BTREE,
  KEY `idx_usage_count` (`usage_count`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='课程标签表';

-- ----------------------------
-- 5. 课程与标签关联表
-- ----------------------------
DROP TABLE IF EXISTS `osh_course_tag_relation`;
CREATE TABLE `osh_course_tag_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` int NOT NULL COMMENT '课程 ID',
  `tag_id` int NOT NULL COMMENT '标签 ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_tag` (`course_id`,`tag_id`) USING BTREE,
  KEY `idx_tag_id` (`tag_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='课程标签关联表';

-- ----------------------------
-- 6. 用户课程学习进度表
-- ----------------------------
DROP TABLE IF EXISTS `osh_user_course_progress`;
CREATE TABLE `osh_user_course_progress` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `course_id` int NOT NULL COMMENT '课程 ID',
  `section_id` bigint DEFAULT NULL COMMENT '当前学习章节 ID',
  `learned_section_count` int DEFAULT '0' COMMENT '已学章节数',
  `total_section_count` int DEFAULT '0' COMMENT '总章节数',
  `progress_percent` decimal(5,2) DEFAULT '0.00' COMMENT '学习进度百分比',
  `last_learn_time` datetime DEFAULT NULL COMMENT '最后学习时间',
  `is_completed` tinyint(1) DEFAULT '0' COMMENT '是否已完成：0-否，1-是',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_course` (`user_id`,`course_id`) USING BTREE,
  KEY `idx_course_id` (`course_id`) USING BTREE,
  KEY `idx_progress` (`progress_percent`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='用户课程学习进度表';

-- ----------------------------
-- 7. 课程服务人员表
-- ----------------------------
DROP TABLE IF EXISTS `osh_course_staff`;
CREATE TABLE `osh_course_staff` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `course_id` int NOT NULL COMMENT '课程 ID',
  `staff_type` varchar(20) COLLATE utf8mb4_0900_as_cs DEFAULT 'assistant' COMMENT '服务类型：assistant-助教，tutor-辅导员，instructor-讲师',
  `apply_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `exam_score` int DEFAULT NULL COMMENT '考试成绩',
  `audit_status` varchar(20) COLLATE utf8mb4_0900_as_cs DEFAULT 'pending' COMMENT '审核状态：pending-待审核，approved-已通过，rejected-已拒绝',
  `audit_user_id` bigint DEFAULT NULL COMMENT '审核人用户 ID',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `audit_remark` varchar(500) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '审核备注',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否在职：0-否，1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_course_staff` (`user_id`,`course_id`) USING BTREE,
  KEY `idx_course_id` (`course_id`) USING BTREE,
  KEY `idx_audit_status` (`audit_status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='课程服务人员表';

-- ----------------------------
-- 8. 课程评价表
-- ----------------------------
DROP TABLE IF EXISTS `osh_course_review`;
CREATE TABLE `osh_course_review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价 ID',
  `course_id` int NOT NULL COMMENT '课程 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `rating` tinyint NOT NULL COMMENT '评分：1-差评，2-中评，3-好评',
  `review_content` text COLLATE utf8mb4_0900_as_cs COMMENT '评价内容',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `is_visible` tinyint(1) DEFAULT '1' COMMENT '是否可见：0-隐藏，1-显示',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_course_review` (`user_id`,`course_id`) USING BTREE,
  KEY `idx_course_id` (`course_id`) USING BTREE,
  KEY `idx_rating` (`rating`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='课程评价表';

-- ----------------------------
-- 9. 初始化标签数据
-- ----------------------------
INSERT INTO `osh_course_tag` VALUES 
(1, '开源项目', 'opensource', 0, 1, 1, NOW(), NOW()),
(2, 'AI', 'ai', 0, 2, 1, NOW(), NOW()),
(3, '企业刚需', 'enterprise', 0, 3, 1, NOW(), NOW());

SET FOREIGN_KEY_CHECKS = 1;
