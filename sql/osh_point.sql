-- 1. 角色表
drop table if exists osh_user_role;
CREATE TABLE osh_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    name VARCHAR(50) NOT NULL COMMENT '角色名称',
    level INT NOT NULL COMMENT '角色级别（数字越大权限越高）',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    created_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    updated_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新人',
    is_delete TINYINT(4) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除'
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_as_cs;

-- 2. 权限表
drop table if exists osh_user_permission;
CREATE TABLE osh_user_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    name VARCHAR(50) NOT NULL COMMENT '权限名称',
    type VARCHAR(20) COMMENT '权限类型：MENU/BUTTON/API',
    parent_id BIGINT DEFAULT 0,
    sort_order INT DEFAULT 0,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    created_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    updated_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新人',
    is_delete TINYINT(4) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除'
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_as_cs;

-- 3. 角色-权限关联表
drop table if exists osh_user_role_permission;
CREATE TABLE osh_user_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    created_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    updated_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新人',
    is_delete TINYINT(4) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_as_cs;

-- 4. 用户-角色关联表
drop table if exists osh_user_user_role;
CREATE TABLE osh_user_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    created_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    updated_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新人',
    is_delete TINYINT(4) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    UNIQUE KEY uk_user_role (user_id, role_id)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_as_cs;

-- 初始化角色数据
INSERT INTO osh_user_role (code, name, level, created_by, updated_by) VALUES
    ('FOUNDER', '创始人', 100, 'system', 'system'),
    ('CORE_DEVELOPER', '核心开发者', 90, 'system', 'system'),
    ('ADMIN', '管理员', 80, 'system', 'system'),
    ('NORMAL_DEVELOPER', '普通开发者', 60, 'system', 'system'),
    ('YEAR_VIP', '年VIP用户', 50, 'system', 'system'),
    ('BABY_USER', '小班用户', 30, 'system', 'system'),
    ('NORMAL_USER', '普通用户', 10, 'system', 'system');

-- 初始化权限数据
INSERT INTO osh_user_permission (code, name, type, created_by, updated_by) VALUES
    ('user:view', '查看用户', 'API', 'system', 'system'),
    ('user:edit', '编辑用户', 'API', 'system', 'system'),
    ('user:delete', '删除用户', 'API', 'system', 'system'),
    ('points:earn', '获取积分', 'API', 'system', 'system'),
    ('points:deduct', '扣除积分', 'API', 'system', 'system');