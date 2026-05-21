-- =====================================================
-- 发起拼团功能 - 数据库字段扩展
-- 创建日期: 2026-04-30
-- 说明: 为 osh_group_work 表添加发起拼团所需的字段
-- =====================================================

-- 1. 为 osh_group_work 表添加新字段
ALTER TABLE osh_group_work
ADD COLUMN min_num INT NOT NULL DEFAULT 2 COMMENT '最低成团人数' AFTER leader_user_id,
ADD COLUMN max_num INT NOT NULL DEFAULT 5 COMMENT '最多成团人数' AFTER min_num,
ADD COLUMN duration INT NOT NULL DEFAULT 12 COMMENT '服务器使用时长（月）' AFTER max_num,
ADD COLUMN custom_price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '自定义拼团价格' AFTER duration;

-- 2. 为现有数据设置默认值
UPDATE osh_group_work 
SET min_num = 2, 
    max_num = 5, 
    duration = 12 
WHERE min_num IS NULL OR max_num IS NULL OR duration IS NULL;

-- 3. 添加索引优化查询性能
ALTER TABLE osh_group_work
ADD INDEX idx_group_activity_id (group_activity_id),
ADD INDEX idx_user_id (user_id),
ADD INDEX idx_group_status (group_status);

-- 4. 添加注释说明
ALTER TABLE osh_group_work COMMENT = '服务器拼团组团表（支持用户自定义发起拼团）';

-- =====================================================
-- 验证脚本
-- =====================================================

-- 查看表结构
DESC osh_group_work;

-- 查看新增字段
SELECT 
    COLUMN_NAME,
    COLUMN_TYPE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'osh_group_work'
  AND COLUMN_NAME IN ('min_num', 'max_num', 'duration', 'custom_price');

-- =====================================================
-- 回滚脚本（如需回滚请执行）
-- =====================================================

-- ALTER TABLE osh_group_work DROP COLUMN min_num;
-- ALTER TABLE osh_group_work DROP COLUMN max_num;
-- ALTER TABLE osh_group_work DROP COLUMN duration;
-- ALTER TABLE osh_group_work DROP COLUMN custom_price;
-- ALTER TABLE osh_group_work DROP INDEX idx_group_activity_id;
-- ALTER TABLE osh_group_work DROP INDEX idx_user_id;
-- ALTER TABLE osh_group_work DROP INDEX idx_group_status;
