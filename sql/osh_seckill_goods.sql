CREATE TABLE `osh_seckill_goods` (
  `id`                BIGINT        NOT NULL AUTO_INCREMENT          COMMENT '主键ID',
  `no`                VARCHAR(16)   DEFAULT NULL                     COMMENT '资源编号',
  `goods_id`          BIGINT        DEFAULT NULL                     COMMENT '关联商品ID（课程/书籍等）',
  `goods_type`        TINYINT       DEFAULT NULL                     COMMENT '商品类型：1-课程 2-书籍 3-实物商品',
  `goods_name`        VARCHAR(128)  DEFAULT NULL                     COMMENT '商品名称快照',
  `goods_cover`       VARCHAR(512)  DEFAULT NULL                     COMMENT '商品封面快照',
  `origin_price`      DECIMAL(10,2) DEFAULT NULL                     COMMENT '商品原价快照',
  `min_seckill_price` DECIMAL(10,2) DEFAULT NULL                     COMMENT '允许的最低秒杀价',
  `status`            TINYINT       DEFAULT NULL                     COMMENT '状态：0-待审核 1-已上架 2-已下架',
  `sort`              INT           DEFAULT NULL                     COMMENT '排序权重，数值越大越靠前',
  `create_by`         VARCHAR(64)   DEFAULT NULL                     COMMENT '创建人',
  `create_time`       DATETIME      DEFAULT CURRENT_TIMESTAMP        COMMENT '创建时间',
  `update_by`         VARCHAR(64)   DEFAULT NULL                     COMMENT '更新人',
  `update_time`       DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_flag`       TINYINT       DEFAULT NULL                     COMMENT '删除标记：0-正常 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_goods` (`goods_id`, `goods_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='秒杀商品池';

INSERT INTO `osh_seckill_goods`
(`goods_id`, `goods_type`, `goods_name`, `goods_cover`, `origin_price`, `min_seckill_price`, `status`, `sort`, `create_by`, `delete_flag`)
VALUES
(101, 1, 'Java零基础入门到精通', 'https://cdn.example.com/course/java-basic.jpg', 299.00, 5.00, 1, 100, 'admin', 0),
(102, 1, 'Python数据分析实战', 'https://cdn.example.com/course/python-data.jpg', 399.00, 9.90, 1, 90, 'admin', 0),
(103, 1, 'Spring Boot微服务实战', 'https://cdn.example.com/course/springboot.jpg', 499.00, 19.90, 1, 80, 'admin', 0),
(104, 1, 'Vue3全家桶实战', 'https://cdn.example.com/course/vue3.jpg', 199.00, 5.00, 2, 70, 'admin', 0),
(105, 1, 'Go语言微服务架构实战', 'https://cdn.example.com/course/golang.jpg', 599.00, 29.90, 1, 95, 'admin', 0),
(201, 2, '深入理解计算机系统', 'https://cdn.example.com/book/csapp.jpg', 139.00, 19.90, 0, 60, 'admin', 0),
(202, 2, 'MySQL是怎样运行的', 'https://cdn.example.com/book/mysql.jpg', 109.00, 9.90, 1, 75, 'admin', 0);
