-- 电子书模块权限配置
-- parent_id 使用 100 作为电子书模块的父ID

-- 1. 电子书模块父菜单
INSERT INTO backstage.osh_permission(permission_name, permission_code, description, parent_id, `type`, url, `path`, component, sort_order, create_time, create_by, update_time, update_by, delete_flag)
VALUES('电子书管理', 'book:module', '电子书模块', 0, 1, NULL, '/book', NULL, 100, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0);

-- 2. 电子书列表查询
INSERT INTO backstage.osh_permission(permission_name, permission_code, description, parent_id, `type`, url, `path`, component, sort_order, create_time, create_by, update_time, update_by, delete_flag)
VALUES('电子书列表', 'book:list', '查询电子书列表', 100, 2, '/pc/book/page', NULL, NULL, 1, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0);

-- 3. 电子书详情查询
INSERT INTO backstage.osh_permission(permission_name, permission_code, description, parent_id, `type`, url, `path`, component, sort_order, create_time, create_by, update_time, update_by, delete_flag)
VALUES('电子书详情', 'book:detail', '查询电子书详情', 100, 2, '/pc/book/getById', NULL, NULL, 2, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0);

-- 4. 新增电子书
INSERT INTO backstage.osh_permission(permission_name, permission_code, description, parent_id, `type`, url, `path`, component, sort_order, create_time, create_by, update_time, update_by, delete_flag)
VALUES('新增电子书', 'book:create', '创建新电子书', 100, 2, '/pc/book/create', NULL, NULL, 3, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0);

-- 5. 修改电子书
INSERT INTO backstage.osh_permission(permission_name, permission_code, description, parent_id, `type`, url, `path`, component, sort_order, create_time, create_by, update_time, update_by, delete_flag)
VALUES('修改电子书', 'book:update', '更新电子书信息', 100, 2, '/pc/book/update', NULL, NULL, 4, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0);

-- 6. 删除电子书
INSERT INTO backstage.osh_permission(permission_name, permission_code, description, parent_id, `type`, url, `path`, component, sort_order, create_time, create_by, update_time, update_by, delete_flag)
VALUES('删除电子书', 'book:delete', '删除电子书', 100, 2, '/pc/book/delete', NULL, NULL, 5, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0);

-- 7. 电子书章节详情
INSERT INTO backstage.osh_permission(permission_name, permission_code, description, parent_id, `type`, url, `path`, component, sort_order, create_time, create_by, update_time, update_by, delete_flag)
VALUES('章节详情', 'book:chapter:detail', '查询章节内容', 100, 2, '/pc/book/detail', NULL, NULL, 6, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0);

-- 8. 电子书章节菜单
INSERT INTO backstage.osh_permission(permission_name, permission_code, description, parent_id, `type`, url, `path`, component, sort_order, create_time, create_by, update_time, update_by, delete_flag)
VALUES('章节菜单', 'book:chapter:menus', '查询章节菜单', 100, 2, '/pc/book/menus', NULL, NULL, 7, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0);

-- 9. 新增章节
INSERT INTO backstage.osh_permission(permission_name, permission_code, description, parent_id, `type`, url, `path`, component, sort_order, create_time, create_by, update_time, update_by, delete_flag)
VALUES('新增章节', 'book:chapter:create', '创建新章节', 100, 2, '/pc/book/chapter/create', NULL, NULL, 8, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0);

-- 10. 修改章节
INSERT INTO backstage.osh_permission(permission_name, permission_code, description, parent_id, `type`, url, `path`, component, sort_order, create_time, create_by, update_time, update_by, delete_flag)
VALUES('修改章节', 'book:chapter:update', '更新章节内容', 100, 2, '/pc/book/chapter/update', NULL, NULL, 9, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0);

-- 11. 电子书标签列表
INSERT INTO backstage.osh_permission(permission_name, permission_code, description, parent_id, `type`, url, `path`, component, sort_order, create_time, create_by, update_time, update_by, delete_flag)
VALUES('标签列表', 'book:tag:list', '查询电子书标签', 100, 2, '/pc/book/getTagList', NULL, NULL, 10, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0);

-- 12. 筛选电子书列表
INSERT INTO backstage.osh_permission(permission_name, permission_code, description, parent_id, `type`, url, `path`, component, sort_order, create_time, create_by, update_time, update_by, delete_flag)
VALUES('筛选电子书', 'book:filter', '按条件筛选电子书', 100, 2, '/pc/book/getFilterBookList', NULL, NULL, 11, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0);

-- 13. 我的电子书列表
INSERT INTO backstage.osh_permission(permission_name, permission_code, description, parent_id, `type`, url, `path`, component, sort_order, create_time, create_by, update_time, update_by, delete_flag)
VALUES('我的电子书', 'book:my:list', '查询我购买的电子书', 100, 2, '/pc/mybook', NULL, NULL, 12, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0);


-- 角色权限关联 - 为所有角色(1-7)分配电子书模块权限(15-26)
INSERT INTO backstage.osh_role_permission(role_id, permission_id, create_time, create_by, update_time, update_by, delete_flag)
VALUES
(1, 15, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(1, 16, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(1, 17, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(1, 18, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(1, 19, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(1, 20, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(1, 21, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(1, 22, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(1, 23, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(1, 24, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(1, 25, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(1, 26, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(2, 15, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(2, 16, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(2, 17, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(2, 18, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(2, 19, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(2, 20, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(2, 21, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(2, 22, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(2, 23, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(2, 24, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(2, 25, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(2, 26, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(3, 15, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(3, 16, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(3, 17, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(3, 18, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(3, 19, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(3, 20, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(3, 21, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(3, 22, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(3, 23, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(3, 24, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(3, 25, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(3, 26, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(4, 15, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(4, 16, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(4, 17, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(4, 18, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(4, 19, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(4, 20, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(4, 21, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(4, 22, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(4, 23, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(4, 24, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(4, 25, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(4, 26, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(5, 15, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(5, 16, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(5, 17, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(5, 18, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(5, 19, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(5, 20, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(5, 21, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(5, 22, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(5, 23, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(5, 24, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(5, 25, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(5, 26, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(6, 15, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(6, 16, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(6, 17, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(6, 18, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(6, 19, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(6, 20, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(6, 21, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(6, 22, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(6, 23, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(6, 24, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(6, 25, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(6, 26, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(7, 15, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(7, 16, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(7, 17, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(7, 18, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(7, 19, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(7, 20, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(7, 21, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(7, 22, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(7, 23, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(7, 24, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(7, 25, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(7, 26, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(1, 27, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(2, 27, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(3, 27, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(4, 27, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(5, 27, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(6, 27, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0),
(7, 27, '2026-04-17 09:00:00', 'system', '2026-04-17 09:00:00', 'system', 0);
