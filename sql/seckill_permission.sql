-- 秒杀模块权限数据
-- sort_order 规则：300=模块父菜单，301=商品池子菜单，302=活动子菜单，303=用户端子菜单
-- type：1-菜单 2-按钮/API

-- ==================== 1. 秒杀模块父菜单 ====================
INSERT INTO backstage.osh_permission(id, permission_name, permission_code, description, parent_id, `type`, url, `path`, component, sort_order, create_time, create_by, update_time, update_by, delete_flag)
VALUES
(300, '秒杀模块', 'seckill', '秒杀活动管理模块', 0, 1, NULL, '/seckill', NULL, 300, NOW(), 0, NOW(), 0, 0);

-- ==================== 2. 商品池管理（parent_id=300） ====================
INSERT INTO backstage.osh_permission(permission_name, permission_code, description, parent_id, `type`, url, `path`, component, sort_order, create_time, create_by, update_time, update_by, delete_flag)
VALUES
('商品池列表',   'seckill:goods:list',   '查询秒杀商品池列表',     300, 2, '/pc/seckill/goods/list',     NULL, NULL, 1, NOW(), 0, NOW(), 0, 0),
('商品池详情',   'seckill:goods:query',  '查询秒杀商品池详情',     300, 2, '/pc/seckill/goods/detail',   NULL, NULL, 2, NOW(), 0, NOW(), 0, 0),
('添加商品',     'seckill:goods:add',    '添加商品到秒杀商品池',   300, 2, '/pc/seckill/goods/add',      NULL, NULL, 3, NOW(), 0, NOW(), 0, 0),
('修改商品',     'seckill:goods:edit',   '修改秒杀商品信息',       300, 2, '/pc/seckill/goods/update',   NULL, NULL, 4, NOW(), 0, NOW(), 0, 0),
('上下架商品',   'seckill:goods:status', '批量上架/下架秒杀商品',  300, 2, '/pc/seckill/goods/status',   NULL, NULL, 5, NOW(), 0, NOW(), 0, 0),
('删除商品',     'seckill:goods:remove', '批量删除秒杀商品',       300, 2, '/pc/seckill/goods/batch',    NULL, NULL, 6, NOW(), 0, NOW(), 0, 0);

-- ==================== 3. 活动管理（parent_id=300） ====================
INSERT INTO backstage.osh_permission(permission_name, permission_code, description, parent_id, `type`, url, `path`, component, sort_order, create_time, create_by, update_time, update_by, delete_flag)
VALUES
('活动列表',     'seckill:activity:list',   '查询秒杀活动列表',         300, 2, '/pc/seckill/activity/list',    NULL, NULL, 10, NOW(), 0, NOW(), 0, 0),
('活动详情',     'seckill:activity:query',  '查询秒杀活动详情',         300, 2, '/pc/seckill/activity/detail',  NULL, NULL, 11, NOW(), 0, NOW(), 0, 0),
('创建活动',     'seckill:activity:add',    '创建秒杀活动',             300, 2, '/pc/seckill/activity/add',     NULL, NULL, 12, NOW(), 0, NOW(), 0, 0),
('修改活动',     'seckill:activity:edit',   '修改秒杀活动',             300, 2, '/pc/seckill/activity/update',  NULL, NULL, 13, NOW(), 0, NOW(), 0, 0),
('发布下架活动', 'seckill:activity:status', '发布或下架秒杀活动',       300, 2, '/pc/seckill/activity/status',  NULL, NULL, 14, NOW(), 0, NOW(), 0, 0),
('删除活动',     'seckill:activity:remove', '批量删除秒杀活动',         300, 2, '/pc/seckill/activity/batch',   NULL, NULL, 15, NOW(), 0, NOW(), 0, 0),
('订单列表',     'seckill:order:list',      '查询秒杀订单列表（管理端）', 300, 2, '/pc/seckill/activity/order/list', NULL, NULL, 16, NOW(), 0, NOW(), 0, 0);

-- ==================== 4. 用户端（parent_id=300，普通用户权限） ====================
INSERT INTO backstage.osh_permission(permission_name, permission_code, description, parent_id, `type`, url, `path`, component, sort_order, create_time, create_by, update_time, update_by, delete_flag)
VALUES
('秒杀活动列表', 'seckill:user:list',   '用户端查询进行中的活动列表', 300, 2, '/pc/seckill/user/activity/list',   NULL, NULL, 20, NOW(), 0, NOW(), 0, 0),
('秒杀活动详情', 'seckill:user:detail', '用户端查询活动详情',         300, 2, '/pc/seckill/user/activity/detail', NULL, NULL, 21, NOW(), 0, NOW(), 0, 0),
('执行秒杀',     'seckill:user:do',     '用户执行秒杀',               300, 2, '/pc/seckill/user/do',              NULL, NULL, 22, NOW(), 0, NOW(), 0, 0),
('查询秒杀结果', 'seckill:user:result', '用户查询秒杀结果（轮询）',   300, 2, '/pc/seckill/user/order/result',    NULL, NULL, 23, NOW(), 0, NOW(), 0, 0),
('取消秒杀订单', 'seckill:user:cancel', '用户取消秒杀订单',           300, 2, '/pc/seckill/user/order/cancel',    NULL, NULL, 24, NOW(), 0, NOW(), 0, 0);
