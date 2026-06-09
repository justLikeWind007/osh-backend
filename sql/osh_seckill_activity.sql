CREATE TABLE `osh_seckill_activity` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT          COMMENT '主键ID',
  `title`           VARCHAR(128)  DEFAULT NULL                     COMMENT '活动标题',
  `start_time`      DATETIME      DEFAULT NULL                     COMMENT '活动开始时间',
  `end_time`        DATETIME      DEFAULT NULL                     COMMENT '活动结束时间',
  `status`          TINYINT       DEFAULT NULL                     COMMENT '活动状态：0-草稿 1-未开始 2-进行中 3-已结束 4-已下架',
  `pay_timeout_min` SMALLINT      DEFAULT NULL                     COMMENT '支付超时时间（分钟），活动内所有商品共用',
  `create_by`       VARCHAR(64)   DEFAULT NULL                     COMMENT '创建人',
  `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP        COMMENT '创建时间',
  `update_by`       VARCHAR(64)   DEFAULT NULL                     COMMENT '更新人',
  `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_flag`     TINYINT       DEFAULT NULL                     COMMENT '删除标记：0-正常 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_status_time` (`status`, `start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='秒杀活动表';

INSERT INTO `osh_seckill_activity`
(`title`, `start_time`, `end_time`, `status`, `pay_timeout_min`, `create_by`, `delete_flag`)
VALUES
('Java零基础入门到精通秒杀活动',  '2026-04-28 10:00:00', '2026-04-30 23:59:59', 2, 15, 'admin', 0),
('Python数据分析实战秒杀活动',    '2026-04-29 20:00:00', '2026-04-29 22:00:00', 1, 15, 'admin', 0),
('Spring Boot微服务实战秒杀活动', '2026-04-25 10:00:00', '2026-04-27 23:59:59', 3, 15, 'admin', 0),
('深入理解计算机系统秒杀活动',    '2026-05-01 00:00:00', '2026-05-03 23:59:59', 0, 30, 'admin', 0),
('Vue3全家桶实战秒杀活动',        '2026-04-20 10:00:00', '2026-04-28 23:59:59', 4, 15, 'admin', 0),
('Go语言微服务架构实战秒杀活动',  '2026-04-28 08:00:00', '2026-04-29 23:59:59', 2, 20, 'admin', 0);
