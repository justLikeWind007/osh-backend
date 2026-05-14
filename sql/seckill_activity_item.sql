CREATE TABLE `osh_seckill_activity_item` (
  `id`               BIGINT        NOT NULL AUTO_INCREMENT          COMMENT '主键ID',
  `activity_id`      BIGINT        DEFAULT NULL                     COMMENT '关联秒杀活动ID',
  `seckill_goods_id` BIGINT        DEFAULT NULL                     COMMENT '关联秒杀商品池ID',
  `goods_id`         BIGINT        DEFAULT NULL                     COMMENT '关联商品ID（冗余快照）',
  `goods_type`       TINYINT       DEFAULT NULL                     COMMENT '商品类型：1-课程 2-书籍 3-实物商品',
  `title`            VARCHAR(200)  DEFAULT NULL                     COMMENT '商品标题快照',
  `cover`            VARCHAR(500)  DEFAULT NULL                     COMMENT '商品封面图快照',
  `origin_price`     DECIMAL(10,2) DEFAULT NULL                     COMMENT '商品原价快照',
  `seckill_price`    DECIMAL(10,2) DEFAULT NULL                     COMMENT '该商品在本次活动的秒杀价格',
  `total_stock`      INT           DEFAULT NULL                     COMMENT '该商品在本次活动的总库存',
  `available_stock`  INT           DEFAULT NULL                     COMMENT '剩余可用库存',
  `sold_count`       INT           DEFAULT NULL                     COMMENT '已售数量',
  `limit_per_user`   INT           DEFAULT NULL                     COMMENT '每人限购数量',
  `sort`             INT           DEFAULT NULL                     COMMENT '在活动内的展示排序，数值越小越靠前',
  `create_by`        BIGINT        DEFAULT NULL                     COMMENT '创建人ID',
  `update_by`        BIGINT        DEFAULT NULL                     COMMENT '更新人ID',
  `create_time`      DATETIME      DEFAULT CURRENT_TIMESTAMP        COMMENT '创建时间',
  `update_time`      DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_flag`      TINYINT       DEFAULT NULL                     COMMENT '删除标记：0-正常 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_seckill_goods_id` (`seckill_goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='秒杀活动商品明细表';

-- 对应活动1（Java零基础）的商品明细
INSERT INTO `osh_seckill_activity_item`
(`activity_id`, `seckill_goods_id`, `goods_id`, `goods_type`, `title`, `cover`, `origin_price`, `seckill_price`, `total_stock`, `available_stock`, `sold_count`, `limit_per_user`, `sort`, `delete_flag`)
VALUES
(1, 1, 101, 1, 'Java零基础入门到精通', 'https://cdn.example.com/course/java-basic.jpg', 299.00, 9.90, 100, 73, 27, 1, 0, 0),
(2, 2, 102, 1, 'Python数据分析实战',   'https://cdn.example.com/course/python-data.jpg', 399.00, 19.90, 200, 200, 0, 1, 0, 0),
(3, 3, 103, 1, 'Spring Boot微服务实战','https://cdn.example.com/course/springboot.jpg',  499.00, 29.90, 50,  0,   50, 1, 0, 0),
(4, 4, 201, 2, '深入理解计算机系统',   'https://cdn.example.com/book/csapp.jpg',          139.00, 49.90, 300, 300, 0,  2, 0, 0),
(5, 5, 104, 1, 'Vue3全家桶实战',       'https://cdn.example.com/course/vue3.jpg',         199.00, 9.90,  80,  45,  35, 1, 0, 0),
(6, 6, 105, 1, 'Go语言微服务架构实战', 'https://cdn.example.com/course/golang.jpg',        599.00, 99.90, 50,  28,  22, 1, 0, 0);
