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

-- CREATE TABLE `osh_user_score` (
--   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
--   `user_id` bigint NOT NULL COMMENT '用户id',
--   `score_type` tinyint NOT NULL COMMENT '积分类型：1=增加，2=扣除',
--   `score_source_type` tinyint NOT NULL COMMENT '积分来源类型：1=回答问题，2=回答被采纳，3=每日签到，4=完善资料，5=违规扣分，6=管理员调整，7=其他',
--   `score_amount` int NOT NULL COMMENT '积分数量（正数表示增加，负数表示扣除，或配合score_type使用）',
--   `reason` varchar(500) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '得分/扣分原因',
--   `business_id` varchar(100) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '业务关联id（如回答id、签到记录id等，建议）',
--   `operator_id` bigint DEFAULT NULL COMMENT '操作人id（系统自动则为null，管理员手动调整时有值）',
--   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--   `create_by` varchar(64) COLLATE utf8mb4_0900_as_cs NOT NULL DEFAULT '' COMMENT '创建人',
--   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
--   `update_by` varchar(64) COLLATE utf8mb4_0900_as_cs NOT NULL DEFAULT '' COMMENT '更新人',
--   `delete_flag` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0=未删除，1=已删除',
--   PRIMARY KEY (`id`),
--   KEY `idx_user_id` (`user_id`),
--   KEY `idx_score_type` (`score_type`),
--   KEY `idx_score_source_type` (`score_source_type`),
--   KEY `idx_business_id` (`business_id`),
--   KEY `idx_delete_flag` (`delete_flag`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='用户积分记录表'

CREATE TABLE `osh_user_asset` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `gold_coin` bigint NOT NULL DEFAULT '0' COMMENT '金币数量',
  `points` bigint NOT NULL DEFAULT '0' COMMENT '积分数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` bigint NOT NULL COMMENT '更新人',
  `delete_flag` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`user_id`),
  KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='用户资产表';