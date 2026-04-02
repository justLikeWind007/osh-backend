-- =============================================
-- 课程管理模块 - 完整权限配置 SQL
-- 适用于 RuoYi 框架 sys_menu、sys_role、sys_user_role 表
-- 执行数据库: backstage (MySQL)
-- =============================================

-- =============================================
-- 第一部分：菜单权限配置（sys_menu）
-- =============================================

-- 1. 一级菜单：课程管理（目录类型 M）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('课程管理', 0, 5, 'course', NULL, 1, 0, 'M', '0', '0', '', 'education', 'admin', sysdate(), '', NULL, '课程管理目录');

-- 获取课程管理菜单ID
SET @courseMenuId = LAST_INSERT_ID();

-- 2. 二级菜单：课程列表（目录类型 C）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('课程列表', @courseMenuId, 1, 'list', 'course/list/index', 1, 0, 'C', '0', '0', 'system:course:list', 'list', 'admin', sysdate(), '', NULL, '课程列表菜单');

-- 获取课程列表菜单ID
SET @courseListId = LAST_INSERT_ID();

-- 3. 二级菜单：课程播放器（目录类型 C）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('课程学习', @courseMenuId, 2, 'player', 'course/player/CoursePlayer', 1, 0, 'C', '0', '0', 'system:course:player', 'video', 'admin', sysdate(), '', NULL, '课程播放器页面');

-- 4. 按钮权限：课程操作按钮（类型 F）

-- 4.1 课程查询
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('课程查询', @courseListId, 1, '', '', 1, 0, 'F', '0', '0', 'system:course:query', '#', 'admin', sysdate(), '', NULL, '');

-- 4.2 课程新增
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('课程新增', @courseListId, 2, '', '', 1, 0, 'F', '0', '0', 'system:course:add', '#', 'admin', sysdate(), '', NULL, '');

-- 4.3 课程修改
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('课程修改', @courseListId, 3, '', '', 1, 0, 'F', '0', '0', 'system:course:edit', '#', 'admin', sysdate(), '', NULL, '');

-- 4.4 课程删除
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('课程删除', @courseListId, 4, '', '', 1, 0, 'F', '0', '0', 'system:course:remove', '#', 'admin', sysdate(), '', NULL, '');

-- 4.5 上传资料
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('资料上传', @courseListId, 5, '', '', 1, 0, 'F', '0', '0', 'system:course:material:upload', '#', 'admin', sysdate(), '', NULL, '');

-- 4.6 删除资料
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('资料删除', @courseListId, 6, '', '', 1, 0, 'F', '0', '0', 'system:course:material:delete', '#', 'admin', sysdate(), '', NULL, '');

-- 4.7 回答问题
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('问题回答', @courseListId, 7, '', '', 1, 0, 'F', '0', '0', 'system:course:question:answer', '#', 'admin', sysdate(), '', NULL, '');

-- 4.8 审核服务人员
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('服务人员审核', @courseListId, 8, '', '', 1, 0, 'F', '0', '0', 'system:course:staff:audit', '#', 'admin', sysdate(), '', NULL, '');


-- =============================================
-- 第二部分：角色权限配置（sys_role_menu）
-- =============================================

-- 准备：查询角色ID
-- 注意：以下假设角色ID，实际执行时根据数据库中角色表数据调整

-- 假设 role_id = 1 是超级管理员（admin）
-- 假设 role_id = 2 是普通管理员（common）
-- 假设 role_id = 3 是课程服务人员（staff）

-- 1. 为超级管理员（admin, role_id=1）分配所有课程模块权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu WHERE perms IN (
    'system:course:list',
    'system:course:player',
    'system:course:query',
    'system:course:add',
    'system:course:edit',
    'system:course:remove',
    'system:course:material:upload',
    'system:course:material:delete',
    'system:course:question:answer',
    'system:course:staff:audit'
) OR (parent_id = @courseMenuId);

-- 2. 为普通管理员（common, role_id=2）分配除审核外的所有权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 2, menu_id FROM sys_menu WHERE perms IN (
    'system:course:list',
    'system:course:player',
    'system:course:query',
    'system:course:add',
    'system:course:edit',
    'system:course:remove',
    'system:course:material:upload',
    'system:course:material:delete',
    'system:course:question:answer'
) OR (parent_id = @courseMenuId AND menu_id != (SELECT menu_id FROM sys_menu WHERE perms = 'system:course:staff:audit'));

-- 3. 为课程服务人员（staff, role_id=3）仅分配回答问题权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 3, menu_id FROM sys_menu WHERE perms = 'system:course:question:answer';


-- =============================================
-- 第三部分：用户角色配置（sys_user_role）
-- =============================================

-- 注意：以下 user_id 仅为示例，实际执行时根据数据库中用户表数据调整

-- 1. 将 admin 用户（user_id=1）分配给超级管理员角色（role_id=1）
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 2. 将用户2（user_id=2）分配给普通管理员角色（role_id=2）
-- INSERT INTO sys_user_role (user_id, role_id) VALUES (2, 2);

-- 3. 将用户3（user_id=3）分配给课程服务人员角色（role_id=3）
-- INSERT INTO sys_user_role (user_id, role_id) VALUES (3, 3);

-- 4. 批量分配示例：为多个普通管理员分配权限
-- INSERT INTO sys_user_role (user_id, role_id)
-- SELECT user_id, 2 FROM sys_user WHERE dept_id = 某个部门;


-- =============================================
-- 第四部分：验证查询语句
-- =============================================

-- 查询课程管理菜单及其子菜单
SELECT menu_id, menu_name, parent_id, menu_type, perms, order_num
FROM sys_menu
WHERE menu_id = @courseMenuId OR parent_id = @courseMenuId
ORDER BY parent_id, order_num;

-- 查询所有课程相关权限
SELECT menu_id, menu_name, perms, menu_type
FROM sys_menu
WHERE perms LIKE 'system:course%'
ORDER BY menu_type, order_num;

-- 查询角色权限分配情况
SELECT r.role_id, r.role_name, m.menu_id, m.menu_name, m.perms
FROM sys_role_menu rm
JOIN sys_role r ON rm.role_id = r.role_id
JOIN sys_menu m ON rm.menu_id = m.menu_id
WHERE m.perms LIKE 'system:course%'
ORDER BY r.role_id, m.order_num;

-- 查询用户角色分配情况
SELECT u.user_id, u.user_name, r.role_id, r.role_name
FROM sys_user_role ur
JOIN sys_user u ON ur.user_id = u.user_id
JOIN sys_role r ON ur.role_id = r.role_id
ORDER BY u.user_id, r.role_id;