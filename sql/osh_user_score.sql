drop table if exists `osh_user_score_record`;
create table `osh_user_score_record` (
    `id` bigint(20) not null auto_increment comment '主键id',
    `user_id` bigint(20) not null comment '用户id',
    `score_type` tinyint(4) not null comment '积分类型：1=增加，2=扣除',
    `score_source_type` tinyint(4) not null comment '积分来源类型：1=回答问题，2=回答被采纳，3=每日签到，4=完善资料，5=违规扣分，6=管理员调整，7=其他',
    `score_amount` int(11) not null comment '积分数量（正数表示增加，负数表示扣除，或配合score_type使用）',
    `reason` varchar(500) default null comment '得分/扣分原因',
    `business_id` varchar(100) default null comment '业务关联id（如回答id、签到记录id等，建议）',
    `operator_id` bigint(20) default null comment '操作人id（系统自动则为null，管理员手动调整时有值）',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `create_by` varchar(64) not null default '' comment '创建人',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    `update_by` varchar(64) not null default '' comment '更新人',
    `delete_flag` tinyint(4) not null default 0 comment '逻辑删除：0=未删除，1=已删除',
    primary key (`id`),
    key `idx_user_id` (`user_id`),
    key `idx_score_type` (`score_type`),
    key `idx_score_source_type` (`score_source_type`),
    key `idx_business_id` (`business_id`),
    key `idx_delete_flag` (`delete_flag`)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_as_cs comment='用户积分记录表';