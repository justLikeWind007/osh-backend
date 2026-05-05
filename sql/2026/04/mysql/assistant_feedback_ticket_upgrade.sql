ALTER TABLE `assistant_feedback`
  ADD COLUMN `ticket_no` varchar(32) NOT NULL DEFAULT '' COMMENT '工单编号' AFTER `user_id`,
  ADD COLUMN `priority` varchar(16) NOT NULL DEFAULT 'medium' COMMENT '优先级：low/medium/high' AFTER `type`,
  ADD COLUMN `handler_id` bigint DEFAULT NULL COMMENT '当前处理人 ID' AFTER `page_path`;

ALTER TABLE `assistant_feedback`
  MODIFY COLUMN `status` varchar(32) NOT NULL DEFAULT 'submitted' COMMENT '工单状态：submitted/triaged/in_progress/resolved/closed/rejected';

UPDATE `assistant_feedback`
SET `status` = CASE `status`
  WHEN 'pending' THEN 'submitted'
  WHEN 'processing' THEN 'in_progress'
  WHEN 'done' THEN 'resolved'
  ELSE `status`
END;

UPDATE `assistant_feedback`
SET `ticket_no` = CONCAT('TK', DATE_FORMAT(COALESCE(`create_time`, NOW()), '%Y%m%d'), LPAD(`id` % 1000000, 6, '0'))
WHERE `ticket_no` = '';

ALTER TABLE `assistant_feedback`
  ADD UNIQUE KEY `uk_ticket_no` (`ticket_no`);
