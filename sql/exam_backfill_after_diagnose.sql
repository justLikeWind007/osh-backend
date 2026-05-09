-- =============================================================================
-- 考试权限补全（不占用固定 id：用 MAX(id)+1 顺延；适合 74+ 已被其它模块占用的库）
--
-- 执行前可核对：
--   SELECT MAX(id) AS max_permission_id FROM osh_permission;
--
-- 从上到下整段执行。完成后相关账号重新登录。
--
-- create_by / update_by：表字段为数值型用户 id（与 OSHBaseEntity 一致），勿写字符串。
-- 默认用 1；若你库无 user id=1，请先：SELECT id FROM osh_user ORDER BY id LIMIT 1; 再全局替换下面的 @BOOTSTRAP_USER_ID。
-- =============================================================================

SET NAMES utf8mb4;

-- 脚本内「操作人」用户 id（按需改成你的管理员用户主键）
SET @BOOTSTRAP_USER_ID := 1;

-- ── 0) 修正题目删除权限码笔误（可选）
UPDATE `osh_permission`
SET `permission_code` = 'exam:question:delete',
    `update_time` = NOW(),
    `update_by` = @BOOTSTRAP_USER_ID
WHERE `permission_code` IN ('exam:question:delet', 'exam:question:del')
  AND `delete_flag` = 0;

-- ── 1) 父节点 exam（不存在才插入，id = 当前 MAX(id) + 1）
INSERT INTO `osh_permission` (
    `id`, `permission_name`, `permission_code`, `description`, `parent_id`, `type`,
    `url`, `path`, `component`, `sort_order`, `create_by`, `update_by`, `delete_flag`
)
SELECT
    mx.m + 1,
    '考试模块',
    'exam',
    NULL,
    0,
    1,
    NULL,
    '/paper',
    NULL,
    mx.m + 1,
    @BOOTSTRAP_USER_ID,
    @BOOTSTRAP_USER_ID,
    0
FROM (SELECT IFNULL(MAX(`id`), 0) AS m FROM `osh_permission`) AS mx
WHERE NOT EXISTS (
    SELECT 1 FROM `osh_permission` WHERE `permission_code` = 'exam' AND `delete_flag` = 0
);

-- ── 2) 各子权限各一条 INSERT（每条执行后 MAX(id) 变化，避免主键冲突）
INSERT INTO `osh_permission` (`id`, `permission_name`, `permission_code`, `description`, `parent_id`, `type`, `url`, `path`, `component`, `sort_order`, `create_by`, `update_by`, `delete_flag`)
SELECT mx.m + 1, '考试列表', 'exam:list', '查询考试列表', p.id, 2, '/pc/testpaper/list', NULL, NULL, 1, @BOOTSTRAP_USER_ID, @BOOTSTRAP_USER_ID, 0
FROM (SELECT IFNULL(MAX(`id`), 0) AS m FROM `osh_permission`) AS mx
CROSS JOIN (SELECT `id` FROM `osh_permission` WHERE `permission_code` = 'exam' AND `delete_flag` = 0 LIMIT 1) AS p
WHERE NOT EXISTS (SELECT 1 FROM `osh_permission` WHERE `permission_code` = 'exam:list' AND `delete_flag` = 0);

INSERT INTO `osh_permission` (`id`, `permission_name`, `permission_code`, `description`, `parent_id`, `type`, `url`, `path`, `component`, `sort_order`, `create_by`, `update_by`, `delete_flag`)
SELECT mx.m + 1, '参加考试', 'exam:take', '参加考试', p.id, 2, '/pc/testpaper/read', NULL, NULL, 2, @BOOTSTRAP_USER_ID, @BOOTSTRAP_USER_ID, 0
FROM (SELECT IFNULL(MAX(`id`), 0) AS m FROM `osh_permission`) AS mx
CROSS JOIN (SELECT `id` FROM `osh_permission` WHERE `permission_code` = 'exam' AND `delete_flag` = 0 LIMIT 1) AS p
WHERE NOT EXISTS (SELECT 1 FROM `osh_permission` WHERE `permission_code` = 'exam:take' AND `delete_flag` = 0);

INSERT INTO `osh_permission` (`id`, `permission_name`, `permission_code`, `description`, `parent_id`, `type`, `url`, `path`, `component`, `sort_order`, `create_by`, `update_by`, `delete_flag`)
SELECT mx.m + 1, '提交答卷', 'exam:submit', '提交考试答卷', p.id, 2, '/pc/user_test/save', NULL, NULL, 3, @BOOTSTRAP_USER_ID, @BOOTSTRAP_USER_ID, 0
FROM (SELECT IFNULL(MAX(`id`), 0) AS m FROM `osh_permission`) AS mx
CROSS JOIN (SELECT `id` FROM `osh_permission` WHERE `permission_code` = 'exam' AND `delete_flag` = 0 LIMIT 1) AS p
WHERE NOT EXISTS (SELECT 1 FROM `osh_permission` WHERE `permission_code` = 'exam:submit' AND `delete_flag` = 0);

INSERT INTO `osh_permission` (`id`, `permission_name`, `permission_code`, `description`, `parent_id`, `type`, `url`, `path`, `component`, `sort_order`, `create_by`, `update_by`, `delete_flag`)
SELECT mx.m + 1, '考试记录', 'exam:record:list', '查询考试记录', p.id, 2, '/pc/user_test/list', NULL, NULL, 4, @BOOTSTRAP_USER_ID, @BOOTSTRAP_USER_ID, 0
FROM (SELECT IFNULL(MAX(`id`), 0) AS m FROM `osh_permission`) AS mx
CROSS JOIN (SELECT `id` FROM `osh_permission` WHERE `permission_code` = 'exam' AND `delete_flag` = 0 LIMIT 1) AS p
WHERE NOT EXISTS (SELECT 1 FROM `osh_permission` WHERE `permission_code` = 'exam:record:list' AND `delete_flag` = 0);

INSERT INTO `osh_permission` (`id`, `permission_name`, `permission_code`, `description`, `parent_id`, `type`, `url`, `path`, `component`, `sort_order`, `create_by`, `update_by`, `delete_flag`)
SELECT mx.m + 1, '新增考试', 'exam:create', '新增考试', p.id, 2, '/pc/exam/save', NULL, NULL, 5, @BOOTSTRAP_USER_ID, @BOOTSTRAP_USER_ID, 0
FROM (SELECT IFNULL(MAX(`id`), 0) AS m FROM `osh_permission`) AS mx
CROSS JOIN (SELECT `id` FROM `osh_permission` WHERE `permission_code` = 'exam' AND `delete_flag` = 0 LIMIT 1) AS p
WHERE NOT EXISTS (SELECT 1 FROM `osh_permission` WHERE `permission_code` = 'exam:create' AND `delete_flag` = 0);

INSERT INTO `osh_permission` (`id`, `permission_name`, `permission_code`, `description`, `parent_id`, `type`, `url`, `path`, `component`, `sort_order`, `create_by`, `update_by`, `delete_flag`)
SELECT mx.m + 1, '修改考试', 'exam:update', '修改考试信息', p.id, 2, '/pc/exam/update', NULL, NULL, 6, @BOOTSTRAP_USER_ID, @BOOTSTRAP_USER_ID, 0
FROM (SELECT IFNULL(MAX(`id`), 0) AS m FROM `osh_permission`) AS mx
CROSS JOIN (SELECT `id` FROM `osh_permission` WHERE `permission_code` = 'exam' AND `delete_flag` = 0 LIMIT 1) AS p
WHERE NOT EXISTS (SELECT 1 FROM `osh_permission` WHERE `permission_code` = 'exam:update' AND `delete_flag` = 0);

INSERT INTO `osh_permission` (`id`, `permission_name`, `permission_code`, `description`, `parent_id`, `type`, `url`, `path`, `component`, `sort_order`, `create_by`, `update_by`, `delete_flag`)
SELECT mx.m + 1, '删除考试', 'exam:delete', '删除考试', p.id, 2, '/pc/exam/delete', NULL, NULL, 7, @BOOTSTRAP_USER_ID, @BOOTSTRAP_USER_ID, 0
FROM (SELECT IFNULL(MAX(`id`), 0) AS m FROM `osh_permission`) AS mx
CROSS JOIN (SELECT `id` FROM `osh_permission` WHERE `permission_code` = 'exam' AND `delete_flag` = 0 LIMIT 1) AS p
WHERE NOT EXISTS (SELECT 1 FROM `osh_permission` WHERE `permission_code` = 'exam:delete' AND `delete_flag` = 0);

-- ── 3) 已有行（收藏/标签/题目）统一挂到 exam 父节点下
UPDATE `osh_permission` AS c
INNER JOIN `osh_permission` AS p
    ON p.`permission_code` = 'exam' AND p.`delete_flag` = 0
SET c.`parent_id` = p.`id`,
    c.`update_time` = NOW(),
    c.`update_by` = @BOOTSTRAP_USER_ID
WHERE c.`delete_flag` = 0
  AND c.`permission_code` IN (
      'exam:collect',
      'exam:collect:cancel',
      'exam:tag:list',
      'exam:question:save',
      'exam:question:delete'
  );

-- ── 4) 角色–权限（按 permission_code 取 id，INSERT IGNORE 幂等；与 migrate_delete_flag 角色策略一致）
INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`, `delete_flag`)
SELECT 1, p.`id`, @BOOTSTRAP_USER_ID, @BOOTSTRAP_USER_ID, 0
FROM `osh_permission` p
WHERE p.`delete_flag` = 0 AND p.`permission_code` IN ('exam', 'exam:list', 'exam:tag:list');

INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`, `delete_flag`)
SELECT 2, p.`id`, @BOOTSTRAP_USER_ID, @BOOTSTRAP_USER_ID, 0
FROM `osh_permission` p
WHERE p.`delete_flag` = 0
  AND p.`permission_code` IN (
      'exam', 'exam:list', 'exam:take', 'exam:submit', 'exam:record:list',
      'exam:collect', 'exam:collect:cancel', 'exam:tag:list'
  );

INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`, `delete_flag`)
SELECT 3, p.`id`, @BOOTSTRAP_USER_ID, @BOOTSTRAP_USER_ID, 0
FROM `osh_permission` p
WHERE p.`delete_flag` = 0
  AND p.`permission_code` IN (
      'exam', 'exam:list', 'exam:take', 'exam:submit', 'exam:record:list',
      'exam:collect', 'exam:collect:cancel', 'exam:tag:list'
  );

INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`, `delete_flag`)
SELECT 4, p.`id`, @BOOTSTRAP_USER_ID, @BOOTSTRAP_USER_ID, 0
FROM `osh_permission` p
WHERE p.`delete_flag` = 0
  AND p.`permission_code` IN (
      'exam', 'exam:list', 'exam:take', 'exam:submit', 'exam:record:list',
      'exam:collect', 'exam:collect:cancel', 'exam:tag:list'
  );

INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`, `delete_flag`)
SELECT 5, p.`id`, @BOOTSTRAP_USER_ID, @BOOTSTRAP_USER_ID, 0
FROM `osh_permission` p
WHERE p.`delete_flag` = 0
  AND p.`permission_code` IN (
      'exam', 'exam:list', 'exam:take', 'exam:submit', 'exam:record:list',
      'exam:collect', 'exam:collect:cancel', 'exam:tag:list'
  );

INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`, `delete_flag`)
SELECT 6, p.`id`, @BOOTSTRAP_USER_ID, @BOOTSTRAP_USER_ID, 0
FROM `osh_permission` p
WHERE p.`delete_flag` = 0
  AND p.`permission_code` IN (
      'exam', 'exam:list', 'exam:take', 'exam:submit', 'exam:record:list',
      'exam:create', 'exam:update', 'exam:delete',
      'exam:collect', 'exam:collect:cancel', 'exam:tag:list',
      'exam:question:save', 'exam:question:delete'
  );

INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`, `delete_flag`)
SELECT 7, p.`id`, @BOOTSTRAP_USER_ID, @BOOTSTRAP_USER_ID, 0
FROM `osh_permission` p
WHERE p.`delete_flag` = 0
  AND p.`permission_code` IN (
      'exam', 'exam:list', 'exam:take', 'exam:submit', 'exam:record:list',
      'exam:create', 'exam:update', 'exam:delete',
      'exam:collect', 'exam:collect:cancel', 'exam:tag:list',
      'exam:question:save', 'exam:question:delete'
  );

-- 普通管理员 role_id=5 也补题目维护（与 level 4–6 产品规则一致；若不想给 5 题目权限可删掉本段）
INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`, `delete_flag`)
SELECT 5, p.`id`, @BOOTSTRAP_USER_ID, @BOOTSTRAP_USER_ID, 0
FROM `osh_permission` p
WHERE p.`delete_flag` = 0 AND p.`permission_code` IN ('exam:question:save', 'exam:question:delete');

-- ── 5) 其它 level 4–6 角色（若 id 不是 5/6/7 也能挂上题目权限）
INSERT IGNORE INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`, `delete_flag`)
SELECT r.`id`, p.`id`, @BOOTSTRAP_USER_ID, @BOOTSTRAP_USER_ID, 0
FROM `osh_role` r
INNER JOIN `osh_permission` p
    ON p.`permission_code` IN ('exam:question:save', 'exam:question:delete')
    AND p.`delete_flag` = 0
WHERE r.`delete_flag` = 0
  AND r.`level` >= 4
  AND r.`level` <= 6;

-- ── 6) 验证（可选）
-- SELECT id, permission_code, parent_id FROM osh_permission WHERE permission_code LIKE 'exam%' ORDER BY id;
-- SELECT MAX(id) FROM osh_permission;
