-- ============================================
-- 反馈处理闭环增强：状态流转 + 处理记录
-- 执行时间: 2026-05-10
-- ============================================

USE backstage;

-- 1. 反馈主表补充处理字段
ALTER TABLE `assistant_feedback`
    ADD COLUMN IF NOT EXISTS `handler_name` VARCHAR(64) DEFAULT NULL COMMENT '处理人名称' AFTER `handler_id`,
    ADD COLUMN IF NOT EXISTS `handled_time` DATETIME DEFAULT NULL COMMENT '最近处理时间' AFTER `handler_name`,
    ADD COLUMN IF NOT EXISTS `close_reason` VARCHAR(1000) DEFAULT NULL COMMENT '关闭原因' AFTER `handled_time`;

ALTER TABLE `assistant_feedback`
    MODIFY COLUMN `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '反馈状态：PENDING/PROCESSING/RESOLVED/CLOSED';

-- 2. 状态归一化
UPDATE `assistant_feedback`
SET `status` = CASE LOWER(`status`)
    WHEN 'submitted' THEN 'PENDING'
    WHEN 'pending' THEN 'PENDING'
    WHEN 'triaged' THEN 'PROCESSING'
    WHEN 'in_progress' THEN 'PROCESSING'
    WHEN 'processing' THEN 'PROCESSING'
    WHEN 'resolved' THEN 'RESOLVED'
    WHEN 'done' THEN 'RESOLVED'
    WHEN 'closed' THEN 'CLOSED'
    WHEN 'rejected' THEN 'CLOSED'
    ELSE `status`
END
WHERE `status` IS NOT NULL;

-- 3. 处理人名称和关闭原因回填
UPDATE `assistant_feedback` feedback
LEFT JOIN `osh_user` user_info ON feedback.`handler_id` = user_info.`id`
SET feedback.`handler_name` = COALESCE(NULLIF(user_info.`nickname`, ''), user_info.`username`, feedback.`handler_name`)
WHERE feedback.`handler_id` IS NOT NULL
  AND (feedback.`handler_name` IS NULL OR feedback.`handler_name` = '');

UPDATE `assistant_feedback`
SET `handled_time` = COALESCE(`handled_time`, `update_time`)
WHERE `status` IN ('PROCESSING', 'RESOLVED', 'CLOSED')
  AND `handled_time` IS NULL;

UPDATE `assistant_feedback`
SET `close_reason` = NULLIF(`result`, '')
WHERE `status` = 'CLOSED'
  AND (`close_reason` IS NULL OR `close_reason` = '');

-- 4. 创建处理记录表
CREATE TABLE IF NOT EXISTS `assistant_feedback_process_record` (
    `id` BIGINT NOT NULL COMMENT '记录ID',
    `feedback_id` BIGINT NOT NULL COMMENT '反馈ID',
    `from_status` VARCHAR(32) DEFAULT NULL COMMENT '变更前状态',
    `to_status` VARCHAR(32) NOT NULL COMMENT '变更后状态',
    `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(64) DEFAULT NULL COMMENT '操作人名称',
    `remark` VARCHAR(1000) DEFAULT NULL COMMENT '处理说明',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `delete_flag` TINYINT(1) DEFAULT 0 COMMENT '删除标记（0-未删除 1-已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_feedback_id` (`feedback_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反馈处理记录表';

-- 5. 为存量反馈补初始化记录
INSERT INTO `assistant_feedback_process_record`
(`id`, `feedback_id`, `from_status`, `to_status`, `operator_id`, `operator_name`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`, `delete_flag`)
SELECT
    (`feedback`.`id` * 10) + 1,
    `feedback`.`id`,
    NULL,
    'PENDING',
    `feedback`.`user_id`,
    COALESCE(NULLIF(`user_info`.`nickname`, ''), `user_info`.`username`, '匿名用户'),
    '用户提交反馈',
    COALESCE(`feedback`.`create_time`, NOW()),
    COALESCE(`feedback`.`create_time`, NOW()),
    `feedback`.`user_id`,
    `feedback`.`user_id`,
    0
FROM `assistant_feedback` feedback
LEFT JOIN `osh_user` user_info ON feedback.`user_id` = user_info.`id`
LEFT JOIN `assistant_feedback_process_record` record
    ON record.`feedback_id` = feedback.`id` AND record.`delete_flag` = 0
WHERE feedback.`delete_flag` = 0
  AND record.`id` IS NULL;

-- 6. 为已处理的存量反馈补最终处理记录
INSERT INTO `assistant_feedback_process_record`
(`id`, `feedback_id`, `from_status`, `to_status`, `operator_id`, `operator_name`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`, `delete_flag`)
SELECT
    (`feedback`.`id` * 10) + 2,
    `feedback`.`id`,
    CASE `feedback`.`status`
        WHEN 'PROCESSING' THEN 'PENDING'
        WHEN 'RESOLVED' THEN 'PROCESSING'
        WHEN 'CLOSED' THEN 'PENDING'
        ELSE NULL
    END,
    `feedback`.`status`,
    `feedback`.`handler_id`,
    COALESCE(`feedback`.`handler_name`, '管理员'),
    CASE
        WHEN `feedback`.`status` = 'CLOSED' THEN COALESCE(NULLIF(`feedback`.`close_reason`, ''), NULLIF(`feedback`.`result`, ''), '管理员关闭反馈')
        ELSE COALESCE(NULLIF(`feedback`.`result`, ''), CONCAT('状态更新为', `feedback`.`status`))
    END,
    COALESCE(`feedback`.`handled_time`, `feedback`.`update_time`, NOW()),
    COALESCE(`feedback`.`handled_time`, `feedback`.`update_time`, NOW()),
    `feedback`.`handler_id`,
    `feedback`.`handler_id`,
    0
FROM `assistant_feedback` feedback
LEFT JOIN `assistant_feedback_process_record` record
    ON record.`feedback_id` = feedback.`id`
   AND record.`to_status` = feedback.`status`
   AND record.`delete_flag` = 0
WHERE feedback.`delete_flag` = 0
  AND feedback.`status` IN ('PROCESSING', 'RESOLVED', 'CLOSED')
  AND record.`id` IS NULL;

SELECT '✅ 反馈处理闭环增强完成' AS message;
