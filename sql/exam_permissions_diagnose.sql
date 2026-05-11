-- =============================================================================
-- 诊断用：执行后把结果截图或粘贴给开发（不要在生产库频繁跑）
-- =============================================================================
SET NAMES utf8mb4;

-- 1) 父权限 exam 是否存在（没有则题目权限的 parent_id 会为 NULL，应先在 migrate 里补考试模块权限）
SELECT id, permission_name, permission_code, parent_id, delete_flag
FROM osh_permission
WHERE permission_code = 'exam' AND delete_flag = 0;

-- 2) 题目相关权限是否已有
SELECT id, permission_name, permission_code, delete_flag
FROM osh_permission
WHERE permission_code IN (
    'exam:question:save',
    'exam:question:delete',
    'exam:create',
    'exam:update',
    'exam:delete'
)
ORDER BY permission_code;

-- 3) id 85、86 当前被谁占用（若与脚本旧版冲突，会出现「题目权限插不进」）
SELECT id, permission_name, permission_code, delete_flag
FROM osh_permission
WHERE id IN (85, 86);

-- 4) 各角色 level（脚本 B 旧版要求 level 在 4–6 之间才有授权行）
SELECT id, role_name, role_code, `level`, status, delete_flag
FROM osh_role
WHERE delete_flag = 0
ORDER BY id;

-- 5) 预估「按 level 4–6 + 两条题目权限」能生成多少条（应为：有权限行数 × 有角色数）
SELECT COUNT(*) AS pair_count
FROM osh_role r
INNER JOIN osh_permission p
    ON p.permission_code IN ('exam:question:save', 'exam:question:delete')
    AND p.delete_flag = 0
WHERE r.delete_flag = 0
  AND r.`level` >= 4
  AND r.`level` <= 6;

-- 6) 若你使用 migrate_delete_flag 的种子：role_id 5/6/7 与 level 是否一致
SELECT id, role_name, role_code, `level`
FROM osh_role
WHERE id IN (5, 6, 7) AND delete_flag = 0;
