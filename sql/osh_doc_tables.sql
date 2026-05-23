-- 通用文档主表 + 通用资源关联表（支持课程小节及未来其它资源）
-- 说明：
-- 1) 仅新增表，不修改任何现有表
-- 2) 关联表使用 ref_type + ref_id 的多态设计，支持扩展到 course/exam/book/tool 等资源
-- 3) 支持“引用整篇文档”与“引用文档片段（锚点）”

create table if not exists `osh_doc` (
    `id`             bigint(20)    not null comment '文档ID（雪花算法）',
    `title`          varchar(200)  not null comment '文档标题',
    `content_format` varchar(16)   not null default 'html' comment '内容格式：html/markdown',
    `content`        longtext      not null comment '文档正文内容',
    `summary`        varchar(500)  default null comment '摘要',
    `status`         tinyint(4)    not null default 4 comment '状态：0草稿 2审核中 4已发布 6审核拒绝',
    `visibility`     tinyint(4)    not null default 1 comment '可见性：0私有 1跟随资源',
    `delete_flag`    tinyint(4)    not null default 0 comment '逻辑删除：0未删除 1已删除',
    `create_by`      varchar(64)   default null comment '创建人',
    `create_time`    datetime      not null default current_timestamp comment '创建时间',
    `update_by`      varchar(64)   default null comment '更新人',
    `update_time`    datetime      not null default current_timestamp on update current_timestamp comment '更新时间',
    primary key (`id`),
    key `idx_doc_status_delete` (`status`, `delete_flag`),
    key `idx_doc_create_by` (`create_by`),
    key `idx_doc_update_time` (`update_time`)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_as_cs comment='通用文档主表';


create table if not exists `osh_doc_ref` (
    `id`            bigint(20)    not null comment '主键ID（雪花算法）',
    `doc_id`        bigint(20)    not null comment '文档ID -> osh_doc.id',
    `ref_type`      varchar(32)   not null comment '资源类型：course_section/course/exam/book/tool/website/open_project/qa 等',
    `ref_id`        bigint(20)    not null comment '资源ID',
    `is_primary`    tinyint(4)    not null default 1 comment '是否主引用：0否 1是',
    `sort`          int(11)       not null default 0 comment '排序值（同资源下）',
    `anchor_type`   varchar(32)   not null default 'full' comment '锚点类型：full/heading/range/xpath',
    `anchor_start`  varchar(191)  not null default '' comment '锚点起始（如标题ID/字符偏移）',
    `anchor_end`    varchar(191)  not null default '' comment '锚点结束（可空）',
    `excerpt_title` varchar(200)  default null comment '片段展示标题（可空）',
    `delete_flag`   tinyint(4)    not null default 0 comment '逻辑删除：0未删除 1已删除',
    `create_by`     varchar(64)   default null comment '创建人',
    `create_time`   datetime      not null default current_timestamp comment '创建时间',
    `update_by`     varchar(64)   default null comment '更新人',
    `update_time`   datetime      not null default current_timestamp on update current_timestamp comment '更新时间',
    primary key (`id`),
    key `idx_ref_type_ref_id_delete` (`ref_type`, `ref_id`, `delete_flag`),
    key `idx_ref_doc_id_delete` (`doc_id`, `delete_flag`),
    key `idx_ref_primary` (`ref_type`, `ref_id`, `is_primary`, `delete_flag`),
    unique key `uk_doc_ref_active` (`doc_id`, `ref_type`, `ref_id`, `anchor_type`, `anchor_start`, `anchor_end`, `delete_flag`)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_as_cs comment='文档与资源通用关联表（支持片段锚点）';
