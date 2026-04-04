-- 问题表
drop table if exists `osh_question_answer_question`;
create table `osh_question_answer_question` (
    `id` bigint(20) not null auto_increment comment '主键id',
    `user_id` bigint(20) not null comment '提问者id',
    `resource_type` tinyint(4) not null default 0 comment '资源类型：0=无，1=网站，2=课程，3=电子书，4=其他',
    `resource_no` bigint(20) default null comment '资源编号（resource_type=0时为空）',
    `content` text not null comment '问题内容',
    `is_paid_only` tinyint(4) not null default 0 comment '是否仅付费用户专属答疑：0=普通免费，1=付费专属',
    `status` tinyint(4) not null default 0 comment '状态：0=待发布，1=待回答，2=已回答',
    `view_count` int(11) not null default 0 comment '浏览量',
    `follow_count` int(11) not null default 0 comment '关注数',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `create_by` varchar(64) not null default '' comment '创建人',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    `update_by` varchar(64) not null default '' comment '更新人',
    `delete_flag` tinyint(4) not null default 0 comment '逻辑删除：0=未删除，1=已删除',
    primary key (`id`),
    key `idx_user_status` (`user_id`, `status`),
    key `idx_status_created_time` (`status`, `created_time`),
    key `idx_resource` (`resource_type`, `resource_id`),
    key `idx_is_delete` (`is_delete`)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_as_cs comment='问题表';

-- 标签表
drop table if exists `osh_question_answer_tag`;
create table `osh_question_answer_tag` (
    `id` bigint(20) not null auto_increment comment '标签id',
    `name` varchar(50) not null comment '标签名称',
    `type` varchar(20) not null comment '标签类型',
    `use_count` int(11) not null default 0 comment '使用次数',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `create_by` varchar(64) not null default '' comment '创建人',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    `update_by` varchar(64) not null default '' comment '更新人',
    `delete_flag` tinyint(4) not null default 0 comment '逻辑删除：0=未删除，1=已删除',
    primary key (`id`),
    unique key `uk_name` (`name`),
    key `idx_is_delete` (`is_delete`)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_as_cs comment='标签表';

-- 问题-标签关联表
drop table if exists `osh_question_answer_question_tag`;
create table `osh_question_answer_question_tag` (
    `id` bigint(20) not null auto_increment comment '主键id',
    `question_id` bigint(20) not null comment '问题id',
    `tag_id` bigint(20) not null comment '标签id',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `create_by` varchar(64) not null default '' comment '创建人',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    `update_by` varchar(64) not null default '' comment '更新人',
    `delete_flag` tinyint(4) not null default 0 comment '逻辑删除：0=未删除，1=已删除',
    primary key (`id`),
    unique key `uk_question_tag` (`question_id`, `tag_id`),
    key `idx_tag` (`tag_id`),
    key `idx_is_delete` (`is_delete`)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_as_cs comment='问题标签关联表';

CREATE TABLE `osh_question_answer_user_question_follow` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `question_id` BIGINT NOT NULL COMMENT '问题ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(64) NOT NULL COMMENT '创建人',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` VARCHAR(64) NOT NULL COMMENT '更新人',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标志(0-未删除,1-已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_question` (`user_id`, `question_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_question_id` (`question_id`),
    KEY `idx_is_delete` (`is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='用户关注问题表';

-- 回答表
drop table if exists `osh_question_answer_answer`;
create table `osh_question_answer_answer` (
    `id` bigint(20) not null auto_increment comment '主键id',
    `question_id` bigint(20) not null comment '所属问题id',
    `user_id` bigint(20) not null comment '回答者id',
    `content` text not null comment '回答内容',
    `vote_count` int(11) not null default 0 comment '热度（点赞数/投票数）',
    `is_solution` tinyint(4) not null default 0 comment '是否为提问者标记的已解决回答：0=否，1=是',
    `status` tinyint(4) not null default 0 comment '状态：0=正常，1=违规锁定',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `create_by` varchar(64) not null default '' comment '创建人',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    `update_by` varchar(64) not null default '' comment '更新人',
    `delete_flag` tinyint(4) not null default 0 comment '逻辑删除：0=未删除，1=已删除',
    primary key (`id`),
    key `idx_question_solution` (`question_id`, `is_solution`),
    key `idx_question_vote` (`question_id`, `vote_count`),
    key `idx_question_created_time` (`question_id`, `created_time`),
    key `idx_user` (`user_id`),
    key `idx_is_delete` (`is_delete`)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_as_cs comment='回答表';

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