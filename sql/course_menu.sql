-- =============================================
-- 课程管理模块 - 系统菜单注册 SQL
-- 适用于 RuoYi 框架 sys_menu 表
-- 执行数据库: backstage (MySQL)
-- 执行时机: 在启动项目前，先执行此 SQL
-- =============================================

-- 1. 一级菜单：课程管理（目录）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('课程管理', 0, 5, 'course', NULL, 1, 0, 'M', '0', '0', '', 'education', 'admin', sysdate(), '', NULL, '课程管理目录');

-- 获取刚插入的一级菜单 ID（后续子菜单的 parent_id）
-- 注意：如果你的 sys_menu 表 menu_id 是自增的，请记住这个 ID
-- 以下假设一级菜单 ID 为 @courseMenuId，实际执行时请替换

SET @courseMenuId = LAST_INSERT_ID();

-- 2. 二级菜单：课程列表（管理页面）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('课程列表', @courseMenuId, 1, 'list', 'course/list/index', 1, 0, 'C', '0', '0', 'system:course:list', 'list', 'admin', sysdate(), '', NULL, '课程列表菜单');

-- 3. 二级菜单：课程播放器（学习页面）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('课程学习', @courseMenuId, 2, 'player', 'course/player/CoursePlayer', 1, 0, 'C', '0', '0', 'system:course:player', 'video', 'admin', sysdate(), '', NULL, '课程播放器页面');

-- 4. 隐藏路由：带参数的课程播放页（从课程列表跳转时使用）
--    hidden 路由不在侧边栏显示，但可以通过 URL 直接访问
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('课程播放', @courseMenuId, 3, 'player/:courseId(\\d+)', 'course/player/CoursePlayer', 1, 1, 'C', '1', '0', 'system:course:player', '#', 'admin', sysdate(), '', NULL, '课程播放详情页（隐藏路由）');

-- 5. 按钮权限：课程管理操作按钮
SET @courseListId = @courseMenuId + 1;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('课程查询', @courseListId, 1, '#', '', 1, 0, 'F', '0', '0', 'system:course:query', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('课程新增', @courseListId, 2, '#', '', 1, 0, 'F', '0', '0', 'system:course:add', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('课程修改', @courseListId, 3, '#', '', 1, 0, 'F', '0', '0', 'system:course:edit', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('课程删除', @courseListId, 4, '#', '', 1, 0, 'F', '0', '0', 'system:course:remove', '#', 'admin', sysdate(), '', NULL, '');

-- =============================================
-- 执行完成后，使用 admin 账号登录系统
-- 侧边栏会出现「课程管理」目录
-- 包含「课程列表」和「课程学习」两个子菜单
-- =============================================
