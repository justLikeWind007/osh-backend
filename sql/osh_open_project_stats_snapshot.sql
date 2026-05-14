-- 开源项目每日数据快照表
-- 每天定时同步后插入一条，用于计算增量排行榜
DROP TABLE IF EXISTS `osh_open_project_stats_snapshot`;
CREATE TABLE `osh_open_project_stats_snapshot` (
    `id`            bigint  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `project_id`    bigint  NOT NULL COMMENT '开源项目ID',
    `star_count`    int     NOT NULL DEFAULT 0 COMMENT '当天 Star 数',
    `fork_count`    int     NOT NULL DEFAULT 0 COMMENT '当天 Fork 数',
    `snapshot_date` date    NOT NULL COMMENT '快照日期（精确到天）',
    `create_time`   datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`     bigint   DEFAULT NULL COMMENT '创建人（用户ID）',
    `update_time`   datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`     bigint   DEFAULT NULL COMMENT '更新人（用户ID）',
    `delete_flag`   tinyint  NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_project_date` (`project_id`, `snapshot_date`),
    INDEX `idx_snapshot_date` (`snapshot_date`),
    INDEX `idx_project_id`   (`project_id`),
    INDEX `idx_delete_flag`  (`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='开源项目每日数据快照表';
