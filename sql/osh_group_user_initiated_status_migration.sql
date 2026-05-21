-- =====================================================
-- 拼团状态码更新迁移脚本
-- 执行日期: 2026-05-11
-- 说明: 将旧的拼团状态码迁移到新的状态码定义
-- =====================================================

-- 新状态码定义：
-- 0：进行中（招募中/未成团）- 当前人数 < 最低成团人数
-- 1：拼团成功（达到最低人数）- 可继续加入
-- 2：拼团完成（同状态1）- 可继续加入（用于区分不同业务场景）
-- 3：拼团结束 - 达到人数上限 OR 服务器剩余时间不足一个月

-- =====================================================
-- 1. 备份当前数据（可选，建议执行）
-- =====================================================
-- CREATE TABLE osh_group_user_initiated_backup_20260511 AS 
-- SELECT * FROM osh_group_user_initiated;

-- =====================================================
-- 2. 状态码迁移逻辑
-- =====================================================

-- 原状态码：
-- 0：招募中 -> 新状态码：0（进行中）
-- 1：已成团 -> 新状态码：1（拼团成功）
-- 2：已取消/过期 -> 新状态码：3（拼团结束）

-- 执行迁移
UPDATE osh_group_user_initiated 
SET group_status = 3, 
    update_time = NOW()
WHERE group_status = 2 
  AND delete_flag = 0;

-- =====================================================
-- 3. 验证迁移结果
-- =====================================================

-- 查看各状态的记录数
SELECT 
    group_status,
    CASE group_status
        WHEN 0 THEN '进行中'
        WHEN 1 THEN '拼团成功'
        WHEN 2 THEN '拼团完成'
        WHEN 3 THEN '拼团结束'
        ELSE '未知'
    END AS status_name,
    COUNT(*) AS record_count
FROM osh_group_user_initiated
WHERE delete_flag = 0
GROUP BY group_status
ORDER BY group_status;

-- =====================================================
-- 4. 检查是否有状态为2的记录（应该为0）
-- =====================================================
SELECT COUNT(*) AS should_be_zero
FROM osh_group_user_initiated
WHERE group_status = 2 
  AND delete_flag = 0;

-- =====================================================
-- 5. 可选：添加状态字段注释（如果数据库支持）
-- =====================================================
-- ALTER TABLE osh_group_user_initiated 
-- MODIFY COLUMN group_status INT NOT NULL DEFAULT 0 
-- COMMENT '拼团状态：0-进行中 1-拼团成功 2-拼团完成 3-拼团结束';

-- =====================================================
-- 完成提示
-- =====================================================
-- 迁移完成！请验证数据是否正确。
-- 如有问题，可从备份表恢复：
-- UPDATE osh_group_user_initiated o
-- INNER JOIN osh_group_user_initiated_backup_20260511 b ON o.id = b.id
-- SET o.group_status = b.group_status, o.update_time = b.update_time;
