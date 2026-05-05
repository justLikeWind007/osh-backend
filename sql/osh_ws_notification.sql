-- WebSocket 通知消息表
drop table if exists `osh_ws_notification`;
create table `osh_ws_notification` (
    `id`             bigint(20)   not null comment '主键id（雪花算法）',
    `target_user_id` bigint(20)   not null comment '接收消息的用户id',
    `type`           varchar(64)  not null comment '消息类型，由业务方定义（如 QA_NEW_ANSWER）',
    `title`          varchar(128) not null comment '通知标题',
    `content`        varchar(256) default null comment '通知内容摘要',
    `jump_url`       varchar(256) default null comment '点击跳转的前端路由，为空则不跳转',
    `biz_id`         varchar(64)  default null comment '业务id（订单id、问题id等）',
    `create_time`    datetime     not null default current_timestamp comment '创建时间',
    `create_by`      bigint(20)   not null comment '创建人',
    `update_time`    datetime     not null default current_timestamp on update current_timestamp comment '更新时间',
    `update_by`      bigint(20)   not null comment '更新人',
    `delete_flag`    tinyint(4)   not null default 0 comment '逻辑删除：0=未删除，1=已删除',
    primary key (`id`),
    key `idx_target_user`  (`target_user_id`),
    key `idx_type`         (`type`),
    key `idx_create_time`  (`create_time`),
    key `idx_delete_flag`  (`delete_flag`)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_as_cs comment='WebSocket 通知消息表';
