-- 给 osh_user_role 表添加有效期字段
ALTER TABLE `osh_user_role` ADD COLUMN `expire_time` datetime DEFAULT NULL COMMENT '角色过期时间，NULL表示永久有效' AFTER `role_id`;
