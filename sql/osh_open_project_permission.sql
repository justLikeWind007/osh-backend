-- 开源项目模块权限数据
-- id 从 400 开始，避免与其他模块冲突
-- 权限前缀：op（openproject）
-- 所有接口均需登录，无匿名访问
-- 角色说明（参考 osh_role 表）：
--   1-普通用户  2-小班用户  3-普通开发者  4-VIP用户/普通管理员
--   5-核心开发者  6-创始人  7-超级管理员

-- ─── 1. 插入权限记录（共 8 条：1 个模块菜单 + 7 个接口权限）─────────────────

INSERT INTO `osh_permission`
    (`id`, `permission_name`, `permission_code`, `description`, `parent_id`, `type`, `url`, `path`, `component`, `sort_order`, `create_time`, `create_by`, `update_time`, `update_by`, `delete_flag`)
VALUES
-- 开源项目模块（菜单，顶级）
(400, '开源项目模块', 'op',          '开源项目模块',       0,   1, NULL,                        '/openproject', NULL, 300, '2026-05-07 09:00:00', 'system', '2026-05-07 09:00:00', 'system', 0),
-- 接口权限（parent_id=400）
(401, '项目列表',     'op:list',     '分页查询已通过项目', 400, 2, '/pc/openproject/list',      NULL, NULL, 1, '2026-05-07 09:00:00', 'system', '2026-05-07 09:00:00', 'system', 0),
(402, '项目详情',     'op:detail',   '查询项目详情',       400, 2, '/pc/openproject/detail/{id}',NULL,NULL, 2, '2026-05-07 09:00:00', 'system', '2026-05-07 09:00:00', 'system', 0),
(403, '标签列表',     'op:tags',     '查询项目标签列表',   400, 2, '/pc/openproject/tags',      NULL, NULL, 3, '2026-05-07 09:00:00', 'system', '2026-05-07 09:00:00', 'system', 0),
(404, '点击计数',     'op:click',    '增加项目点击次数',   400, 2, '/pc/openproject/click',     NULL, NULL, 4, '2026-05-07 09:00:00', 'system', '2026-05-07 09:00:00', 'system', 0),
(405, '提交项目',     'op:submit',   '用户提交开源项目',   400, 2, '/pc/openproject/submit',    NULL, NULL, 5, '2026-05-07 09:00:00', 'system', '2026-05-07 09:00:00', 'system', 0),
(406, '审核项目',     'op:audit',    '管理员审核开源项目', 400, 2, '/pc/openproject/audit',     NULL, NULL, 6, '2026-05-07 09:00:00', 'system', '2026-05-07 09:00:00', 'system', 0),
(407, '待审核列表',   'op:pending',  '查询待审核项目列表', 400, 2, '/pc/openproject/pending',   NULL, NULL, 7, '2026-05-07 09:00:00', 'system', '2026-05-07 09:00:00', 'system', 0);

-- ─── 2. 角色-权限关联 ─────────────────────────────────────────────────────────
-- op:list / op:detail / op:tags / op:click / op:submit：所有角色（1~7）
-- op:audit / op:pending：仅高级角色（4-VIP/管理员、5-核心开发者、6-创始人、7-超级管理员）

INSERT INTO `osh_role_permission` (`role_id`, `permission_id`, `create_by`, `update_by`)
VALUES
-- 模块菜单：所有角色
(1,400,'system','system'),(2,400,'system','system'),(3,400,'system','system'),
(4,400,'system','system'),(5,400,'system','system'),(6,400,'system','system'),(7,400,'system','system'),
-- op:list：所有角色
(1,401,'system','system'),(2,401,'system','system'),(3,401,'system','system'),
(4,401,'system','system'),(5,401,'system','system'),(6,401,'system','system'),(7,401,'system','system'),
-- op:detail：所有角色
(1,402,'system','system'),(2,402,'system','system'),(3,402,'system','system'),
(4,402,'system','system'),(5,402,'system','system'),(6,402,'system','system'),(7,402,'system','system'),
-- op:tags：所有角色
(1,403,'system','system'),(2,403,'system','system'),(3,403,'system','system'),
(4,403,'system','system'),(5,403,'system','system'),(6,403,'system','system'),(7,403,'system','system'),
-- op:click：所有角色
(1,404,'system','system'),(2,404,'system','system'),(3,404,'system','system'),
(4,404,'system','system'),(5,404,'system','system'),(6,404,'system','system'),(7,404,'system','system'),
-- op:submit：角色 4、5、6、7
(4,405,'system','system'),(5,405,'system','system'),(6,405,'system','system'),(7,405,'system','system'),
-- op:audit：角色 4、5、6、7
(4,406,'system','system'),(5,406,'system','system'),(6,406,'system','system'),(7,406,'system','system'),
-- op:pending：角色 4、5、6、7
(4,407,'system','system'),(5,407,'system','system'),(6,407,'system','system'),(7,407,'system','system');
