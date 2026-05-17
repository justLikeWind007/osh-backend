-- 开源项目与本站资源关联表
-- 支持一个项目关联多个资源（课程、电子书、工具等）
DROP TABLE IF EXISTS `osh_open_project_resource_rel`;
CREATE TABLE `osh_open_project_resource_rel` (
    `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `project_id`    bigint       NOT NULL COMMENT '开源项目ID',
    `resource_type` varchar(20)  NOT NULL COMMENT '资源类型：course/book/tool',
    `resource_id`   bigint       DEFAULT NULL COMMENT '资源ID（可选，用于精确关联）',
    `resource_url`  varchar(500) NOT NULL COMMENT '资源前端路由（如 /course/1）',
    `resource_name` varchar(200) DEFAULT NULL COMMENT '资源名称（冗余，方便展示）',
    `create_time`   datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`     bigint       DEFAULT NULL COMMENT '创建人（用户ID）',
    `update_time`   datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`     bigint       DEFAULT NULL COMMENT '更新人（用户ID）',
    `delete_flag`   tinyint      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_project_id`    (`project_id`),
    INDEX `idx_resource_type` (`resource_type`),
    INDEX `idx_delete_flag`   (`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='开源项目与本站资源关联表';
