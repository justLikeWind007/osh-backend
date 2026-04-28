-- 文件上传权限配置
-- parent_id 使用 200 作为文件上传模块的父ID

-- 1. 文件上传模块父菜单
INSERT INTO backstage.osh_permission(permission_name, permission_code, description, parent_id, `type`, url, `path`, component, sort_order, create_time, create_by, update_time, update_by, delete_flag)
VALUES('文件上传', 'upload:module', '文件上传模块', 0, 1, NULL, '/upload', NULL, 200, '2026-04-18 09:00:00', 'system', '2026-04-18 09:00:00', 'system', 0);

-- 2. 文件上传权限
INSERT INTO backstage.osh_permission(permission_name, permission_code, description, parent_id, `type`, url, `path`, component, sort_order, create_time, create_by, update_time, update_by, delete_flag)
VALUES('上传文件', 'upload:file', '上传图片、视频等文件', 200, 2, '/pc/upload', NULL, NULL, 1, '2026-04-18 09:00:00', 'system', '2026-04-18 09:00:00', 'system', 0);


-- 角色权限关联 - 为所有角色(1-7)分配文件上传权限
-- 注意：需要先执行上面的权限插入语句，然后查询实际的 permission_id 替换下面的 XX 和 YY

-- 方式1：如果你知道插入后的权限ID（推荐）
-- 先执行上面的 INSERT 语句，然后执行：SELECT id, permission_code FROM osh_permission WHERE permission_code IN ('upload:module', 'upload:file');
-- 假设查询结果是：upload:module 的 id=44, upload:file 的 id=45
-- 然后将下面的 44 和 45 替换成实际的 ID

-- 为所有角色分配 upload:module 权限（将 44 替换成实际的 permission_id）
INSERT INTO backstage.osh_role_permission(role_id, permission_id, create_time, create_by, update_time, update_by, delete_flag)
VALUES
(1, 44, '2026-04-18 09:00:00', 'system', '2026-04-18 09:00:00', 'system', 0),
(2, 44, '2026-04-18 09:00:00', 'system', '2026-04-18 09:00:00', 'system', 0),
(3, 44, '2026-04-18 09:00:00', 'system', '2026-04-18 09:00:00', 'system', 0),
(4, 44, '2026-04-18 09:00:00', 'system', '2026-04-18 09:00:00', 'system', 0),
(5, 44, '2026-04-18 09:00:00', 'system', '2026-04-18 09:00:00', 'system', 0),
(6, 44, '2026-04-18 09:00:00', 'system', '2026-04-18 09:00:00', 'system', 0),
(7, 44, '2026-04-18 09:00:00', 'system', '2026-04-18 09:00:00', 'system', 0);

-- 为所有角色分配 upload:file 权限（将 45 替换成实际的 permission_id）
INSERT INTO backstage.osh_role_permission(role_id, permission_id, create_time, create_by, update_time, update_by, delete_flag)
VALUES
(1, 45, '2026-04-18 09:00:00', 'system', '2026-04-18 09:00:00', 'system', 0),
(2, 45, '2026-04-18 09:00:00', 'system', '2026-04-18 09:00:00', 'system', 0),
(3, 45, '2026-04-18 09:00:00', 'system', '2026-04-18 09:00:00', 'system', 0),
(4, 45, '2026-04-18 09:00:00', 'system', '2026-04-18 09:00:00', 'system', 0),
(5, 45, '2026-04-18 09:00:00', 'system', '2026-04-18 09:00:00', 'system', 0),
(6, 45, '2026-04-18 09:00:00', 'system', '2026-04-18 09:00:00', 'system', 0),
(7, 45, '2026-04-18 09:00:00', 'system', '2026-04-18 09:00:00', 'system', 0);
