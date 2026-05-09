-- =============================================================================
-- 考试模块：题目权限 + 角色授权（与 exam:question:*、level 4–6 / @examLevelAuth 对齐）
--
-- 若 Navicat 显示 Affected rows: 0：
--   • 权限两条：可能已存在相同 permission_code（幂等）；或请先跑 exam_permissions_diagnose.sql 核对。
--   • 角色授权：常见原因是没有 osh_role.level 在 [4,6] 的行；已增加「种子 role_id 5,6,7」可选兜底（与 migrate_delete_flag 一致）。
--
-- 执行后请让相关账号重新登录，权限才会进 JWT / 前端 permission 列表。
-- =============================================================================

SET NAMES utf8mb4;

-- ── A) 题目权限（不固定 id，避免与现网 85/86 占用冲突；父级为 permission_code = 'exam'）
INSERT INTO `osh_permission` (
    `permission_name`, `permission_code`, `description`, `parent_id`, `type`, `url`,
    `path`, `component`, `sort_order`, `create_time`, `create_by`, `update_time`, `update_by`, `delete_flag`
)
SELECT
    '题目保存',
    'exam:question:save',
    '新增或修改考试题目',
    (SELECT `id` FROM `osh_permission` WHERE `permission_code` = 'exam' AND `delete_flag` = 0 ORDER BY `id` LIMIT 1),
    2,
    '/pc/exam/question/save',
    NULL,
    NULL,
    11,
    NOW(),
    'system',
    NOW(),
    'system',
    0
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `osh_permission` WHERE `permission_code` = 'exam:question:save' AND `delete_flag` = 0
);

INSERT INTO `osh_permission` (
    `permission_name`, `permission_code`, `description`, `parent_id`, `type`, `url`,
    `path`, `component`, `sort_order`, `create_time`, `create_by`, `update_time`, `update_by`, `delete_flag`
)
SELECT
    '题目删除',
    'exam:question:delete',
    '删除考试题目',
    (SELECT `id` FROM `osh_permission` WHERE `permission_code` = 'exam' AND `delete_flag` = 0 ORDER BY `id` LIMIT 1),
    2,
    '/pc/exam/question/delete',
    NULL,
    NULL,
    12,
    NOW(),
    'system',
    NOW(),
    'system',
    0
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `osh_permission` WHERE `permission_code` = 'exam:question:delete' AND `delete_flag` = 0
);

-- ── B) 仅把「题目保存 / 题目删除」授给管理员角色（避免对 exam:create 等重复插入导致全是 IGNORE、难以观察）
--     条件 1：level 在 4–6（与 ExamLevelAuthorization、前端一致）
--     条件 2：或 role_id 为 5、6、7（与 migrate_delete_flag.sql 种子一致；若你库中这些 id 不是管理员请勿用）
INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`, `delete_flag`)
SELECT
    r.`id`,
    p.`id`,
    'system',
    'system',
    0
FROM `osh_role` r
INNER JOIN `osh_permission` p
    ON p.`permission_code` IN ('exam:question:save', 'exam:question:delete')
    AND p.`delete_flag` = 0
WHERE r.`delete_flag` = 0
  AND (
      (r.`level` >= 4 AND r.`level` <= 6)
      OR r.`id` IN (5, 6, 7)
  );

-- ── C) 可选：仍希望 level 4–6 角色同时具备「考试创建/修改/删除」且库里尚未关联时，取消下面整段注释再执行
/*
INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`, `delete_flag`)
SELECT r.`id`, p.`id`, 'system', 'system', 0
FROM `osh_role` r
INNER JOIN `osh_permission` p
    ON p.`permission_code` IN ('exam:create', 'exam:update', 'exam:delete')
    AND p.`delete_flag` = 0
WHERE r.`delete_flag` = 0
  AND r.`level` >= 4
  AND r.`level` <= 6;
*/
