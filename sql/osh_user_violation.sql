-- 违规记录表
drop table if exists `osh_question_answer_violation_record`;
create table `osh_question_answer_violation_record` (
    `id` bigint(20) not null auto_increment comment '主键id',
    `user_id` bigint(20) not null comment '违规用户id',
    `answer_id` bigint(20) not null comment '违规回答id',
    `question_id` bigint(20) not null comment '所属问题id',
    `violation_type` tinyint(4) not null default 1 comment '违规类型：1=乱答，2=广告，3=恶意灌水，4=其他',
    `reason` varchar(500) default null comment '违规原因（管理员填写或系统自动判定）',
    `operator_id` bigint(20) default null comment '操作人id（管理员id，系统自动判定则为null）',
    `status` tinyint(4) not null default 0 comment '状态：0=有效，1=已撤销，2=已过期',
    `created_time` datetime not null default current_timestamp comment '创建时间',
    `created_by` varchar(64) not null default '' comment '创建人',
    `updated_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    `updated_by` varchar(64) not null default '' comment '更新人',
    `is_delete` tinyint(4) not null default 0 comment '逻辑删除：0=未删除，1=已删除',
    primary key (`id`),
    key `idx_user_status` (`user_id`, `status`, `created_time`),
    key `idx_answer` (`answer_id`),
    key `idx_operator` (`operator_id`),
    key `idx_is_delete` (`is_delete`)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_as_cs comment='违规记录表';

-- 用户违规统计表
drop table if exists `osh_question_answer_user_violation_stat`;
create table `osh_question_answer_user_violation_stat` (
    `user_id` bigint(20) not null comment '用户id',
    `violation_count` int(11) not null default 0 comment '当前有效违规次数',
    `is_graylisted` tinyint(4) not null default 0 comment '是否灰名单：0=否，1=是',
    `is_banned` tinyint(4) not null default 0 comment '是否封号：0=否，1=是',
    `reason` varchar(500) default null comment '违规原因（默认违规三次封号）',
    `created_time` datetime not null default current_timestamp comment '创建时间',
    `created_by` varchar(64) not null default '' comment '创建人',
    `updated_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    `updated_by` varchar(64) not null default '' comment '更新人',
    `is_delete` tinyint(4) not null default 0 comment '逻辑删除：0=未删除，1=已删除',
    primary key (`user_id`),
    key `idx_is_delete` (`is_delete`)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_as_cs comment='用户违规统计表（仅记录有违规行为的用户）';