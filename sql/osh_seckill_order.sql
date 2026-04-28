CREATE TABLE `osh_seckill_order` (
  `id`                BIGINT        NOT NULL AUTO_INCREMENT          COMMENT '主键ID',
  `seckill_no`        VARCHAR(32)   DEFAULT NULL                     COMMENT '秒杀订单编号（格式：SK+时间戳+随机数）',
  `activity_id`       BIGINT        DEFAULT NULL                     COMMENT '关联秒杀活动ID',
  `user_id`           BIGINT        DEFAULT NULL                     COMMENT '购买用户ID',
  `goods_id`          BIGINT        DEFAULT NULL                     COMMENT '商品ID（冗余）',
  `goods_type`        TINYINT       DEFAULT NULL                     COMMENT '商品类型：1-课程 2-书籍',
  `goods_title`       VARCHAR(128)  DEFAULT NULL                     COMMENT '商品标题快照',
  `goods_cover`       VARCHAR(512)  DEFAULT NULL                     COMMENT '商品封面快照',
  `origin_price`      DECIMAL(10,2) DEFAULT NULL                     COMMENT '原价快照',
  `seckill_price`     DECIMAL(10,2) DEFAULT NULL                     COMMENT '秒杀价格快照',
  `quantity`          TINYINT       DEFAULT NULL                     COMMENT '购买数量',
  `status`            TINYINT       DEFAULT NULL                     COMMENT '订单状态：0-待支付 1-已支付 2-已取消 3-已超时 4-已退款',
  `pay_time`          DATETIME      DEFAULT NULL                     COMMENT '实际支付时间',
  `pay_expire_time`   DATETIME      DEFAULT NULL                     COMMENT '支付截止时间',
  `cancel_time`       DATETIME      DEFAULT NULL                     COMMENT '取消时间',
  `cancel_reason`     VARCHAR(128)  DEFAULT NULL                     COMMENT '取消原因：user_cancel-用户主动取消 timeout-超时取消 system-系统取消',
  `osh_order_no`      VARCHAR(32)   DEFAULT NULL                     COMMENT '关联主订单编号（支付成功后写入）',
  `create_time`       DATETIME      DEFAULT CURRENT_TIMESTAMP        COMMENT '创建时间',
  `update_time`       DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_seckill_no` (`seckill_no`),
  UNIQUE KEY `uk_activity_user` (`activity_id`, `user_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status_expire` (`status`, `pay_expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='秒杀订单表';

INSERT INTO `osh_seckill_order`
(`seckill_no`, `activity_id`, `user_id`, `goods_id`, `goods_type`, `goods_title`, `goods_cover`, `origin_price`, `seckill_price`, `quantity`, `status`, `pay_time`, `pay_expire_time`, `cancel_time`, `cancel_reason`, `osh_order_no`)
VALUES
('SK20260428100512001', 1, 1001, 101, 1, 'Java零基础入门到精通【限时秒杀】', 'https://cdn.example.com/course/java-basic.jpg', 299.00, 9.90, 1, 1, '2026-04-28 10:08:30', '2026-04-28 10:20:12', NULL, NULL, 'OSH20260428100830888'),
('SK20260428143022002', 1, 1002, 101, 1, 'Java零基础入门到精通【限时秒杀】', 'https://cdn.example.com/course/java-basic.jpg', 299.00, 9.90, 1, 0, NULL, '2026-04-28 14:45:22', NULL, NULL, NULL),
('SK20260428112015003', 1, 1003, 101, 1, 'Java零基础入门到精通【限时秒杀】', 'https://cdn.example.com/course/java-basic.jpg', 299.00, 9.90, 1, 2, NULL, '2026-04-28 11:35:15', '2026-04-28 11:25:30', 'user_cancel', NULL),
('SK20260428093045004', 1, 1004, 101, 1, 'Java零基础入门到精通【限时秒杀】', 'https://cdn.example.com/course/java-basic.jpg', 299.00, 9.90, 1, 3, NULL, '2026-04-28 09:45:45', '2026-04-28 09:46:00', 'timeout', NULL),
('SK20260425105520005', 3, 1005, 103, 1, 'Spring Boot微服务实战【已结束】', 'https://cdn.example.com/course/springboot.jpg', 499.00, 29.90, 1, 1, '2026-04-25 10:58:10', '2026-04-25 11:10:20', NULL, NULL, 'OSH20260425105810777'),
('SK20260428150030006', 1, 1006, 101, 1, 'Java零基础入门到精通【限时秒杀】', 'https://cdn.example.com/course/java-basic.jpg', 299.00, 9.90, 1, 1, '2026-04-28 15:05:45', '2026-04-28 15:15:30', NULL, NULL, 'OSH20260428150545999'),
('SK20260428083015007', 6, 1007, 105, 1, 'Go语言微服务架构实战【火爆进行中】', 'https://cdn.example.com/course/golang.jpg', 599.00, 99.90, 1, 1, '2026-04-28 08:35:20', '2026-04-28 08:50:15', NULL, NULL, 'OSH20260428083520666'),
('SK20260428140520008', 6, 1008, 105, 1, 'Go语言微服务架构实战【火爆进行中】', 'https://cdn.example.com/course/golang.jpg', 599.00, 99.90, 1, 0, NULL, '2026-04-28 14:25:20', NULL, NULL, NULL);
