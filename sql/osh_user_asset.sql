CREATE TABLE `osh_user_violation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint NOT NULL COMMENT '违规用户id',
  `violation_type` tinyint NOT NULL DEFAULT '1' COMMENT '违规类型：1=乱答，2=广告，3=恶意灌水，4=其他',
  `reason` varchar(500) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '违规原因（管理员填写或系统自动判定）',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人id（管理员id，系统自动判定则为null）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8mb4_0900_as_cs NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) COLLATE utf8mb4_0900_as_cs NOT NULL DEFAULT '' COMMENT '更新人',
  `delete_flag` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0=未删除，1=已删除',
  PRIMARY KEY (`id`),
  KEY `idx_operator` (`operator_id`),
  KEY `idx_delete_flag` (`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='违规记录表'

CREATE TABLE `osh_user_asset_record` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `change_type` tinyint NOT NULL COMMENT '变动类型：0-收入，1-支出',
    `change_source` tinyint NOT NULL COMMENT '变动来源：0-签到，1-观看视频，2-分享，3-购买商品，4-提现，5-管理员调整等',
    `change_amount` bigint NOT NULL COMMENT '变动数量（正数）',
    `before_balance` bigint NOT NULL COMMENT '变动前余额',
    `after_balance` bigint NOT NULL COMMENT '变动后余额',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注说明',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` bigint NOT NULL COMMENT '创建人',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` bigint NOT NULL COMMENT '更新人',
    `delete_flag` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引',
    KEY `idx_business_id` (`business_id`) COMMENT '业务ID索引',
    KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='用户资产变动记录表';

CREATE TABLE `osh_user_asset` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `points` bigint NOT NULL DEFAULT '0' COMMENT '积分数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` bigint NOT NULL COMMENT '更新人',
  `delete_flag` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`user_id`),
  KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='用户资产表';