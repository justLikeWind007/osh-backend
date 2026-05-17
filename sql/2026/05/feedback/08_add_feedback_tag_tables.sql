-- ============================================
-- 反馈标签功能：标签表 + 关联表
-- 执行时间: 2026-05-12
-- ============================================

USE backstage;

-- 1. 创建反馈标签表
CREATE TABLE IF NOT EXISTS `assistant_feedback_tag` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
    `code` VARCHAR(64) NOT NULL COMMENT '标签编码',
    `name` VARCHAR(64) NOT NULL COMMENT '标签名称',
    `sort_order` INT DEFAULT 0 COMMENT '排序值',
    `use_count` INT DEFAULT 0 COMMENT '使用次数',
    `is_enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用（0-否 1-是）',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '标签备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `delete_flag` TINYINT(1) DEFAULT 0 COMMENT '删除标记（0-未删除 1-已删除）',
    UNIQUE KEY `uk_feedback_tag_code` (`code`),
    UNIQUE KEY `uk_feedback_tag_name` (`name`),
    KEY `idx_feedback_tag_enabled_sort` (`is_enabled`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI助手反馈标签表';

-- 2. 创建反馈标签关联表
CREATE TABLE IF NOT EXISTS `assistant_feedback_tag_rel` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    `feedback_id` BIGINT NOT NULL COMMENT '反馈ID',
    `tag_id` BIGINT NOT NULL COMMENT '标签ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `delete_flag` TINYINT(1) DEFAULT 0 COMMENT '删除标记（0-未删除 1-已删除）',
    UNIQUE KEY `uk_feedback_tag_rel` (`feedback_id`, `tag_id`),
    KEY `idx_feedback_tag_rel_tag_id` (`tag_id`),
    KEY `idx_feedback_tag_rel_feedback_id` (`feedback_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI助手反馈标签关联表';

-- 3. 初始化默认反馈标签
INSERT INTO `assistant_feedback_tag` (`code`, `name`, `sort_order`, `remark`)
VALUES
('course-content', '课程内容', 1, '课程内容质量、结构编排相关反馈'),
('ui-experience', '界面体验', 2, '页面布局、交互体验相关反馈'),
('course-design', '课程设计', 3, '课程模块设计、章节组织相关反馈'),
('course-player', '课程播放器', 4, '播放器与观看体验相关反馈'),
('exam-module', '考试模块', 5, '考试、测验、提交相关反馈'),
('community-module', '社区互动', 6, '评论、讨论、互动相关反馈'),
('learning-path', '学习路径', 7, '学习顺序、章节节奏、进阶路线相关反馈'),
('resource-material', '资料资源', 8, '讲义、附件、代码资料相关反馈'),
('account-login', '账号登录', 9, '登录鉴权、账号状态相关反馈'),
('performance-stability', '性能稳定', 10, '加载速度、卡顿、报错、稳定性相关反馈')
ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `sort_order` = VALUES(`sort_order`),
    `remark` = VALUES(`remark`),
    `delete_flag` = 0,
    `is_enabled` = 1;

-- 4. 验证结果
SELECT '✅ 反馈标签表初始化完成' AS message;
SELECT CONCAT('反馈标签数: ', COUNT(*)) AS result FROM `assistant_feedback_tag`;
SELECT CONCAT('反馈标签关联数: ', COUNT(*)) AS result FROM `assistant_feedback_tag_rel`;
