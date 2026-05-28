CREATE TABLE `osh_seckill_goods_tag` (
  `id`               BIGINT        NOT NULL AUTO_INCREMENT          COMMENT '主键ID',
  `seckill_goods_id` BIGINT        DEFAULT NULL                     COMMENT '关联秒杀商品池ID',
  `tag_name`         VARCHAR(64)   DEFAULT NULL                     COMMENT '标签名称',
  `sort_order`       INT           DEFAULT 0                        COMMENT '排序，数值越小越靠前',
  `create_by`        VARCHAR(64)   DEFAULT NULL                     COMMENT '创建人',
  `create_time`      DATETIME      DEFAULT CURRENT_TIMESTAMP        COMMENT '创建时间',
  `update_by`        VARCHAR(64)   DEFAULT NULL                     COMMENT '更新人',
  `update_time`      DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_flag`      TINYINT       DEFAULT 0                        COMMENT '删除标记：0-正常 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_seckill_goods_id` (`seckill_goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='秒杀商品标签表';
