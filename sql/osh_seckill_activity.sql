CREATE TABLE `osh_seckill_activity` (
  `id`                BIGINT        NOT NULL AUTO_INCREMENT          COMMENT '主键ID',
  `goods_id`          BIGINT        DEFAULT NULL                     COMMENT '关联商品ID（对应 osh_course.id）',
  `goods_type`        TINYINT       DEFAULT NULL                     COMMENT '商品类型：1-课程 2-书籍',
  `title`             VARCHAR(128)  DEFAULT NULL                     COMMENT '活动标题（冗余快照）',
  `cover`             VARCHAR(512)  DEFAULT NULL                     COMMENT '商品封面图（冗余快照）',
  `origin_price`      DECIMAL(10,2) DEFAULT NULL                     COMMENT '商品原价快照',
  `seckill_price`     DECIMAL(10,2) DEFAULT NULL                     COMMENT '秒杀价格',
  `total_stock`       INT           DEFAULT NULL                     COMMENT '活动总库存',
  `available_stock`   INT           DEFAULT NULL                     COMMENT '剩余可用库存',
  `sold_count`        INT           DEFAULT NULL                     COMMENT '已售数量',
  `limit_per_user`    TINYINT       DEFAULT NULL                     COMMENT '每人限购数量',
  `start_time`        DATETIME      DEFAULT NULL                     COMMENT '活动开始时间',
  `end_time`          DATETIME      DEFAULT NULL                     COMMENT '活动结束时间',
  `status`            TINYINT       DEFAULT NULL                     COMMENT '活动状态：0-草稿 1-未开始 2-进行中 3-已结束 4-已下架',
  `pay_timeout_min`   SMALLINT      DEFAULT NULL                     COMMENT '支付超时时间（分钟）',
  `create_by`         VARCHAR(64)   DEFAULT NULL                     COMMENT '创建人',
  `create_time`       DATETIME      DEFAULT CURRENT_TIMESTAMP        COMMENT '创建时间',
  `update_by`         VARCHAR(64)   DEFAULT NULL                     COMMENT '更新人',
  `update_time`       DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_flag`       TINYINT       DEFAULT NULL                     COMMENT '删除标记：0-正常 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_goods_id` (`goods_id`),
  KEY `idx_status_time` (`status`, `start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='秒杀活动表';

INSERT INTO `osh_seckill_activity`
(`goods_id`, `goods_type`, `title`, `cover`, `origin_price`, `seckill_price`, `total_stock`, `available_stock`, `sold_count`, `limit_per_user`, `start_time`, `end_time`, `status`, `pay_timeout_min`, `create_by`, `delete_flag`)
VALUES
(101, 1, 'Java零基础入门到精通【限时秒杀】', 'https://cdn.example.com/course/java-basic.jpg', 299.00, 9.90, 100, 73, 27, 1, '2026-04-28 10:00:00', '2026-04-30 23:59:59', 2, 15, 'admin', 0),
(102, 1, 'Python数据分析实战【明日开抢】', 'https://cdn.example.com/course/python-data.jpg', 399.00, 19.90, 200, 200, 0, 1, '2026-04-29 20:00:00', '2026-04-29 22:00:00', 1, 15, 'admin', 0),
(103, 1, 'Spring Boot微服务实战【已结束】', 'https://cdn.example.com/course/springboot.jpg', 499.00, 29.90, 50, 0, 50, 1, '2026-04-25 10:00:00', '2026-04-27 23:59:59', 3, 15, 'admin', 0),
(201, 2, '深入理解计算机系统【待发布】', 'https://cdn.example.com/book/csapp.jpg', 139.00, 49.90, 300, 300, 0, 2, '2026-05-01 00:00:00', '2026-05-03 23:59:59', 0, 30, 'admin', 0),
(104, 1, 'Vue3全家桶实战【已下架】', 'https://cdn.example.com/course/vue3.jpg', 199.00, 9.90, 80, 45, 35, 1, '2026-04-20 10:00:00', '2026-04-28 23:59:59', 4, 15, 'admin', 0),
(105, 1, 'Go语言微服务架构实战【火爆进行中】', 'https://cdn.example.com/course/golang.jpg', 599.00, 99.90, 50, 28, 22, 1, '2026-04-28 08:00:00', '2026-04-29 23:59:59', 2, 20, 'admin', 0);
