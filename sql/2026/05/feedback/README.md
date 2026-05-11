# 反馈系统 SQL 脚本

## 当前口径

- 反馈模块以 `sql/2026/05/feedback/` 目录下脚本作为唯一正式结构来源。
- 最终模型使用 `assistant_feedback.category_id` 关联 `assistant_feedback_category.id`。
- `assistant_feedback_category.code` 是稳定业务编码，用于前后端分类识别。
- `assistant_feedback.type`、`priority` 属于废弃历史设计，不应再作为开发环境初始化入口的一部分。
- `sql/2026/04/mysql/assistant_feedback.sql` 与 `assistant_feedback_ticket_upgrade.sql` 仅保留作历史参考，当前反馈模块不要再执行。

## 📋 脚本说明

| 文件 | 版本 | 说明 | 执行顺序 |
|------|------|------|---------|
| `00_init_feedback_tables.sql` | V1.0 | 创建反馈基础表（分类表、评论表、基础字段） | 0 |
| `01_create_like_favorite_tables.sql` | V1.0.1 | 创建点赞表和收藏表 | 1 |
| `02_add_redundant_fields.sql` | V1.0.2 | 添加冗余字段和索引 | 2 |
| `03_init_redundant_data.sql` | V1.0.3 | 初始化冗余字段（可选） | 3 |
| `04_add_hot_score_field.sql` | V1.1 | 添加热度分字段和新索引 | 4 |
| `05_fix_category_icons.sql` | V1.2 | 修复分类图标为 Emoji | 5 |
| `06_test_data.sql` | Test Data | 测试数据（仅开发/测试环境） | 6 |
| `07_init_feedback_permissions.sql` | V1.3 | 初始化反馈后台管理入口权限（sys_menu / sys_role_menu） | 7 |
| `99_rollback.sql` | - | 回滚脚本（删除所有数据） | - |

---

## 🚀 快速执行

### 方式一：逐个执行（推荐）

```bash
cd /Users/tianyi/devproj/osh-all/osh-backend/sql/2026/05/feedback

# 0. 创建基础表（首次执行必需）
mysql -u root -p backstage < 00_init_feedback_tables.sql

# 1. 创建点赞/收藏表
mysql -u root -p backstage < 01_create_like_favorite_tables.sql

# 2. 添加冗余字段
mysql -u root -p backstage < 02_add_redundant_fields.sql

# 3. 初始化冗余字段（如果有历史数据）
mysql -u root -p backstage < 03_init_redundant_data.sql

# 4. 添加热度分字段
mysql -u root -p backstage < 04_add_hot_score_field.sql

# 5. 修复分类图标
mysql --default-character-set=utf8mb4 -u root -p backstage < 05_fix_category_icons.sql

# 6. 插入测试数据（仅开发/测试环境）
mysql -u root -p backstage < 06_test_data.sql

# 7. 初始化反馈后台管理权限
mysql -u root -p backstage < 07_init_feedback_permissions.sql
```

### 方式二：一键执行（生产环境）

```bash
cd /Users/tianyi/devproj/osh-all/osh-backend/sql/2026/05/feedback

# 仅执行迁移脚本（不包含测试数据）
for file in $(ls 0[0-5]_*.sql 07_*.sql | sort); do
    echo "执行: $file"
    if [[ $file == "05_"* ]]; then
        mysql --default-character-set=utf8mb4 -u root -p backstage < "$file"
    else
        mysql -u root -p backstage < "$file"
    fi
done
```

### 方式三：一键执行（开发/测试环境）

```bash
cd /Users/tianyi/devproj/osh-all/osh-backend/sql/2026/05/feedback

# 执行所有脚本（包含测试数据）
for file in $(ls 0[0-6]_*.sql 07_*.sql | sort); do
    echo "执行: $file"
    if [[ $file == "05_"* ]]; then
        mysql --default-character-set=utf8mb4 -u root -p backstage < "$file"
    else
        mysql -u root -p backstage < "$file"
    fi
done
```

---

## ✅ 验证

```sql
-- 检查表是否创建成功
SHOW TABLES LIKE 'assistant_feedback%';

-- 检查字段是否添加成功
DESC assistant_feedback;
DESC assistant_feedback_like;
DESC assistant_feedback_favorite;
DESC assistant_feedback_category;

-- 检查索引是否创建成功
SHOW INDEX FROM assistant_feedback_like;
SHOW INDEX FROM assistant_feedback_favorite;
SHOW INDEX FROM assistant_feedback WHERE Key_name IN ('idx_hot_score', 'idx_hot_score_v2');

-- 查看数据统计
SELECT 
    COUNT(*) AS total_feedback,
    SUM(like_count) AS total_likes,
    SUM(favorite_count) AS total_favorites,
    AVG(hot_score) AS avg_hot_score,
    MAX(hot_score) AS max_hot_score
FROM assistant_feedback
WHERE delete_flag = 0;

-- 查看分类图标
SELECT code, name, icon, HEX(icon) AS icon_hex
FROM assistant_feedback_category
ORDER BY sort_order;

-- 查看反馈后台权限点
SELECT menu_id, menu_name, menu_type, visible, perms
FROM sys_menu
WHERE perms = 'system:feedback:manage'
   OR path = 'assistant-feedback'
ORDER BY parent_id, order_num, menu_id;
```

---

## ⚠️ 注意事项

1. **执行前备份**：
   ```bash
   mysqldump -u root -p backstage > backup_$(date +%Y%m%d_%H%M%S).sql
   ```

2. **字符集问题**：执行 `05_fix_category_icons.sql` 时必须使用 `--default-character-set=utf8mb4`

3. **数据量大时**：`03_init_redundant_data.sql` 可能执行较慢，建议分批执行

4. **幂等性**：所有脚本支持重复执行（使用 `IF NOT EXISTS`、`IF EXISTS` 等）

5. **回滚操作**：`99_rollback.sql` 会删除所有点赞/收藏数据，请谨慎使用

6. **测试数据**：`06_test_data.sql` 仅用于开发和测试环境，**请勿在生产环境执行**
   - 包含约 22 条虚拟反馈记录
   - 包含约 18 条虚拟评论记录
   - 需要用户ID (1-19) 在系统中存在

7. **权限接入说明**：`07_init_feedback_permissions.sql` 当前会创建“隐藏但启用”的后台菜单资源
   - 目的：先打通 `sys_menu -> sys_role_menu -> @PreAuthorize` 的统一权限链路
   - 原因：反馈后台管理页尚未正式接入 `backstage-ui`，提前展示菜单会形成空路由
   - 前端页面就绪后，可将 `assistant-feedback` 目录和 `system:feedback:manage` 页面菜单的 `visible` 改为 `0`
   - 当前口径仅保留一个后台管理入口权限 `system:feedback:manage`，不拆按钮权限

---

## 🔧 回滚

如需回滚，执行：

```bash
mysql -u root -p backstage < 99_rollback.sql
```

**警告**：此操作会删除所有点赞/收藏数据，且不可恢复！

---

## 📊 表结构

### assistant_feedback_like（点赞表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| feedback_id | BIGINT | 反馈ID |
| user_id | BIGINT | 用户ID |
| create_time | DATETIME | 点赞时间 |

**索引**：
- `uk_feedback_user`（唯一索引）：防止重复点赞
- `idx_user_id`：用户点赞历史查询
- `idx_create_time`：时间范围查询

### assistant_feedback_favorite（收藏表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| feedback_id | BIGINT | 反馈ID |
| user_id | BIGINT | 用户ID |
| create_time | DATETIME | 收藏时间 |

**索引**：
- `uk_feedback_user`（唯一索引）：防止重复收藏
- `idx_user_id`：用户收藏历史查询
- `idx_create_time`：时间范围查询

### assistant_feedback（新增字段）

| 字段 | 类型 | 说明 |
|------|------|------|
| like_count | INT | 点赞数量（冗余字段） |
| favorite_count | INT | 收藏数量（冗余字段） |
| hot_score | INT | 热度分（计算字段） |

**新增索引**：
- `idx_hot_score_v2`：热度排序索引（hot_score DESC, create_time DESC）

### assistant_feedback_category（字段修改）

| 字段 | 类型 | 说明 |
|------|------|------|
| icon | VARCHAR(50) | 分类图标（Emoji，utf8mb4） |

---

## 📞 相关文档

- 数据库设计文档：`../../../../docs/database/`
- 开发规范：`../../../../AGENTS.md`
