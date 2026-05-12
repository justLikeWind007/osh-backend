-- =====================================================
-- 工具点赞差评记录表
-- =====================================================
create table if not exists `osh_tool_vote` (
    `id` bigint not null auto_increment comment '主键ID',
    `user_id` bigint not null comment '用户ID',
    `tool_id` bigint not null comment '工具ID',
    `type` tinyint not null comment '评价类型：1-点赞，3-差评',
    `create_by` varchar(64) character set utf8mb4 collate utf8mb4_0900_as_cs default null comment '创建人',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_by` varchar(64) character set utf8mb4 collate utf8mb4_0900_as_cs default null comment '更新人',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    `delete_flag` tinyint not null default 0 comment '删除标识：0-正常，1-删除',
    primary key (`id`),
    unique key `uk_user_tool` (`user_id`, `tool_id`),
    key `idx_tool_type` (`tool_id`, `type`, `delete_flag`)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_as_cs comment='工具点赞差评记录表';

-- =====================================================
-- 工具模块权限数据初始化
-- 说明：
--   type=1 菜单权限，type=2 按钮/API权限
--   parent_id 指向 permission_code='tool' 的菜单节点
-- =====================================================

-- 工具模块父菜单（若不存在则插入）
insert into `osh_permission` (
    `permission_name`, `permission_code`, `description`, `parent_id`, `type`, `url`, `path`, `component`, `sort_order`,
    `create_time`, `create_by`, `update_time`, `update_by`, `delete_flag`
)
select '工具管理', 'tool', '工具模块菜单',
       0, 1, null, '/tool/osh', 'tool/list/index', 20, now(), 'system', now(), 'system', 0
where not exists (select 1 from `osh_permission` p where p.permission_code = 'tool' and p.delete_flag = 0);

-- 新增工具
insert into `osh_permission` (
    `permission_name`, `permission_code`, `description`, `parent_id`, `type`, `url`, `path`, `component`, `sort_order`,
    `create_time`, `create_by`, `update_time`, `update_by`, `delete_flag`
)
select '新增工具', 'tool:create', '新增工具',
       coalesce((select p.id from `osh_permission` p where p.permission_code = 'tool' and p.delete_flag = 0 limit 1), 0),
       2, '/pc/tool/save', null, null, 21, now(), 'system', now(), 'system', 0
where not exists (select 1 from `osh_permission` p where p.permission_code = 'tool:create' and p.delete_flag = 0);

-- 修改工具
insert into `osh_permission` (
    `permission_name`, `permission_code`, `description`, `parent_id`, `type`, `url`, `path`, `component`, `sort_order`,
    `create_time`, `create_by`, `update_time`, `update_by`, `delete_flag`
)
select '修改工具', 'tool:update', '修改工具',
       coalesce((select p.id from `osh_permission` p where p.permission_code = 'tool' and p.delete_flag = 0 limit 1), 0),
       2, '/pc/tool/update', null, null, 22, now(), 'system', now(), 'system', 0
where not exists (select 1 from `osh_permission` p where p.permission_code = 'tool:update' and p.delete_flag = 0);

-- 删除工具
insert into `osh_permission` (
    `permission_name`, `permission_code`, `description`, `parent_id`, `type`, `url`, `path`, `component`, `sort_order`,
    `create_time`, `create_by`, `update_time`, `update_by`, `delete_flag`
)
select '删除工具', 'tool:delete', '删除工具',
       coalesce((select p.id from `osh_permission` p where p.permission_code = 'tool' and p.delete_flag = 0 limit 1), 0),
       2, '/pc/tool/delete', null, null, 23, now(), 'system', now(), 'system', 0
where not exists (select 1 from `osh_permission` p where p.permission_code = 'tool:delete' and p.delete_flag = 0);

-- 收藏工具
insert into `osh_permission` (
    `permission_name`, `permission_code`, `description`, `parent_id`, `type`, `url`, `path`, `component`, `sort_order`,
    `create_time`, `create_by`, `update_time`, `update_by`, `delete_flag`
)
select '收藏工具', 'tool:collection:add', '收藏工具',
       coalesce((select p.id from `osh_permission` p where p.permission_code = 'tool' and p.delete_flag = 0 limit 1), 0),
       2, '/pc/tool/collection/add', null, null, 24, now(), 'system', now(), 'system', 0
where not exists (select 1 from `osh_permission` p where p.permission_code = 'tool:collection:add' and p.delete_flag = 0);

-- 取消收藏工具
insert into `osh_permission` (
    `permission_name`, `permission_code`, `description`, `parent_id`, `type`, `url`, `path`, `component`, `sort_order`,
    `create_time`, `create_by`, `update_time`, `update_by`, `delete_flag`
)
select '取消收藏工具', 'tool:collection:remove', '取消收藏工具',
       coalesce((select p.id from `osh_permission` p where p.permission_code = 'tool' and p.delete_flag = 0 limit 1), 0),
       2, '/pc/tool/collection/remove', null, null, 25, now(), 'system', now(), 'system', 0
where not exists (select 1 from `osh_permission` p where p.permission_code = 'tool:collection:remove' and p.delete_flag = 0);

-- 点赞工具
insert into `osh_permission` (
    `permission_name`, `permission_code`, `description`, `parent_id`, `type`, `url`, `path`, `component`, `sort_order`,
    `create_time`, `create_by`, `update_time`, `update_by`, `delete_flag`
)
select '点赞工具', 'tool:vote:good', '点赞工具',
       coalesce((select p.id from `osh_permission` p where p.permission_code = 'tool' and p.delete_flag = 0 limit 1), 0),
       2, '/pc/tool/vote/good', null, null, 30, now(), 'system', now(), 'system', 0
where not exists (select 1 from `osh_permission` p where p.permission_code = 'tool:vote:good' and p.delete_flag = 0);

-- 差评工具
insert into `osh_permission` (
    `permission_name`, `permission_code`, `description`, `parent_id`, `type`, `url`, `path`, `component`, `sort_order`,
    `create_time`, `create_by`, `update_time`, `update_by`, `delete_flag`
)
select '差评工具', 'tool:vote:bad', '差评工具',
       coalesce((select p.id from `osh_permission` p where p.permission_code = 'tool' and p.delete_flag = 0 limit 1), 0),
       2, '/pc/tool/vote/bad', null, null, 31, now(), 'system', now(), 'system', 0
where not exists (select 1 from `osh_permission` p where p.permission_code = 'tool:vote:bad' and p.delete_flag = 0);
