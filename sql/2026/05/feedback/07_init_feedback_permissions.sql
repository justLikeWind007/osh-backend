-- =============================================
-- AI 助手反馈模块 - 后台权限初始化 SQL
-- 适用于 RuoYi 框架 sys_menu、sys_role、sys_role_menu 表
-- 执行数据库: backstage (MySQL)
-- 说明:
-- 1. 反馈管理能力复用现有一体化课程/内容管理入口，不再新增独立反馈后台页面。
-- 2. 本脚本仅补齐 system:feedback:manage 权限资源，便于角色授权与接口鉴权。
-- 3. 后台接口统一通过 @PreAuthorize("@ss.hasPermi('system:feedback:manage')") 生效。
-- =============================================

-- =============================================
-- 第一部分：菜单权限配置（sys_menu）
-- =============================================

-- 1. 复用课程管理菜单，挂载一个按钮权限资源（类型 F）
SET @courseListMenuId = (
    SELECT menu_id
    FROM sys_menu
    WHERE perms = 'system:course:list'
      AND menu_type = 'C'
    LIMIT 1
);

INSERT INTO sys_menu (
    menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type,
    visible, status, perms, icon, create_by, create_time, update_by, update_time, remark
)
SELECT
    '反馈管理', @courseListMenuId, 9, '#', '', 1, 0, 'F',
    '0', '0', 'system:feedback:manage', '#', 'admin', sysdate(), '', NULL, '反馈模块管理能力（复用现有课程/内容管理入口）'
FROM dual
WHERE @courseListMenuId IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM sys_menu
      WHERE perms = 'system:feedback:manage'
        AND menu_type = 'F'
  );

SET @feedbackManageMenuId = (
    SELECT menu_id
    FROM sys_menu
    WHERE perms = 'system:feedback:manage'
      AND menu_type = 'F'
    LIMIT 1
);

-- =============================================
-- 第二部分：角色权限配置（sys_role_menu）
-- =============================================

-- 默认仅为超级管理员角色分配反馈模块全部权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r
JOIN sys_menu m
WHERE r.role_key = 'admin'
  AND m.menu_id = @feedbackManageMenuId
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_menu rm
      WHERE rm.role_id = r.role_id
        AND rm.menu_id = m.menu_id
  );

-- =============================================
-- 第三部分：验证查询语句
-- =============================================

-- 查询反馈管理权限资源
SELECT menu_id, menu_name, parent_id, menu_type, visible, status, perms, order_num
FROM sys_menu
WHERE menu_id = @feedbackManageMenuId
ORDER BY parent_id, order_num, menu_id;

-- 查询超级管理员反馈权限分配结果
SELECT r.role_id, r.role_name, r.role_key, m.menu_id, m.menu_name, m.perms, m.menu_type
FROM sys_role_menu rm
JOIN sys_role r ON rm.role_id = r.role_id
JOIN sys_menu m ON rm.menu_id = m.menu_id
WHERE r.role_key = 'admin'
  AND m.menu_id = @feedbackManageMenuId
ORDER BY m.parent_id, m.order_num, m.menu_id;
