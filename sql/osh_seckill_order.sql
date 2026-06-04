CREATE TABLE `osh_seckill_order` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT          COMMENT '主键ID',
  `seckill_no`      VARCHAR(64)   NOT NULL                         COMMENT '秒杀尝试号（Lua成功后生成，唯一标识一次有效秒杀尝试）',
  `order_no`        VARCHAR(32)   NOT NULL                         COMMENT '统一订单号（checkout成功后生成，对接支付系统）',
  `activity_id`     BIGINT        DEFAULT NULL                     COMMENT '关联秒杀活动ID',
  `item_id`         BIGINT        DEFAULT NULL                     COMMENT '关联秒杀活动商品明细ID',
  `user_id`         BIGINT        DEFAULT NULL                     COMMENT '购买用户ID',
  `goods_id`        BIGINT        DEFAULT NULL                     COMMENT '商品ID（冗余）',
  `goods_type`      TINYINT       DEFAULT NULL                     COMMENT '商品类型：1-课程 2-书籍',
  `goods_title`     VARCHAR(128)  DEFAULT NULL                     COMMENT '商品标题快照',
  `goods_cover`     VARCHAR(512)  DEFAULT NULL                     COMMENT '商品封面快照',
  `origin_price`    DECIMAL(10,2) DEFAULT NULL                     COMMENT '原价快照',
  `seckill_price`   DECIMAL(10,2) DEFAULT NULL                     COMMENT '秒杀单价快照',
  `total_amount`    DECIMAL(10,2) DEFAULT NULL                     COMMENT '实付总金额（seckill_price × quantity）',
  `quantity`        TINYINT       DEFAULT NULL                     COMMENT '购买数量',
  `status`          TINYINT       DEFAULT NULL                     COMMENT '订单状态：0-待支付 1-已支付 2-已取消 3-已超时',
  `pay_time`        DATETIME      DEFAULT NULL                     COMMENT '实际支付时间',
  `pay_expire_time` DATETIME      DEFAULT NULL                     COMMENT '支付截止时间',
  `cancel_time`     DATETIME      DEFAULT NULL                     COMMENT '取消时间',
  `cancel_reason`   VARCHAR(128)  DEFAULT NULL                     COMMENT '取消原因：user_cancel-用户主动取消 pay_timeout-超时取消 system-系统取消',
  `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP        COMMENT '创建时间',
  `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_flag`     TINYINT       DEFAULT 0                        COMMENT '删除标记：0-正常 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_seckill_no` (`seckill_no`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_item_user_status` (`item_id`, `user_id`, `status`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status_expire` (`status`, `pay_expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='秒杀订单表（seckill_no=秒杀尝试号，order_no=统一订单号，支持同一用户多单累计购买）';

-- 示例数据：seckill_no 为秒杀尝试号（SK前缀），order_no 为统一订单号（OSH前缀）
INSERT INTO `osh_seckill_order`
(`seckill_no`, `order_no`, `activity_id`, `item_id`, `user_id`, `goods_id`, `goods_type`, `goods_title`, `goods_cover`, `origin_price`, `seckill_price`, `quantity`, `status`, `pay_time`, `pay_expire_time`, `cancel_time`, `cancel_reason`)
VALUES
('SK20260428100512001', 'OSH20260428100830888', 1, 1, 1001, 101, 1, 'Java零基础入门到精通', 'https://cdn.example.com/course/java-basic.jpg', 299.00, 9.90,  1, 1, '2026-04-28 10:08:30', '2026-04-28 10:20:12', NULL,                 NULL         ),
('SK20260428143022002', 'OSH20260428143100001', 1, 1, 1002, 101, 1, 'Java零基础入门到精通', 'https://cdn.example.com/course/java-basic.jpg', 299.00, 9.90,  1, 0, NULL,                  '2026-04-28 14:45:22', NULL,                 NULL         ),
('SK20260428112015003', 'OSH20260428112100001', 1, 1, 1003, 101, 1, 'Java零基础入门到精通', 'https://cdn.example.com/course/java-basic.jpg', 299.00, 9.90,  1, 2, NULL,                  '2026-04-28 11:35:15', '2026-04-28 11:25:30','user_cancel' ),
('SK20260428093045004', 'OSH20260428093100001', 1, 1, 1004, 101, 1, 'Java零基础入门到精通', 'https://cdn.example.com/course/java-basic.jpg', 299.00, 9.90,  1, 3, NULL,                  '2026-04-28 09:45:45', '2026-04-28 09:46:00','pay_timeout' ),
('SK20260425105520005', 'OSH20260425105810777', 3, 3, 1005, 103, 1, 'Spring Boot微服务实战','https://cdn.example.com/course/springboot.jpg', 499.00, 29.90, 1, 1, '2026-04-25 10:58:10', '2026-04-25 11:10:20', NULL,                 NULL         ),
('SK20260428150030006', 'OSH20260428150545999', 1, 1, 1006, 101, 1, 'Java零基础入门到精通', 'https://cdn.example.com/course/java-basic.jpg', 299.00, 9.90,  1, 1, '2026-04-28 15:05:45', '2026-04-28 15:15:30', NULL,                 NULL         ),
('SK20260428083015007', 'OSH20260428083520666', 6, 6, 1007, 105, 1, 'Go语言微服务架构实战', 'https://cdn.example.com/course/golang.jpg',     599.00, 99.90, 1, 1, '2026-04-28 08:35:20', '2026-04-28 08:50:15', NULL,                 NULL         ),
('SK20260428140520008', 'OSH20260428140600001', 6, 6, 1008, 105, 1, 'Go语言微服务架构实战', 'https://cdn.example.com/course/golang.jpg',     599.00, 99.90, 1, 0, NULL,                  '2026-04-28 14:25:20', NULL,                 NULL         );
