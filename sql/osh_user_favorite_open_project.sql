-- 用户收藏开源项目表
DROP TABLE IF EXISTS `osh_user_favorite_open_project`;
CREATE TABLE `osh_user_favorite_open_project` (
    `id`          bigint   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     bigint   NOT NULL COMMENT '用户ID',
    `project_id`  bigint   NOT NULL COMMENT '开源项目ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    `create_by`   bigint NOT NULL DEFAULT 0 COMMENT '创建人',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`   bigint NOT NULL DEFAULT 0 COMMENT '更新人',
    `delete_flag` tinyint  NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_project` (`user_id`, `project_id`),
    INDEX `idx_user_id`    (`user_id`),
    INDEX `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='用户收藏开源项目表';
