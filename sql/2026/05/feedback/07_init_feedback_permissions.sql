-- =============================================
-- AI 助手反馈模块 - 后台权限初始化 SQL
-- 适用于 RuoYi 框架 sys_menu、sys_role、sys_role_menu 表
-- 执行数据库: backstage (MySQL)
-- 说明:
-- 1. 当前反馈后台管理页尚未正式接入 backstage-ui，本脚本先创建“隐藏但启用”的菜单资源。
-- 2. 目录/页面菜单默认 visible='1'，前端页面就绪后可改为 visible='0' 正式展示。
-- 3. 后台接口权限统一使用 system:feedback:* 权限码，并通过 @PreAuthorize("@ss.hasPermi(...)") 生效。
-- =============================================

-- =============================================
-- 第一部分：菜单权限配置（sys_menu）
-- =============================================

-- 1. 一级目录：反馈管理（目录类型 M，默认隐藏）
INSERT INTO sys_menu (
    menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type,
    visible, status, perms, icon, create_by, create_time, update_by, update_time, remark
)
SELECT
    '反馈管理', 0, 8, 'assistant-feedback', NULL, 1, 0, 'M',
    '1', '0', '', 'message', 'admin', sysdate(), '', NULL, '反馈模块后台管理目录（前端页面接入前默认隐藏）'
FROM dual
WHERE NOT EXISTS (
    SELECT 1
    FROM sys_menu
    WHERE parent_id = 0
      AND path = 'assistant-feedback'
      AND menu_type = 'M'
);

SET @feedbackDirMenuId = (
    SELECT menu_id
    FROM sys_menu
    WHERE parent_id = 0
      AND path = 'assistant-feedback'
      AND menu_type = 'M'
    LIMIT 1
);

-- 2. 二级菜单：反馈管理入口（菜单类型 C，默认隐藏）
INSERT INTO sys_menu (
    menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type,
    visible, status, perms, icon, create_by, create_time, update_by, update_time, remark
)
SELECT
    '反馈管理', @feedbackDirMenuId, 1, 'manage', 'assistant/feedback/index', 1, 0, 'C',
    '1', '0', 'system:feedback:manage', 'list', 'admin', sysdate(), '', NULL, '反馈模块后台管理入口（前端页面接入前默认隐藏）'
FROM dual
WHERE @feedbackDirMenuId IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM sys_menu
      WHERE perms = 'system:feedback:manage'
        AND menu_type = 'C'
  );

SET @feedbackListMenuId = (
    SELECT menu_id
    FROM sys_menu
    WHERE perms = 'system:feedback:manage'
      AND menu_type = 'C'
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
  AND (
      m.menu_id = @feedbackDirMenuId
      OR m.menu_id = @feedbackListMenuId
  )
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_menu rm
      WHERE rm.role_id = r.role_id
        AND rm.menu_id = m.menu_id
  );

-- =============================================
-- 第三部分：验证查询语句
-- =============================================

-- 查询反馈模块目录与管理入口
SELECT menu_id, menu_name, parent_id, menu_type, visible, status, perms, order_num
FROM sys_menu
WHERE menu_id = @feedbackDirMenuId
   OR menu_id = @feedbackListMenuId
ORDER BY parent_id, order_num, menu_id;

-- 查询超级管理员反馈权限分配结果
SELECT r.role_id, r.role_name, r.role_key, m.menu_id, m.menu_name, m.perms, m.menu_type
FROM sys_role_menu rm
JOIN sys_role r ON rm.role_id = r.role_id
JOIN sys_menu m ON rm.menu_id = m.menu_id
WHERE r.role_key = 'admin'
  AND (
      m.menu_id = @feedbackDirMenuId
      OR m.menu_id = @feedbackListMenuId
  )
ORDER BY m.parent_id, m.order_num, m.menu_id;

-- 前端页面接入完成后，可执行以下语句正式展示菜单
-- UPDATE sys_menu SET visible = '0' WHERE menu_id IN (@feedbackDirMenuId, @feedbackListMenuId);
