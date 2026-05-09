
-- =============================================
-- 1. 扩展 osh_examination 表（加资源绑定字段）
-- =============================================
ALTER TABLE `osh_examination`
    ADD COLUMN `resource_type` varchar(20) DEFAULT NULL COMMENT '关联资源类型：course/book/null',
    ADD COLUMN `resource_id`   bigint(20)  DEFAULT NULL COMMENT '关联资源ID',
    ADD COLUMN `cover`         varchar(500) DEFAULT NULL COMMENT '考试封面图',
    ADD COLUMN `description`   text         DEFAULT NULL COMMENT '考试描述',
    ADD COLUMN `collect_count` int(11)      NOT NULL DEFAULT 0 COMMENT '收藏数';

-- =============================================
-- 2. 考试标签表（独立，与课程/QnA标签不共用）
-- ==-- ===========================================
-- 考试标签表初始化脚本
-- ===========================================
CREATE TABLE IF NOT EXISTS `osh_exam_tag` (
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    `name`        varchar(50)  NOT NULL                COMMENT '标签名称',
    `sort`        int(11)      NOT NULL DEFAULT 0      COMMENT '排序权重（越大越靠前）',
    `use_count`   int(11)      NOT NULL DEFAULT 0      COMMENT '使用次数',
    `status`      tinyint(1)   NOT NULL DEFAULT 1      COMMENT '状态：0-禁用 1-启用',
    `delete_flag` tinyint(1)   NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-删除',
    `create_by`   varchar(64)  DEFAULT ''              COMMENT '创建者',
    `create_time` datetime      DEFAULT NULL            COMMENT '创建时间',
    `update_by`   varchar(64)  DEFAULT ''              COMMENT '更新者',
    `update_time` datetime      DEFAULT NULL            COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    KEY `idx_use_count` (`use_count`),
    KEY `idx_delete_flag` (`delete_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试标签表';
-- 初始化默认标签
INSERT INTO `osh_exam_tag` (`name`, `sort`, `use_count`, `status`, `create_by`, `create_time`)
SELECT 'Java', 100, 0, 1, 'admin', NOW() WHERE NOT EXISTS (SELECT 1 FROM `osh_exam_tag` WHERE `name` = 'Java');
INSERT INTO `osh_exam_tag` (`name`, `sort`, `use_count`, `status`, `create_by`, `create_time`)
SELECT 'Python', 90, 0, 1, 'admin', NOW() WHERE NOT EXISTS (SELECT 1 FROM `osh_exam_tag` WHERE `name` = 'Python');
INSERT INTO `osh_exam_tag` (`name`, `sort`, `use_count`, `status`, `create_by`, `create_time`)
SELECT 'MySQL', 80, 0, 1, 'admin', NOW() WHERE NOT EXISTS (SELECT 1 FROM `osh_exam_tag` WHERE `name` = 'MySQL');
INSERT INTO `osh_exam_tag` (`name`, `sort`, `use_count`, `status`, `create_by`, `create_time`)
SELECT 'Spring Boot', 70, 0, 1, 'admin', NOW() WHERE NOT EXISTS (SELECT 1 FROM `osh_exam_tag` WHERE `name` = 'Spring Boot');
INSERT INTO `osh_exam_tag` (`name`, `sort`, `use_count`, `status`, `create_by`, `create_time`)
SELECT 'Docker', 60, 0, 1, 'admin', NOW() WHERE NOT EXISTS (SELECT 1 FROM `osh_exam_tag` WHERE `name` = 'Docker');
INSERT INTO `osh_exam_tag` (`name`, `sort`, `use_count`, `status`, `create_by`, `create_time`)
SELECT '算法', 50, 0, 1, 'admin', NOW() WHERE NOT EXISTS (SELECT 1 FROM `osh_exam_tag` WHERE `name` = '算法');
INSERT INTO `osh_exam_tag` (`name`, `sort`, `use_count`, `status`, `create_by`, `create_time`)
SELECT '前端开发', 40, 0, 1, 'admin', NOW() WHERE NOT EXISTS (SELECT 1 FROM `osh_exam_tag` WHERE `name` = '前端开发');
INSERT INTO `osh_exam_tag` (`name`, `sort`, `use_count`, `status`, `create_by`, `create_time`)
SELECT '面试题', 30, 0, 1, 'admin', NOW() WHERE NOT EXISTS (SELECT 1 FROM `osh_exam_tag` WHERE `name` = '面试题');

-- =============================================
-- 3. 考试-标签关联表
-- =============================================
CREATE TABLE IF NOT EXISTS `osh_exam_tag_rel` (
    `id`          bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `exam_id`     bigint(20)  NOT NULL                COMMENT '考试ID',
    `tag_id`      bigint(20)  NOT NULL                COMMENT '标签ID',
    `delete_flag` tinyint(1)  NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-删除',
    `create_by`   varchar(64) DEFAULT ''              COMMENT '创建者',
    `create_time` datetime    DEFAULT NULL            COMMENT '创建时间',
    `update_by`   varchar(64) DEFAULT ''              COMMENT '更新者',
    `update_time` datetime    DEFAULT NULL            COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_exam_tag` (`exam_id`, `tag_id`),
    KEY `idx_exam_id` (`exam_id`),
    KEY `idx_tag_id` (`tag_id`),
    KEY `idx_delete_flag` (`delete_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试标签关联表';

-- =============================================
-- 4. 考试收藏表（独立，与课程收藏同模式）
-- =============================================
CREATE TABLE IF NOT EXISTS `osh_exam_collection` (
    `id`          bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     bigint(20)  NOT NULL                COMMENT '用户ID',
    `exam_id`     bigint(20)  NOT NULL                COMMENT '考试ID',
    `delete_flag` tinyint(1)  NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-删除',
    `create_by`   varchar(64) DEFAULT ''              COMMENT '创建者',
    `create_time` datetime    DEFAULT NULL            COMMENT '创建时间',
    `update_by`   varchar(64) DEFAULT ''              COMMENT '更新者',
    `update_time` datetime    DEFAULT NULL            COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_exam` (`user_id`, `exam_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_exam_id` (`exam_id`),
    KEY `idx_delete_flag` (`delete_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试收藏表';

-- =============================================
-- 5. 权限数据（接续现有 id=73 之后，从 74 开始）
-- =============================================
INSERT INTO `osh_permission` (`id`, `permission_name`, `permission_code`, `description`, `parent_id`, `type`, `url`, `path`, `component`, `sort_order`, `create_time`, `create_by`, `update_time`, `update_by`, `delete_flag`) VALUES
-- 考试模块父菜单
(74, '考试模块',   'exam',                NULL,           0,  1, NULL,                          '/paper', NULL, 74, NOW(), 'system', NOW(), 'system', 0),
-- 考试列表（所有登录用户可见）
(75, '考试列表',   'exam:list',           '查询考试列表', 74, 2, '/pc/testpaper/list',           NULL, NULL, 1,  NOW(), 'system', NOW(), 'system', 0),
-- 考试详情（VIP+ 才能参加，后端校验）
(76, '参加考试',   'exam:take',           '参加考试',     74, 2, '/pc/testpaper/read',           NULL, NULL, 2,  NOW(), 'system', NOW(), 'system', 0),
-- 提交答卷
(77, '提交答卷',   'exam:submit',         '提交考试答卷', 74, 2, '/pc/user_test/save',           NULL, NULL, 3,  NOW(), 'system', NOW(), 'system', 0),
-- 我的考试记录
(78, '考试记录',   'exam:record:list',    '查询考试记录', 74, 2, '/pc/user_test/list',           NULL, NULL, 4,  NOW(), 'system', NOW(), 'system', 0),
-- 管理：新增考试（高权限）
(79, '新增考试',   'exam:create',         '新增考试',     74, 2, '/pc/exam/save',                NULL, NULL, 5,  NOW(), 'system', NOW(), 'system', 0),
-- 管理：修改考试
(80, '修改考试',   'exam:update',         '修改考试信息', 74, 2, '/pc/exam/update',              NULL, NULL, 6,  NOW(), 'system', NOW(), 'system', 0),
-- 管理：删除考试
(81, '删除考试',   'exam:delete',         '删除考试',     74, 2, '/pc/exam/delete',              NULL, NULL, 7,  NOW(), 'system', NOW(), 'system', 0),
-- 收藏考试
(82, '收藏考试',   'exam:collect',        '收藏考试',     74, 2, '/pc/exam/collect',             NULL, NULL, 8,  NOW(), 'system', NOW(), 'system', 0),
-- 取消收藏
(83, '取消收藏',   'exam:collect:cancel', '取消收藏考试', 74, 2, '/pc/exam/collect/cancel',      NULL, NULL, 9,  NOW(), 'system', NOW(), 'system', 0),
-- 标签列表
(84, '标签列表',   'exam:tag:list',       '查询考试标签', 74, 2, '/pc/exam/tag/list',            NULL, NULL, 10, NOW(), 'system', NOW(), 'system', 0);

-- =============================================
-- 6. 角色权限关联（所有角色1-7 分配考试模块权限）
-- 角色等级说明：
--   role 1 = 普通用户(level=0)  → 只给 exam:list
--   role 2 = 小班用户(level=1)  → 只给 exam:list
--   role 3 = 普通开发者(level=2) → 给 exam:list/take/submit/record/collect/collect:cancel/tag:list
--   role 4 = VIP(level=3)       → 同 role 3
--   role 5 = 核心开发者(level=4) → 同 role 3
--   role 6 = 创始人(level=5)    → 全部权限
--   role 7 = 管理员             → 全部权限
-- =============================================

-- 角色1（普通用户）：仅列表
INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`) VALUES
(1, 74, 'system', 'system'), (1, 75, 'system', 'system');

-- 角色2（小班用户）：仅列表
INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`) VALUES
(2, 74, 'system', 'system'), (2, 75, 'system', 'system');

-- 角色3（普通开发者 level=2）：可参加考试
INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`) VALUES
(3, 74, 'system', 'system'), (3, 75, 'system', 'system'), (3, 76, 'system', 'system'),
(3, 77, 'system', 'system'), (3, 78, 'system', 'system'), (3, 82, 'system', 'system'),
(3, 83, 'system', 'system'), (3, 84, 'system', 'system');

-- 角色4（VIP level=3）：可参加考试
INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`) VALUES
(4, 74, 'system', 'system'), (4, 75, 'system', 'system'), (4, 76, 'system', 'system'),
(4, 77, 'system', 'system'), (4, 78, 'system', 'system'), (4, 82, 'system', 'system'),
(4, 83, 'system', 'system'), (4, 84, 'system', 'system');

-- 角色5（核心开发者 level=4）：可参加考试
INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`) VALUES
(5, 74, 'system', 'system'), (5, 75, 'system', 'system'), (5, 76, 'system', 'system'),
(5, 77, 'system', 'system'), (5, 78, 'system', 'system'), (5, 82, 'system', 'system'),
(5, 83, 'system', 'system'), (5, 84, 'system', 'system');

-- 角色6（创始人 level=5）：全部权限
INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`) VALUES
(6, 74, 'system', 'system'), (6, 75, 'system', 'system'), (6, 76, 'system', 'system'),
(6, 77, 'system', 'system'), (6, 78, 'system', 'system'), (6, 79, 'system', 'system'),
(6, 80, 'system', 'system'), (6, 81, 'system', 'system'), (6, 82, 'system', 'system'),
(6, 83, 'system', 'system'), (6, 84, 'system', 'system');

-- 角色7（管理员）：全部权限
INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`) VALUES
(7, 74, 'system', 'system'), (7, 75, 'system', 'system'), (7, 76, 'system', 'system'),
(7, 77, 'system', 'system'), (7, 78, 'system', 'system'), (7, 79, 'system', 'system'),
(7, 80, 'system', 'system'), (7, 81, 'system', 'system'), (7, 82, 'system', 'system'),
(7, 83, 'system', 'system'), (7, 84, 'system', 'system');
