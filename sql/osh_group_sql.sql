-- 把权限字符对应的菜单ID分配给角色
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu WHERE perms IN (
                                                'course:create',
                                                'course:update',
                                                'course:delete'
    );

select  *  from osh_user


select * from osh_role_permission
select * from osh_role
select * from osh_permission

a123  AdminA123
normal-test  normal123
c3138969266    Admin123
