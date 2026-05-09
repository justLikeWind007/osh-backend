-- =====================================================
-- 用户发起拼团表 - 建表SQL
-- 创建日期: 2026-05-05
-- 说明: 用于存储用户自己发起的拼团记录（区别于加入系统活动）
-- =====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `osh_group_user_initiated`;
CREATE TABLE `osh_group_user_initiated` (
    `id`                    bigint          NOT NULL AUTO_INCREMENT              COMMENT '发起记录ID（主键）',
    `user_id`               bigint          NOT NULL                             COMMENT '发起人用户ID → osh_user.id',
    `order_id`              bigint          DEFAULT NULL                         COMMENT '关联订单ID → osh_group_order.id',
    
    -- 用户自定义规则
    `min_num`               int             NOT NULL DEFAULT 2                   COMMENT '最低成团人数',
    `max_num`               int             NOT NULL DEFAULT 5                   COMMENT '最多成团人数',
    `duration`              int             NOT NULL DEFAULT 12                  COMMENT '服务器使用时长（月）',
    `custom_price`          decimal(10,2)  NOT NULL DEFAULT 0.00                COMMENT '自定义拼团价格（总价）',
    
    -- 状态与进度
    `group_status`          tinyint         NOT NULL DEFAULT 0                   COMMENT '组团状态：0-招募中 1-已成团 2-已取消/过期',
    `current_num`           int             NOT NULL DEFAULT 1                   COMMENT '当前参团人数（含发起人自己）',
    
    -- 服务器信息（成团后分配）
    `server_ip`             varchar(50)     DEFAULT NULL                         COMMENT '服务器IP地址',
    `server_account`        varchar(100)    DEFAULT NULL                         COMMENT '服务器登录账号',
    `server_password`       varchar(255)    DEFAULT NULL                         COMMENT '服务器登录密码（AES加密存储）',
    `server_start_time`     datetime        DEFAULT NULL                         COMMENT '服务器开始使用时间',
    `server_expire_time`    datetime        DEFAULT NULL                         COMMENT '服务器到期时间',
    
    -- 时间
    `initiate_time`         datetime        DEFAULT CURRENT_TIMESTAMP            COMMENT '发起时间',
    `expire_time`           datetime        DEFAULT NULL                         COMMENT '招募截止时间',
    `create_time`           datetime        DEFAULT CURRENT_TIMESTAMP            COMMENT '创建时间',
    `update_time`           datetime        DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_flag`           tinyint(1)     DEFAULT 0                            COMMENT '逻辑删除：0-正常 1-已删除',
    
    PRIMARY KEY (`id`),
    KEY `idx_user_id`           (`user_id`),
    KEY `idx_order_id`          (`order_id`),
    KEY `idx_group_status`      (`group_status`),
    KEY `idx_initiate_time`     (`initiate_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户发起拼团记录表';

SET FOREIGN_KEY_CHECKS = 1;
