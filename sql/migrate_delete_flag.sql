-- =============================================
-- 迁移脚本：统一软删除字段为 delete_flag
-- 执行数据库: backstage
-- =============================================

-- =============================================
-- 1. osh_examination 表：is_delet / is_delete → delete_flag
-- =============================================

-- 先检查是否已有 delete_flag 列，没有则添加
ALTER TABLE `osh_examination`
    ADD COLUMN IF NOT EXISTS `delete_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除';

-- 将旧字段 is_delet 的值迁移到 delete_flag（兼容两种拼写）
UPDATE `osh_examination` SET `delete_flag` = `is_delet` WHERE `is_delet` IS NOT NULL;

-- 删除旧字段（确认数据迁移完成后执行）
ALTER TABLE `osh_examination` DROP COLUMN IF EXISTS `is_delet`;

-- =============================================
-- 2. osh_user_exam_record 表：is_delete → delete_flag
-- =============================================

ALTER TABLE `osh_user_exam_record`
    ADD COLUMN IF NOT EXISTS `delete_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除';

-- 将旧字段 is_delete 的值迁移到 delete_flag
UPDATE `osh_user_exam_record` SET `delete_flag` = `is_delete` WHERE `is_delete` IS NOT NULL;

-- 删除旧字段
ALTER TABLE `osh_user_exam_record` DROP COLUMN IF EXISTS `is_delete`;

-- =============================================
-- 3. osh_question 表：is_delete → delete_flag
--    （ExamMapper.xml 中 JOIN osh_question 用的是 delete_flag，需保持一致）
-- =============================================

ALTER TABLE `osh_question`
    ADD COLUMN IF NOT EXISTS `delete_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除';

UPDATE `osh_question` SET `delete_flag` = `is_delete` WHERE `is_delete` IS NOT NULL;

ALTER TABLE `osh_question` DROP COLUMN IF EXISTS `is_delete`;

-- =============================================
-- 4. 考试模块权限数据（幂等插入）
--    角色对应关系（来自数据库实际数据）：
--      role_id=1, role_code=normal,         level=1  → 普通用户
--      role_id=2, role_code=developer,      level=2  → 普通开发者
--      role_id=3, role_code=vip,            level=2  → VIP用户
--      role_id=4, role_code=small_class,    level=3  → 小班用户
--      role_id=5, role_code=manager,        level=4  → 普通管理员
--      role_id=6, role_code=core_developer, level=5  → 核心开发者
--      role_id=7, role_code=founder,        level=6  → 创始人
--
--    权限规则：除普通用户（level=1）外，其余角色均可参加考试
--      level=1（role_id=1）：仅考试列表、标签列表
--      level>=2（role_id=2,3,4,5,6,7）：可参加考试、提交答卷、查看记录、收藏
--      level>=5（role_id=6,7）：额外拥有管理权限（新增/修改/删除考试）
-- =============================================

-- 先清理考试模块旧的权限关联（避免脏数据）
DELETE FROM `osh_role_permission` WHERE `permission_id` BETWEEN 74 AND 84;

-- 权限数据（幂等插入，若 osh_permission 中已有则跳过）
INSERT IGNORE INTO `osh_permission`
    (`id`, `permission_name`, `permission_code`, `description`, `parent_id`, `type`, `url`, `path`, `component`, `sort_order`, `create_by`, `update_by`, `delete_flag`)
VALUES
    (74, '考试模块',   'exam',                NULL,           0,  1, NULL,                 '/paper', NULL, 74, 'system', 'system', 0),
    (75, '考试列表',   'exam:list',           '查询考试列表', 74, 2, '/pc/testpaper/list', NULL, NULL, 1,  'system', 'system', 0),
    (76, '参加考试',   'exam:take',           '参加考试',     74, 2, '/pc/testpaper/read', NULL, NULL, 2,  'system', 'system', 0),
    (77, '提交答卷',   'exam:submit',         '提交考试答卷', 74, 2, '/pc/user_test/save', NULL, NULL, 3,  'system', 'system', 0),
    (78, '考试记录',   'exam:record:list',    '查询考试记录', 74, 2, '/pc/user_test/list', NULL, NULL, 4,  'system', 'system', 0),
    (79, '新增考试',   'exam:create',         '新增考试',     74, 2, '/pc/exam/save',      NULL, NULL, 5,  'system', 'system', 0),
    (80, '修改考试',   'exam:update',         '修改考试信息', 74, 2, '/pc/exam/update',    NULL, NULL, 6,  'system', 'system', 0),
    (81, '删除考试',   'exam:delete',         '删除考试',     74, 2, '/pc/exam/delete',    NULL, NULL, 7,  'system', 'system', 0),
    (82, '收藏考试',   'exam:collect',        '收藏考试',     74, 2, '/pc/exam/collect',   NULL, NULL, 8,  'system', 'system', 0),
    (83, '取消收藏',   'exam:collect:cancel', '取消收藏考试', 74, 2, '/pc/exam/collect',   NULL, NULL, 9,  'system', 'system', 0),
    (84, '标签列表',   'exam:tag:list',       '查询考试标签', 74, 2, '/pc/exam/tag/list',  NULL, NULL, 10, 'system', 'system', 0);

-- ── 角色1（普通用户 level=1）：仅考试列表 + 标签列表
INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`, `delete_flag`) VALUES
(1, 74, 'system', 'system', 0),
(1, 75, 'system', 'system', 0),
(1, 84, 'system', 'system', 0);

-- ── 角色2（普通开发者 level=2）：可参加考试
INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`, `delete_flag`) VALUES
(2, 74, 'system', 'system', 0),
(2, 75, 'system', 'system', 0),
(2, 76, 'system', 'system', 0),
(2, 77, 'system', 'system', 0),
(2, 78, 'system', 'system', 0),
(2, 82, 'system', 'system', 0),
(2, 83, 'system', 'system', 0),
(2, 84, 'system', 'system', 0);

-- ── 角色3（VIP用户 level=2）：可参加考试
INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`, `delete_flag`) VALUES
(3, 74, 'system', 'system', 0),
(3, 75, 'system', 'system', 0),
(3, 76, 'system', 'system', 0),
(3, 77, 'system', 'system', 0),
(3, 78, 'system', 'system', 0),
(3, 82, 'system', 'system', 0),
(3, 83, 'system', 'system', 0),
(3, 84, 'system', 'system', 0);

-- ── 角色4（小班用户 level=3）：可参加考试
INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`, `delete_flag`) VALUES
(4, 74, 'system', 'system', 0),
(4, 75, 'system', 'system', 0),
(4, 76, 'system', 'system', 0),
(4, 77, 'system', 'system', 0),
(4, 78, 'system', 'system', 0),
(4, 82, 'system', 'system', 0),
(4, 83, 'system', 'system', 0),
(4, 84, 'system', 'system', 0);

-- ── 角色5（普通管理员 level=4）：可参加考试
INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`, `delete_flag`) VALUES
(5, 74, 'system', 'system', 0),
(5, 75, 'system', 'system', 0),
(5, 76, 'system', 'system', 0),
(5, 77, 'system', 'system', 0),
(5, 78, 'system', 'system', 0),
(5, 82, 'system', 'system', 0),
(5, 83, 'system', 'system', 0),
(5, 84, 'system', 'system', 0);

-- ── 角色6（核心开发者 level=5）：全部权限含管理
INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`, `delete_flag`) VALUES
(6, 74, 'system', 'system', 0),
(6, 75, 'system', 'system', 0),
(6, 76, 'system', 'system', 0),
(6, 77, 'system', 'system', 0),
(6, 78, 'system', 'system', 0),
(6, 79, 'system', 'system', 0),
(6, 80, 'system', 'system', 0),
(6, 81, 'system', 'system', 0),
(6, 82, 'system', 'system', 0),
(6, 83, 'system', 'system', 0),
(6, 84, 'system', 'system', 0);

-- ── 角色7（创始人 level=6）：全部权限含管理
INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`, `delete_flag`) VALUES
(7, 74, 'system', 'system', 0),
(7, 75, 'system', 'system', 0),
(7, 76, 'system', 'system', 0),
(7, 77, 'system', 'system', 0),
(7, 78, 'system', 'system', 0),
(7, 79, 'system', 'system', 0),
(7, 80, 'system', 'system', 0),
(7, 81, 'system', 'system', 0),
(7, 82, 'system', 'system', 0),
(7, 83, 'system', 'system', 0),
(7, 84, 'system', 'system', 0);
