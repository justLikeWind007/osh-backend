-- 违规记录表
drop table if exists `osh_user_violation_record`;
create table `osh_question_answer_violation_record` (
    `id` bigint(20) not null auto_increment comment '主键id',
    `user_id` bigint(20) not null comment '违规用户id',
    `violation_type` tinyint(4) not null default 1 comment '违规类型：1=乱答，2=广告，3=恶意灌水，4=其他',
    `reason` varchar(500) default null comment '违规原因（管理员填写或系统自动判定）',
    `operator_id` bigint(20) default null comment '操作人id（管理员id，系统自动判定则为null）',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `create_by` varchar(64) not null default '' comment '创建人',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    `update_by` varchar(64) not null default '' comment '更新人',
    `delete_flag` tinyint(4) not null default 0 comment '逻辑删除：0=未删除，1=已删除',
    primary key (`id`),
    key `idx_answer` (`answer_id`),
    key `idx_operator` (`operator_id`),
    key `idx_delete_flag` (`delete_flag`)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_as_cs comment='违规记录表';