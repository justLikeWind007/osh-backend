-- ============================================
-- AI 助手反馈系统数据库初始化脚本（最终结构）
-- 执行时间: 2026-05-10
-- ============================================

USE backstage;

-- ============================================
-- 步骤 1: 创建反馈主表（最终结构）
-- ============================================
CREATE TABLE IF NOT EXISTS `assistant_feedback` (
    `id` BIGINT NOT NULL COMMENT '反馈ID',
    `user_id` BIGINT NOT NULL COMMENT '提交用户ID',
    `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
    `ticket_no` VARCHAR(32) NOT NULL COMMENT '工单编号',
    `title` VARCHAR(128) NOT NULL COMMENT '反馈标题',
    `content` VARCHAR(1000) NOT NULL COMMENT '反馈内容',
    `status` VARCHAR(32) NOT NULL DEFAULT 'submitted' COMMENT '工单状态：submitted/triaged/in_progress/resolved/closed/rejected',
    `is_pinned` TINYINT(1) DEFAULT 0 COMMENT '是否置顶（0-否 1-是）',
    `pin_order` INT DEFAULT 0 COMMENT '置顶排序（1-3，0表示不置顶）',
    `comment_count` INT DEFAULT 0 COMMENT '评论数量',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `like_count` INT DEFAULT 0 COMMENT '点赞数量',
    `favorite_count` INT DEFAULT 0 COMMENT '收藏数量',
    `result` VARCHAR(1000) DEFAULT '' COMMENT '处理结果说明',
    `page_path` VARCHAR(255) DEFAULT NULL COMMENT '反馈来源页面路径',
    `handler_id` BIGINT DEFAULT NULL COMMENT '处理人ID',
    `hot_score` INT DEFAULT 0 COMMENT '热度分',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `delete_flag` TINYINT(1) DEFAULT 0 COMMENT '删除标记（0-未删除 1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_ticket_no` (`ticket_no`),
    KEY `idx_feedback_user_time` (`user_id`, `create_time`),
    KEY `idx_feedback_status` (`status`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_is_pinned` (`is_pinned`),
    KEY `idx_hot_score_v2` (`hot_score` DESC, `create_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI助手反馈表';

-- ============================================
-- 步骤 2: 创建反馈分类表
-- ============================================
CREATE TABLE IF NOT EXISTS `assistant_feedback_category` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    `code` VARCHAR(20) NOT NULL COMMENT '分类代码',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `description` VARCHAR(255) COMMENT '分类描述',
    `drive_force` VARCHAR(100) COMMENT '驱动力',
    `expected_result` VARCHAR(100) COMMENT '期望结果',
    `tone_tendency` VARCHAR(100) COMMENT '语气倾向',
    `icon` VARCHAR(50) COMMENT '分类图标',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `is_admin_only` TINYINT(1) DEFAULT 0 COMMENT '是否仅管理员可用（0-否 1-是）',
    `allow_comment` TINYINT(1) DEFAULT 1 COMMENT '是否允许评论（0-否 1-是）',
    `is_enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用（0-否 1-是）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI助手反馈分类表';

INSERT INTO `assistant_feedback_category`
(`code`, `name`, `description`, `drive_force`, `expected_result`, `tone_tendency`, `icon`, `sort_order`, `is_admin_only`, `allow_comment`)
VALUES
('announcement', '公告', '系统公告、重要通知', '信息传达', '用户知晓', '正式、权威', 'announcement', 0, 1, 0),
('suggestion', '建议', '对产品功能、体验的改进建议', '追求更好', '方案被采纳', '建设性、客观', 'lightbulb', 1, 0, 1),
('bug', '错误', '功能异常、Bug反馈', '修复偏差', '功能恢复正常', '严肃、描述性', 'bug', 2, 0, 1),
('question', '提问', '使用疑问、功能咨询', '好奇/求知', '获得信息/答案', '探询、求知', 'question', 3, 0, 1),
('help', '求助', '遇到困难需要帮助', '陷入困局', '解决当前难题', '诚恳、迫切', 'help', 4, 0, 1),
('other', '其它', '其他类型的反馈', '自由表达', '产生连接/记录', '随意、多样化', 'note', 5, 0, 1)
ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `description` = VALUES(`description`),
    `drive_force` = VALUES(`drive_force`),
    `expected_result` = VALUES(`expected_result`),
    `tone_tendency` = VALUES(`tone_tendency`),
    `icon` = VALUES(`icon`),
    `sort_order` = VALUES(`sort_order`),
    `is_admin_only` = VALUES(`is_admin_only`),
    `allow_comment` = VALUES(`allow_comment`);

-- ============================================
-- 步骤 3: 对已有开发库补齐最终字段
-- ============================================
ALTER TABLE `assistant_feedback`
    ADD COLUMN IF NOT EXISTS `category_id` BIGINT DEFAULT NULL COMMENT '分类ID' AFTER `user_id`,
    ADD COLUMN IF NOT EXISTS `is_pinned` TINYINT(1) DEFAULT 0 COMMENT '是否置顶（0-否 1-是）' AFTER `status`,
    ADD COLUMN IF NOT EXISTS `pin_order` INT DEFAULT 0 COMMENT '置顶排序（1-3，0表示不置顶）' AFTER `is_pinned`,
    ADD COLUMN IF NOT EXISTS `comment_count` INT DEFAULT 0 COMMENT '评论数量' AFTER `pin_order`,
    ADD COLUMN IF NOT EXISTS `view_count` INT DEFAULT 0 COMMENT '浏览次数' AFTER `comment_count`,
    ADD COLUMN IF NOT EXISTS `like_count` INT DEFAULT 0 COMMENT '点赞数量' AFTER `view_count`,
    ADD COLUMN IF NOT EXISTS `favorite_count` INT DEFAULT 0 COMMENT '收藏数量' AFTER `like_count`,
    ADD COLUMN IF NOT EXISTS `handler_id` BIGINT DEFAULT NULL COMMENT '处理人ID' AFTER `page_path`,
    ADD COLUMN IF NOT EXISTS `hot_score` INT DEFAULT 0 COMMENT '热度分' AFTER `handler_id`;

ALTER TABLE `assistant_feedback`
    MODIFY COLUMN `status` VARCHAR(32) NOT NULL DEFAULT 'submitted' COMMENT '工单状态：submitted/triaged/in_progress/resolved/closed/rejected',
    MODIFY COLUMN `ticket_no` VARCHAR(32) NOT NULL COMMENT '工单编号',
    MODIFY COLUMN `result` VARCHAR(1000) DEFAULT '' COMMENT '处理结果说明',
    MODIFY COLUMN `page_path` VARCHAR(255) DEFAULT NULL COMMENT '反馈来源页面路径',
    MODIFY COLUMN `delete_flag` TINYINT(1) DEFAULT 0 COMMENT '删除标记（0-未删除 1-已删除）';

ALTER TABLE `assistant_feedback` DROP COLUMN IF EXISTS `type`;
ALTER TABLE `assistant_feedback` DROP COLUMN IF EXISTS `priority`;

SET @uk_ticket_no_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'backstage'
      AND TABLE_NAME = 'assistant_feedback'
      AND INDEX_NAME = 'uk_ticket_no'
);

SET @sql = IF(
    @uk_ticket_no_exists = 0,
    'ALTER TABLE `assistant_feedback` ADD UNIQUE KEY `uk_ticket_no` (`ticket_no`)',
    'SELECT ''uk_ticket_no 索引已存在'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx_category_id_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'backstage'
      AND TABLE_NAME = 'assistant_feedback'
      AND INDEX_NAME = 'idx_category_id'
);

SET @sql = IF(
    @idx_category_id_exists = 0,
    'ALTER TABLE `assistant_feedback` ADD INDEX `idx_category_id` (`category_id`)',
    'SELECT ''idx_category_id 索引已存在'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx_is_pinned_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'backstage'
      AND TABLE_NAME = 'assistant_feedback'
      AND INDEX_NAME = 'idx_is_pinned'
);

SET @sql = IF(
    @idx_is_pinned_exists = 0,
    'ALTER TABLE `assistant_feedback` ADD INDEX `idx_is_pinned` (`is_pinned`)',
    'SELECT ''idx_is_pinned 索引已存在'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================
-- 步骤 4: 创建评论表
-- ============================================
CREATE TABLE IF NOT EXISTS `assistant_feedback_comment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    `feedback_id` BIGINT NOT NULL COMMENT '反馈ID',
    `user_id` BIGINT NOT NULL COMMENT '评论用户ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父评论ID（0表示一级评论）',
    `root_id` BIGINT DEFAULT 0 COMMENT '根评论ID（用于二级评论）',
    `reply_to_user_id` BIGINT COMMENT '回复的用户ID',
    `reply_to_user_name` VARCHAR(50) COMMENT '回复的用户名',
    `comment_level` TINYINT DEFAULT 1 COMMENT '评论层级（1-一级评论，2-二级评论/回复）',
    `is_admin_reply` TINYINT(1) DEFAULT 0 COMMENT '是否管理员回复（0-否 1-是）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人',
    `update_by` BIGINT COMMENT '更新人',
    `delete_flag` TINYINT(1) DEFAULT 0 COMMENT '删除标记（0-未删除 1-已删除）',
    INDEX `idx_feedback_id` (`feedback_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_parent_id` (`parent_id`),
    INDEX `idx_root_id` (`root_id`),
    INDEX `idx_create_time` (`create_time`),
    INDEX `idx_delete_flag` (`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI助手反馈评论表';

-- ============================================
-- 步骤 5: 规范化历史开发数据
-- ============================================
UPDATE `assistant_feedback`
SET `status` = CASE `status`
    WHEN 'pending' THEN 'submitted'
    WHEN 'processing' THEN 'in_progress'
    WHEN 'done' THEN 'resolved'
    ELSE `status`
END;

UPDATE `assistant_feedback`
SET `ticket_no` = CONCAT('TK', DATE_FORMAT(COALESCE(`create_time`, NOW()), '%Y%m%d'), LPAD(MOD(`id`, 1000000), 6, '0'))
WHERE `ticket_no` IS NULL OR `ticket_no` = '';

UPDATE `assistant_feedback`
SET `category_id` = (
    SELECT `id`
    FROM `assistant_feedback_category`
    WHERE `code` = 'other'
)
WHERE `category_id` IS NULL;

-- ============================================
-- 步骤 6: 创建反馈标签表
-- ============================================
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

-- ============================================
-- 验证结果
-- ============================================
SELECT '✅ 反馈模块最终结构初始化完成' AS message;
SELECT CONCAT('分类表记录数: ', COUNT(*)) AS result FROM `assistant_feedback_category`;
SELECT CONCAT('反馈表记录数: ', COUNT(*)) AS result FROM `assistant_feedback`;
SELECT CONCAT('反馈标签数: ', COUNT(*)) AS result FROM `assistant_feedback_tag`;
