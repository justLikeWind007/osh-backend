-- 为 osh_group_user_initiated 表添加 type 字段
-- 用于按拼团类型筛选

ALTER TABLE osh_group_user_initiated 
ADD COLUMN `type` VARCHAR(30) NOT NULL DEFAULT 'server' COMMENT '拼团类型：server-服务器';

-- 添加索引
CREATE INDEX idx_type ON osh_group_user_initiated(`type`);

-- 将activityId=4的拼团记录状态修改为结束
UPDATE osh_group_user_initiated SET group_status = 2 WHERE activity_id = 4;