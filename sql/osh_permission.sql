-- 1. 角色表
drop table if exists `osh_role`;
create table `osh_role` (
    `id` int primary key auto_increment comment '角色ID',
    `role_name` varchar(50) unique not null comment '角色名称',
    `role_code` varchar(50) unique not null comment '角色编码',
    `level` tinyint default 0 comment '角色等级：0-普通用户，1-小班用户,2-普通开发者,3-vip用户/普通管理员,4-核心开发者,5-创始人',
    `description` varchar(200) comment '角色描述',
    `status` tinyint default 1 comment '状态：1-启用，0-禁用',
    `create_time` datetime default current_timestamp comment '创建时间',
    `create_by` bigint NOT NULL default 0 comment '创建人',
    `update_time` datetime default current_timestamp on update current_timestamp comment '更新时间',
    `update_by` bigint NOT NULL default 0 comment '更新人',
    `delete_flag` tinyint default 0 comment '删除标志：0-未删除，1-已删除'
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_as_cs comment='角色表';

-- 2. 权限菜单表
drop table if exists `osh_permission`;
create table `osh_permission` (
    `id` int primary key auto_increment comment '权限ID',
    `permission_name` varchar(100) not null comment '权限名称',
    `permission_code` varchar(100) comment '权限标识，如：oshUser:list',
    `description` varchar(200) comment '权限描述',
    `parent_id` int default 0 comment '父权限ID，0表示顶级权限',
    `type` tinyint default 1 comment '类型：1-菜单，2-按钮/-API',
    `url` varchar(200) comment '请求路径',
    `path` varchar(200) comment '前端路由路径',
    `component` varchar(200) comment '前端组件路径',
    `sort_order` int default 0 comment '排序号',
    `create_time` datetime default current_timestamp comment '创建时间',
    `create_by` varchar(64) default 'system' comment '创建人',
    `update_time` datetime default current_timestamp on update current_timestamp comment '更新时间',
    `update_by` varchar(64) default 'system' comment '更新人',
    `delete_flag` tinyint default 0 comment '删除标志：0-未删除，1-已删除'
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_as_cs comment='权限菜单表';

-- 3. 用户-角色关联表
drop table if exists `osh_user_role`;
create table `osh_user_role` (
    `user_id` bigint not null comment '用户ID',
    `role_id` bigint not null comment '角色ID',
    `create_time` datetime default current_timestamp comment '创建时间',
    `create_by` varchar(64) default 'system' comment '创建人',
    `update_time` datetime default current_timestamp on update current_timestamp comment '更新时间',
    `update_by` varchar(64) default 'system' comment '更新人',
    `delete_flag` tinyint default 0 comment '删除标志：0-未删除，1-已删除',
    primary key (`user_id`, `role_id`)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_as_cs comment='用户角色关联表';

-- 4. 角色-权限关联表
drop table if exists `osh_role_permission`;
create table `osh_role_permission` (
    `role_id` int not null comment '角色ID',
    `permission_id` int not null comment '权限ID',
    `create_time` datetime default current_timestamp comment '创建时间',
    `create_by` varchar(64) default 'system' comment '创建人',
    `update_time` datetime default current_timestamp on update current_timestamp comment '更新时间',
    `update_by` varchar(64) default 'system' comment '更新人',
    `delete_flag` tinyint default 0 comment '删除标志：0-未删除，1-已删除',
    primary key (`role_id`, `permission_id`)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_as_cs comment='角色权限关联表';