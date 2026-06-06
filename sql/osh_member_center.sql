CREATE TABLE IF NOT EXISTS `osh_member_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '套餐ID',
  `plan_code` varchar(64) NOT NULL COMMENT '套餐编码',
  `plan_name` varchar(100) NOT NULL COMMENT '套餐名称',
  `member_type` varchar(32) NOT NULL COMMENT '会员类型: vip/small_class',
  `period_type` varchar(16) NOT NULL COMMENT '周期类型: month/year',
  `duration_months` int NOT NULL COMMENT '有效期月数',
  `price` decimal(10,2) NOT NULL COMMENT '售价',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `description` varchar(500) DEFAULT NULL COMMENT '套餐说明',
  `sort` int DEFAULT 0 COMMENT '排序',
  `status` tinyint DEFAULT 1 COMMENT '状态: 1启用 0禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `delete_flag` tinyint DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_member_plan_code` (`plan_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员套餐表';

CREATE TABLE IF NOT EXISTS `osh_member_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会员订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `plan_id` bigint NOT NULL COMMENT '套餐ID',
  `order_no` varchar(64) NOT NULL COMMENT '统一订单号',
  `member_type` varchar(32) NOT NULL COMMENT '会员类型: vip/small_class',
  `plan_name_snapshot` varchar(100) NOT NULL COMMENT '套餐名称快照',
  `duration_months` int NOT NULL COMMENT '购买月数',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `pay_status` tinyint DEFAULT 0 COMMENT '支付状态: 0待支付 1已支付 2已关闭',
  `grant_status` tinyint DEFAULT 0 COMMENT '发放状态: 0待发放 1已发放 2发放失败',
  `grant_message` varchar(500) DEFAULT NULL COMMENT '发放结果信息',
  `start_time` datetime DEFAULT NULL COMMENT '本次权益开始时间',
  `expire_time` datetime DEFAULT NULL COMMENT '本次权益到期时间',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `grant_time` datetime DEFAULT NULL COMMENT '发放时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` bigint NOT NULL DEFAULT 0 COMMENT '更新人',
  `delete_flag` tinyint DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_member_order_no` (`order_no`),
  KEY `idx_member_order_user` (`user_id`, `create_time`),
  KEY `idx_member_order_plan` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员充值订单表';

INSERT INTO `osh_member_plan`
(`plan_code`, `plan_name`, `member_type`, `period_type`, `duration_months`, `price`, `original_price`, `description`, `sort`, `status`, `create_by`, `update_by`, `delete_flag`)
VALUES
('vip_month', 'VIP月卡', 'vip', 'month', 1, 29.90, 39.90, '适合短期体验，开通后享受VIP权益', 10, 1, 0, 0, 0),
('vip_year', 'VIP年卡', 'vip', 'year', 12, 199.00, 358.80, '适合长期学习，年付更划算', 20, 1, 0, 0, 0),
('small_class_year', '小班用户年卡', 'small_class', 'year', 12, 599.00, 799.00, '小班用户仅支持年付，享受小班专属权益', 30, 1, 0, 0, 0)
ON DUPLICATE KEY UPDATE
  `plan_name` = VALUES(`plan_name`),
  `member_type` = VALUES(`member_type`),
  `period_type` = VALUES(`period_type`),
  `duration_months` = VALUES(`duration_months`),
  `price` = VALUES(`price`),
  `original_price` = VALUES(`original_price`),
  `description` = VALUES(`description`),
  `sort` = VALUES(`sort`),
  `status` = VALUES(`status`),
  `update_by` = VALUES(`update_by`),
  `delete_flag` = 0;
