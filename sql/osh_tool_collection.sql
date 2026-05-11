CREATE TABLE `osh_tool_collection` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `tool_id` bigint unsigned NOT NULL COMMENT '工具ID',
  `create_by` varchar(64) COLLATE utf8mb4_0900_as_cs NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_0900_as_cs NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_flag` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_tool` (`user_id`, `tool_id`),
  KEY `idx_tool_id` (`tool_id`, `delete_flag`),
  KEY `idx_user_id` (`user_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='工具用户收藏表';
