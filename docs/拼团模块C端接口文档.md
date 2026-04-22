# 拼团模块 功能接口文档

**基础路径**: `/pc/group`、`/pc/order/group`

## 一、查询拼团列表接口

- **请求方式**: GET
- **完整路径**: `/pc/group/activity/list`
- **认证要求**: 匿名可访问（`@Anonymous`）
- **接口说明**: 查询正在进行中的服务器拼团活动列表，按 sort_order 降序排列

**业务规则说明：**
1. **参团条件**：人数未达上限(`current_num < group_max_num`) 且 距离服务器结束时间 > 1个月
2. **动态定价**：参团费用按剩余月数比例计算，公式：`实际费用 = 基础拼团价 × (剩余月数 / 总月数)`
3. **自动续团**：定时任务检测，满足任一条件自动创建新拼团：
   - 人数达到上限(`current_num >= group_max_num`)
   - 距离服务器结束时间 ≤ 1个月
4. **资源配置**：服务器CPU、内存、存储等配置固定，由系统自动分配，用户不可选择 
 
2. Query Parameters:

| 参数名   | 类型    | 必填 | 说明                                                                 
| -------- | ------- | ---- | ----------------------------------
| type     | String  | 否   | 拼团类型筛选：`server`-服务器
| page     | Integer | 否   | 页码，默认 1                                                         
| pageSize | Integer | 否   | 每页条数，默认 10                                                   

3. 响应示例

**成功响应:**
```json
{
  "total": 12,
  "rows": [
    {
      "id": 1,
      "title": "高性能服务器5人拼团",
      "cpu": "4核",
      "memory": "8GB",
      "storage": "100GB SSD",
      "price": 199.00,
      "current_price": 165.83,
      "total_duration": 12,
      "remaining_months": null,
      "start_time": "2026-04-01 00:00:00",
      "end_time": "2027-04-01 00:00:00",
      "server_start_time": null,
      "server_end_time": null,
      "status": 1,
      "current_num": 3,
      "group_min_num": 2,
      "group_max_num": 5,
      "is_success": false,
      "can_join": true,
      "server_tutorial_url": "/tutorial/server-config"
    }
  ],
  "code": 200,
  "msg": "查询成功"
}
```

### 4. 响应字段说明

| 字段名              | 类型       | 说明                                      |
| ------------------- | ---------- | ----------------------------------------- |
| id                  | Long       | 拼团ID                                    |
| title               | String     | 拼团标题，如"高性能服务器5人拼团"        |
| cpu                 | String     | 服务器CPU配置，如"4核"                   |
| memory              | String     | 服务器内存配置，如"8GB"                  |
| storage             | String     | 服务器存储配置，如"100GB SSD"            |
| base_price          | BigDecimal | 拼团价格（按月计算，完整周期价格）        |
| current_price       | BigDecimal | 当前参团实际价格（按剩余月数比例计算）    |
| total_duration      | Integer    | 服务器总使用时长（月）                    |
| remaining_months    | BigDecimal | 剩余可使用月数（成团后动态计算，未成团为null） |
| start_time          | String     | 拼团开始时间，格式：yyyy-MM-dd HH:mm:ss |
| server_start_time   | String     | 服务器开始使用时间（成团后有值，未成团为null） |
| server_end_time     | String     | 服务器使用结束时间（成团后有值，未成团为null） |
| status              | Integer    | 活动状态： 1-进行中 2-拼团成功（达到最低人数，可继续加入） 3-结束：已达到人数上限 or 服务器使用时间不足一个月 |
| current_num         | Integer    | 当前已参团人数                            |
| group_min_num       | Integer    | 拼团所需最低人数                          |
| group_max_num       | Integer    | 拼团人数上限                              |
| is_success          | Boolean    | 是否已成团（current_num >= group_min_num）|
| can_join            | Boolean    | 当前是否可参团（综合人数、时间判断）      |
| server_tutorial_url | String     | 服务器配置操作教程链接（成团后可访问）    |

### 5. 业务逻辑

```
1. 查询条件：status IN (1, 2)（进中或 已 拼团成功 未满员的）
2. 拼团成功判定：current_num >= group_min_num 时，is_success = true
3. 服务器时间字段逻辑：
   - 未成团时（is_success = false）：
     * server_start_time = null
     * server_end_time = null
     * remaining_months = null
   - 已成团时（is_success = true）：
     * server_start_time = 成团时间（达到最低人数的时间）
     * server_end_time = server_start_time + total_duration 月
     * remaining_months = (server_end_time - NOW()) / 30
4. 计算当前价格：current_price = base_price × (remaining_months / total_duration)
   - 未成团时：按预计服务器使用时长计算（预售阶段，使用total_duration）
   - 已成团时：按server_end_time计算剩余时间
5. 判断是否可参团：
   - can_join = (status = 1 或 status = 2)
   - 即：只要状态是「进行中」或「拼团成功」，就可以参团
   - 当 status = 3（已结束）时，can_join = false
6. 按 sort_order DESC, created_time DESC 排序
7. 分页返回，使用 PageUtils 分页，限制最大 pageSize=50
8. 服务器配置信息直接从活动表读取（固定配置）
```

### 6. 状态流转规则（重要）

**状态变更触发机制：**

```
1. 用户参团时更新状态：
   - 触发时机：每次用户成功参团后
   - 判断条件：current_num >= group_max_num
   - 操作：UPDATE status = 3 WHERE id = #{activityId}
   - 说明：人数达到上限，立即标记为已结束

2. 定时任务更新状态：
   - 执行频率：每天凌晨2点（可配置）
   - 判断条件：已成团（status = 2）AND remaining_months <= 1
   - 操作：UPDATE status = 3 WHERE status = 2 AND (server_end_time - NOW()) / 30 <= 1
   - 说明：服务器剩余使用时间不足一个月，标记为已结束

3. 状态流转图：
   创建(0-草稿) → 上架(1-进行中) → 达到最低人数 → 拼团成功(2)
                                              ↓
                                    满足以下任一条件：
                                    - 人数达到上限（用户参团时触发）
                                    - 服务器剩余时间 ≤ 1个月（定时任务触发）
                                              ↓
                                        已结束(3)
```
```
## 二、查询我的拼团列表接口

- **请求方式**: GET
- **完整路径**: `/pc/group/activity/mylist`
- **认证要求**: **需要登录**（需传 token）
- **接口说明**: 查询当前用户参与的服务器拼团活动列表，包含参团状态、服务器信息、订单信息


 2. Query Parameters:

| 参数名   | 类型    | 必填 | 说明                                                                 
| -------- | ------- | ---- | -------------------------------------------------------------------
| status   | Integer | 否   | 筛选状态：0-进行中 1-已成团 2-已取消/过期，不传则返回全部
| page     | Integer | 否   | 页码，默认 1，最大 100                                                         
| pageSize | Integer | 否   | 每页条数，默认 10，最大 50                                                   

3. 响应示例

**成功响应:**
```json
{
  "total": 12,
  "rows": [
    {
      "activity_id": 1,
      "group_work_id": 1001,
      "title": "高性能服务器5人拼团",
      "cpu": "4核",
      "memory": "8GB",
      "storage": "100GB SSD",
      "base_price": 199.00,
      "actual_price": 165.83,
      "total_duration": 12,
      "start_time": "2026-04-01 00:00:00",
      "end_time": "2027-04-01 00:00:00",
      "server_start_time": "2026-04-15 10:30:00",
      "server_expire_time": "2027-04-15 10:30:00",
      "group_status": 1,
      "group_status_text": "已成团",
      "current_num": 5,
      "group_max_num": 5,
      "is_leader": false,
      "join_time": "2026-04-10 15:30:00",
      "order_no": "GRP20260410001",
      "order_status": "success",
      "server_ip": "192.168.1.100",
      "server_account": "user001",
      "server_password": "******",
      "server_tutorial_url": "/tutorial/server-config",
      "wechat_phone": "13800138000"
    }
  ],
  "code": 200,
  "msg": "查询成功"
}
```



### 4. 响应字段说明

| 字段名            | 类型       | 说明                                       |
| ----------------- | ---------- | ------------------------------------------ |
| activity_id       | Long       | 拼团活动 ID                                 |
| group_work_id     | Long       | 组团记录 ID（用户参团记录）                 |
| title             | String     | 拼团活动标题                                |
| cpu               | String     | 服务器CPU配置                               |
| memory            | String     | 服务器内存配置                              |
| storage           | String     | 服务器存储配置                              |
| base_price        | BigDecimal | 基础拼团价格（完整周期）                    |
| actual_price      | BigDecimal | 用户实际支付价格                            |
| total_duration    | Integer    | 服务器总使用时长（月）                      |
| start_time        | String     | 拼团活动开始时间                            |
| server_start_time | String     | 服务器开始使用时间（成团后有值，未成团null）|
| server_expire_time| String     | 服务器使用到期时间（成团后有值，未成团null）|
| group_status      | Integer    | 组团状态：0-进行中 1-已成团 2-已取消/过期   |
| group_status_text | String     | 组团状态文字描述                            |
| current_num       | Integer    | 当前已参团人数                              |
| group_max_num     | Integer    | 拼团人数上限                                |
| is_leader         | Boolean    | 是否团长（服务器拼团无团长概念，固定false） |
| join_time         | String     | 用户参团时间                                |
| order_no          | String     | 订单编号                                    |
| order_status      | String     | 订单状态：pending/success/refunded/cancel   |
| server_ip         | String     | 服务器IP地址（仅成团后返回）                |
| server_account    | String     | 服务器登录账号（仅成团后返回）              |
| server_password   | String     | 服务器登录密码（脱敏显示，仅成团后返回）    |
| server_tutorial_url | String   | 服务器配置操作教程链接                      |
| wechat_phone      | String     | 服务器负责人微信号（仅成团后返回）          ||

### 5. 业务逻辑

```
1. 鉴权：校验 token，获取 userId
2. 查询当前用户的参团记录：
   SELECT * FROM osh_group_user WHERE user_id = #{userId}
3. 关联查询拼团活动和组团信息：
   LEFT JOIN osh_group_work gw ON gu.group_work_id = gw.id
   LEFT JOIN osh_group_activity ga ON gw.group_activity_id = ga.id
   LEFT JOIN osh_group_order go ON gu.order_id = go.id
4. 如传入 status 参数，追加 gw.status = #{status} 筛选条件
5. 按 gu.join_time DESC 排序
6. 成团后才返回服务器连接信息（server_ip、server_account、server_password）
7. 密码字段需要脱敏处理，显示为 "******"
8. 分页返回，使用 PageUtils 分页
```

## 三、拼团详情接口

### 1. 接口信息

- **请求方式**: GET
- **完整路径**: `/pc/group/work/detail`
- **认证要求**: 匿名可访问（已登录用户返回更多交互信息）
- **接口说明**: 查询单个拼团组团的详细信息，包含参团用户列表、组团进度、服务器配置信息

**业务规则：**
- 拼团成功后（group_status=1），展示服务器配置信息和操作教程入口
- 用户不可主动发起拼团，只能加入系统创建的拼团活动




## 三、拼团详情

### 1. 接口信息

- **请求方式**: GET
- **完整路径**: `/pc/group/work/detail`
- **接口说明**: 查询单个组团的详细信息，包含参团用户列表、组团进度、关联服务器信息，展示该服务器的配置信息（根据服务器Id）

注：拼团成功后（status=2），可点击跳转到  服务器配置操作的图文教程

### 2. 请求参数

**Header:**

| Header | 类型   | 必填 | 说明                      |
| ------ | ------ | ---- | ------------------------- |
| token  | String | 否   | 用户令牌（已登录则传）    |

**Query Parameters:**

| 参数名      | 类型 | 必填 | 说明                         |
| ----------- | ---- | ---- | ---------------------------- |
| groupWorkId | Long | 是   | 组团 ID（osh_group_work.id）  |

### 3. 响应示例

**成功响应:**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "group_work_id": 1001,
    "group_activity_id": 1,
    "activity_title": "高性能服务器5人拼团",
    "cpu": "4核",
    "memory": "8GB",
    "storage": "100GB SSD",
    "base_price": 199.00,
    "current_price": 165.83,
    "total_duration": 12,
    "remaining_months": null,
    "current_num": 3,
    "group_min_num": 2,
    "group_max_num": 5,
    "remain_num": 2,
    "group_status": 0,
    "group_status_text": "进行中",
    "start_time": "2026-04-01 00:00:00",
    "end_time": "2027-04-01 00:00:00",
    "server_start_time": null,
    "server_expire_time": null,
    "created_time": "2026-04-16 18:00:00",
    "users": [
      {
        "username": "user001",
        "nickname": "张三",
        "avatar": "/uploads/avatar/001.jpg",
        "join_time": "2026-04-16 18:00:00"
      },
      {
        "username": "user002",
        "nickname": "李四",
        "avatar": "/uploads/avatar/002.jpg",
        "join_time": "2026-04-16 18:30:00"
      },
      {
        "username": "user003",
        "nickname": "王五",
        "avatar": "/uploads/avatar/003.jpg",
        "join_time": "2026-04-16 19:00:00"
      }
    ],
    "current_user_joined": false,
    "can_join": true,
    "server_tutorial_url": "/tutorial/server-config",
    "wechat_phone": "13800138000",
    "server_detail": null
  }
}
```

### 4. 响应字段说明

| 字段名              | 类型               | 说明                                          |
| ------------------- | ------------------ | --------------------------------------------- |
| group_work_id       | Long               | 组团 ID                                        |
| group_activity_id   | Long               | 拼团活动 ID                                    |
| activity_title      | String             | 拼团活动标题                                   |
| cpu                 | String             | 服务器CPU配置                                  |
| memory              | String             | 服务器内存配置                                 |
| storage             | String             | 服务器存储配置                                 |
| base_price          | BigDecimal         | 基础拼团价格（完整周期）                       |
| current_price       | BigDecimal         | 当前参团价格（按剩余月数计算）                 |
| total_duration      | Integer            | 服务器总使用时长（月）                         |
| remaining_months    | BigDecimal         | 剩余可使用月数（成团后有值，未成团null）       |
| current_num         | Integer            | 已参团人数                                     |
| group_min_num       | Integer            | 拼团最低所需人数（达到此人数才成团）           |
| group_max_num       | Integer            | 拼团人数上限                                   |
| remain_num          | Integer            | 还差几人达到上限（group_max_num - current_num）|
| group_status        | Integer            | 组团状态：0-进行中 1-已成团 2-已取消/过期      |
| group_status_text   | String             | 组团状态文字描述                               |
| start_time          | String             | 拼团活动开始时间                               |
| server_start_time   | String             | 服务器开始使用时间（成团后有值，未成团null）   |
| server_expire_time  | String             | 服务器使用到期时间（成团后有值，未成团null）   |
| created_time        | String             | 组团创建时间                                   |
| users               | List               | 参团用户列表（含 join_time）                   |
| current_user_joined | Boolean            | 当前登录用户是否已加入该团（未登录时为 false） |
| can_join            | Boolean            | 当前是否可以加入（综合状态、人数、时间判断）   |
| server_tutorial_url | String             | 服务器配置操作教程链接                         |
| wechat_phone        | String             | 服务器负责人微信号                             |
| server_detail       | Object             | 服务器详细信息（仅成团后返回，见下方说明）     ||

### 5. 业务逻辑

```
1. 参数校验：查询 osh_group_work（groupWorkId），校验存在性
2. 关联查询拼团活动信息：
   LEFT JOIN osh_group_activity ga ON gw.group_activity_id = ga.id
3. 拼团成功判定：current_num >= group_min_num 时，group_status = 1（已成团）
4. 服务器时间字段逻辑：
   - 未成团时（group_status = 0）：
     * server_start_time = null
     * server_expire_time = null
     * remaining_months = null
   - 已成团时（group_status = 1）：
     * server_start_time = 成团时间（达到最低人数的时间）
     * server_expire_time = server_start_time + total_duration 月
     * remaining_months = (server_expire_time - NOW()) / 30
5. 查询参团用户列表：
   SELECT u.username, u.nickname, u.avatar, gu.join_time 
   FROM osh_group_user gu
   LEFT JOIN osh_user u ON gu.user_id = u.id
   WHERE gu.group_work_id = #{groupWorkId}
   ORDER BY gu.join_time ASC
6. 计算当前价格：current_price = base_price × (remaining_months / total_duration)
   - 未成团时：按预计服务器使用时长计算（预售阶段，使用total_duration）
   - 已成团时：按server_expire_time计算剩余时间
7. 判断是否可以加入：
   - can_join = (status = 1 或 status = 2)
   - 即：只要拼团活动状态是「进行中」或「拼团成功」，就可以参团
   - 当 status = 3（已结束）时，can_join = false
   - 当前用户未加入该团（需 token）
8. 如果 group_status = 1（已成团），返回 server_detail 信息
9. 密码字段需要脱敏处理
10. 返回组团详情
```






---

## 附录F：服务器拼团特殊业务规则（重要）

### F.1 动态价格计算公式

```
剩余月数 = (服务器结束时间 - 当前时间) / 30天
实际参团费用 = 基础拼团价 × (剩余月数 / 总使用月数)

示例：
- 基础拼团价：199元/月
- 总使用时长：12个月  
- 剩余时间：10个月
- 实际费用 = 199 × (10 / 12) = 165.83元
```

**接口应用：**
- 接口一（拼团列表）：返回 `base_price` 和 `current_price` 两个价格字段
- 接口三（拼团详情）：根据剩余时间动态计算 `current_price`

**重要说明：**
- 未成团时（current_num < group_min_num）：
  * `remaining_months` = null
  * `server_start_time` = null
  * `server_end_time` = null
  * 此时价格按total_duration计算（预售阶段）
- 已成团时（current_num >= group_min_num）：
  * `server_start_time` = 达到最低人数的时间
  * `server_end_time` = server_start_time + total_duration 月
  * `remaining_months` = (server_end_time - NOW()) / 30

**拼团活动特点：**
- 拼团活动**没有截止时间**，只有状态变化
- 状态流转：进行中(1) → 拼团成功(2) → 已结束(3)
- 拼团成功后仍可继续加入，直到人数达到上限或服务器到期

### F.2 定时任务规则

**任务1：自动续团检测**
- **执行频率**：每天凌晨2点执行
- **触发条件**（满足任一即创建新团）：
  1. 人数达到上限：`current_num >= group_max_num`
  2. 临近结束：`距离服务器结束时间 <= 1个月（30天）`
- **操作逻辑**：
  ```
  1. 查询所有 status IN (1, 2) 的拼团活动
  2. 遍历检查触发条件
  3. 满足条件则：
     - 复制当前拼团配置（CPU、内存、存储、价格等）
     - 创建新的拼团活动记录
     - 新活动的 start_time = 当前时间
     - 新活动的 end_time = 当前时间 + total_duration 月
     - 新活动的 current_num = 0
     - 新活动的 status = 1（进行中）
  ```

**任务2：过期拼团清理**
- **执行频率**：每小时执行
- **处理逻辑**：
  - 标记已过期的拼团状态为"已结束"（status = 3）
  - 发送到期提醒通知给参团用户

### F.3 参团资格校验规则

用户参团前**必须同时满足**以下条件：

| 校验项 | 条件 | 说明 |
| ------ | ---- | ---- |
| 活动状态 | `status = 1` | 拼团活动必须为"进行中"状态 |
| 人数限制 | `current_num < group_max_num` | 当前人数未达上限 |
| 时间限制 | `remaining_months > 1` | 剩余使用时长大于1个月 |
| 重复购买 | 用户未参加过该拼团 | 防止同一用户重复参团 |
| 账户状态 | 用户账户正常 | 账户未被冻结或禁用 |

**参团资格校验规则：**

用户参团前**必须同时满足**以下条件：

| 校验项 | 条件 | 说明 |
| ------ | ---- | ---- |
| 活动状态 | `status IN (1, 2)` | 拼团活动必须为「进行中」或「拼团成功」状态 |
| 重复购买 | 用户未参加过该拼团 | 防止同一用户重复参团 |
| 账户状态 | 用户账户正常 | 账户未被冻结或禁用 |

**说明：**
- `can_join` 直接由 `status` 字段决定
- 当 `status = 3`（已结束）时，`can_join = false`
- 人数上限判断和时间判断由系统自动更新 `status` 字段，无需前端重复判断

**校验失败提示：**
- 拼团已结束："该拼团已结束，请关注新一期拼团活动"
- 重复购买："您已参与该拼团活动"

### F.4 服务器资源管理

**资源配置原则：**
- **固定配置**：CPU、内存、存储由系统预设，用户不可选择
- **统一标准**：同一拼团活动的所有用户使用相同配置
- **自动分配**：成团后系统自动分配服务器资源

**服务器信息返回规则：**
- **未成团**（group_status = 0）：不返回服务器连接信息
- **已成团**（group_status = 1）：返回完整服务器信息
  - 服务器IP、端口
  - 登录账号、密码（脱敏显示）
  - 控制面板URL
  - 操作系统类型
  - 开通日期、到期日期

**安全要求：**
- 密码字段必须脱敏，显示为 `******`
- 提供"查看密码"功能（需二次验证）
- 记录服务器信息访问日志

### F.5 拼团活动状态流转

```
草稿(0) → 上架 → 进行中(1) → 达到最低人数 → 拼团成功(2)
                                              ↓
                                    满足以下任一条件：
                                    - 人数达到上限（用户参团时触发）
                                    - 服务器剩余时间 ≤ 1个月（定时任务触发）
                                              ↓
                                        已结束(3)
```

**状态说明：**
- **草稿(0)**：项目负责人创建，未对外展示
- **进行中(1)**：用户可参团，未达到最低人数
- **拼团成功(2)**：已达到最低人数（current_num >= group_min_num），继续接受参团
- **已结束(3)**：不可参团（人数达到上限 或 服务器剩余时间 ≤ 1个月）

**拼团成功判定：**
- **未成团**：current_num < group_min_num
  * 服务器时间字段全部为 null
  * 不分配服务器资源
  * 不计费
- **已成团**：current_num >= group_min_num
  * 记录 server_start_time（成团时间）
  * 计算 server_end_time = server_start_time + total_duration
  * 开始分配服务器资源
  * 开始计算服务器使用时长
  * 用户可按剩余月数参团

### F.6 状态更新触发机制（重要）

拼团活动的 `status` 字段更新由以下两个途径触发：

#### 1. 用户参团时更新（人数上限判断）

**触发时机：** 每次用户成功参团后

**判断条件：** 
```sql
current_num >= group_max_num
```

**执行操作：**
```sql
UPDATE osh_group_activity 
SET status = 3 
WHERE id = #{activityId} 
  AND current_num >= group_max_num
```

**说明：**
- 在用户参团的事务中同步执行
- 达到人数上限后立即标记为已结束
- 保证后续用户无法再参团

#### 2. 定时任务更新（服务器时间判断）

**执行频率：** 每天凌晨2点（可配置）

**判断条件：**
```sql
status = 2  -- 拼团成功状态
AND (server_end_time - NOW()) / 30 <= 1  -- 剩余时间不足一个月
```

**执行操作：**
```sql
UPDATE osh_group_activity 
SET status = 3 
WHERE status = 2 
  AND DATEDIFF(server_end_time, NOW()) / 30.0 <= 1
```

**说明：**
- 定时扫描所有已成团的拼团活动
- 检测服务器剩余使用时间
- 不足一个月时自动标记为已结束

#### 3. 状态更新流程图

```
用户参团                              定时任务（每天2点）
  |                                      |
  v                                      v
参团成功                           扫描 status=2 的活动
  |                                      |
  v                                      v
检查人数                        计算 remaining_months
  |                                      |
  v                                      v
current_num >= group_max_num?    remaining_months <= 1?
  |                                      |
  +----------> 是 -------------> 是 <----+
  |                                      |
  v                                      v
UPDATE status=3                  UPDATE status=3
  |                                      |
  v                                      v
拼团结束，不可参团              拼团结束，不可参团
```

### F.7 接口优化要点总结

| 接口 | 优化内容 |
| ---- | -------- |
| 接口一 | 移除type参数（仅服务器），增加价格计算、can_join判断 |
| 接口二 | 改为需登录，增加服务器信息、订单状态、密码脱敏 |
| 接口三 | 简化为纯服务器拼团，增加server_detail、教程链接 |
| 通用 | 统一时间格式为 `yyyy-MM-dd HH:mm:ss`，移除课程/专栏相关字段 |

## 五、可参团列表（组团列表）

### 1. 接口信息

- **请求方式**: GET
- **完整路径**: `/pc/group/list`
- **认证要求**: 匿名可访问（`@Anonymous`）
- **接口说明**: 查询某个拼团活动下当前正在进行中的可参团列表（即别人已发起但还未满员的组团），供用户选择加入
- **现有实现**: [GroupController.java](backstage-system/src/main/java/com/backstage/system/controller/group/GroupController.java:54)

### 2. 请求参数

**Header:**

| Header | 类型   | 必填 | 说明       |
| ------ | ------ | ---- | ---------- |
| appid  | String | 是   | 网校 appid |

**Query Parameters:**

| 参数名  | 类型    | 必填 | 说明                              |
| ------- | ------- | ---- | --------------------------------- |
| groupId | Long    | 是   | 拼团活动 ID（osh_group_activity.id） |
| page    | Integer | 是   | 页码                               |

### 3. 响应示例

**成功响应:**
```json
{
  "total": 5,
  "rows": [
    {
      "id": 1001,
      "num": 2,
      "total": 3,
      "expire": "2026-04-16T18:00:00",
      "createdTime": "2026-04-15T18:00:00",
      "users": [
        {
          "username": "user001",
          "nickname": "小明",
          "avatar": "/uploads/avatar/001.jpg"
        },
        {
          "username": "user002",
          "nickname": "小红",
          "avatar": "/uploads/avatar/002.jpg"
        }
      ]
    },
    {
      "id": 1002,
      "num": 1,
      "total": 3,
      "expire": "2026-04-17T10:00:00",
      "createdTime": "2026-04-16T10:00:00",
      "users": [
        {
          "username": "user003",
          "nickname": "小华",
          "avatar": "/uploads/avatar/003.jpg"
        }
      ]
    }
  ],
  "code": 200,
  "msg": "查询成功"
}
```

### 4. 响应字段说明

**rows 数组中每项**（`GroupWorkVo`）:

| 字段名      | 类型             | 说明                       |
| ----------- | ---------------- | -------------------------- |
| id          | Long             | 组团 ID（osh_group_work.id）|
| num         | Integer          | 已参团人数                  |
| total       | Integer          | 成团所需总人数              |
| expire      | LocalDateTime    | 组团过期时间                |
| createdTime | LocalDateTime    | 组团发起时间                |
| users       | List<GroupUserVo> | 已参团用户列表              |

**users 数组中每项**（`GroupUserVo`）:

| 字段名   | 类型   | 说明       |
| -------- | ------ | ---------- |
| username | String | 用户名      |
| nickname | String | 昵称        |
| avatar   | String | 头像 URL    |

### 5. 业务逻辑

```
现有实现（GroupServiceImpl.selectGroupList()）：
1. 通过 groupMapper.getGroupWorkListByActivityId(groupId) 获取组团列表
2. 遍历每个 GroupWork，通过 groupMapper.getGroupWorkUserListById 获取参团用户 ID 列表
3. 通过 groupMapper.getGroupUserListByIds 批量查询用户信息（username, nickname, avatar）
4. 组装为 GroupWorkVo 列表返回
5. 使用 BaseController.startPage() + getDataTable() 分页

建议增强：
- 追加查询条件 status = 0（进行中）AND expire > NOW()（未过期）
- 将 N+1 查询优化为 LEFT JOIN 一次查询
- 增加 remain_count（还差几人成团）的计算字段
```

### 6. 错误码

| code | msg          | 场景                      |
| ---- | ------------ | ------------------------- |
| 200  | 查询成功      | 正常返回                   |
| 500  | 活动不存在    | groupId 对应的活动不存在    |

---

## 六、发起拼团（创建组团）

### 1. 接口信息

- **请求方式**: POST
- **完整路径**: `/pc/group/work/create`
- **认证要求**: **需要登录**（需传 token，通过 `SecurityUtils.getUserId()` 获取当前用户）
- **接口说明**: 用户发起一个新的组团，自己成为团长，同时创建拼团订单

### 2. 请求参数

**Header:**

| Header       | 类型   | 必填 | 说明                       |
| ------------ | ------ | ---- | -------------------------- |
| appid        | String | 是   | 网校 appid                  |
| token        | String | 是   | 用户登录令牌                |
| Content-Type | String | 是   | application/json           |

**Body (JSON):**

```json
{
  "activity_id": 1,
  "pay_method": "wechat"
}
```

| 参数名      | 类型   | 必填 | 说明                                        |
| ----------- | ------ | ---- | ------------------------------------------- |
| activity_id | Long   | 是   | 拼团活动 ID（osh_group_activity.id）          |
| pay_method  | String | 否   | 支付方式：`wechat`-微信支付、`alipay`-支付宝   |

### 3. 响应示例

**成功响应:**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "group_work_id": 1001,
    "order_id": 5001,
    "order_no": "20260416_a1b2c3d4e",
    "price": 99.00,
    "expire": "2026-04-17T18:00:00",
    "pay_url": "https://pay.weixin.qq.com/..."
  }
}
```

### 4. 响应字段说明

| 字段名        | 类型       | 说明                              |
| ------------- | ---------- | --------------------------------- |
| group_work_id | Long       | 新创建的组团 ID                    |
| order_id      | Long       | 创建的拼团订单 ID                   |
| order_no      | String     | 订单编号                           |
| price         | BigDecimal | 需支付金额（拼团价）                |
| expire        | String     | 组团过期时间                        |
| pay_url       | String     | 支付链接（对接支付网关后返回）       |

### 5. 业务逻辑

```
1. 鉴权：校验 token 有效性，获取 userId 和 schoolId
2. 参数校验：
   a. 查询 osh_group_activity（activity_id），校验存在性
   b. 校验 status = 1（上架）且 NOW() BETWEEN start_time AND end_time
   c. 校验 max_groups 限制（0=不限，否则检查当前组团数 < max_groups）
   d. 校验 per_user_limit 限制（查询该用户在该活动下的已有订单数）
3. 创建组团（osh_group_work）：
   a. group_activity_id = activity_id
   b. leader_user_id = userId
   c. num = 1（团长自己）
   d. total = activity.p_num
   e. status = 0（进行中）
   f. expire = NOW() + activity.duration_hours 小时
4. 创建参团用户记录（osh_group_user）：
   a. group_work_id = 新组团 ID
   b. user_id = userId
   c. is_leader = 1
5. 创建拼团订单（osh_group_order）：
   a. school_id = schoolId
   b. user_id = userId
   c. group_activity_id = activity_id
   d. group_work_id = 新组团 ID
   e. no = 日期 + UUID 随机串
   f. status = "pending"
   g. price = activity.price（拼团价）
   h. total_price = activity.original_price（商品原价）
   i. type = "group"
   j. pay_method = 请求参数
   k. goods_snapshot = activity.goods_snapshot
6. 关联 order_id 回写到 osh_group_user 记录
7. 整个流程需要 @Transactional 事务保护
8. 返回组团信息和订单信息

并发安全：
- max_groups 检查使用 SELECT COUNT + INSERT，需加乐观锁或数据库唯一约束
- per_user_limit 检查同理
```

### 6. 错误码

| code | msg                    | 场景                            |
| ---- | ---------------------- | ------------------------------- |
| 200  | 操作成功                | 正常创建                         |
| 401  | 请先登录                | 未传 token 或 token 过期          |
| 500  | 活动不存在              | activity_id 无对应记录            |
| 500  | 活动已结束              | 活动 status != 1 或不在有效期内   |
| 500  | 开团数量已达上限         | 当前组团数 >= max_groups          |
| 500  | 您已超过该活动的限购次数  | 用户参团次数 >= per_user_limit    |

---

## 七、参与拼团（加入组团）

### 1. 接口信息

- **请求方式**: POST
- **完整路径**: `/pc/group/work/join`
- **认证要求**: **需要登录**（需传 token）
- **接口说明**: 用户加入一个已有的进行中的组团，同时创建拼团订单

### 2. 请求参数

**Header:**

| Header       | 类型   | 必填 | 说明                       |
| ------------ | ------ | ---- | -------------------------- |
| appid        | String | 是   | 网校 appid                  |
| token        | String | 是   | 用户登录令牌                |
| Content-Type | String | 是   | application/json           |

**Body (JSON):**

```json
{
  "group_work_id": 1001,
  "pay_method": "wechat"
}
```

| 参数名        | 类型   | 必填 | 说明                                        |
| ------------- | ------ | ---- | ------------------------------------------- |
| group_work_id | Long   | 是   | 组团 ID（osh_group_work.id）                  |
| pay_method    | String | 否   | 支付方式：`wechat`-微信支付、`alipay`-支付宝   |

### 3. 响应示例

**成功响应:**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "group_work_id": 1001,
    "order_id": 5002,
    "order_no": "20260416_f5g6h7i8j",
    "price": 99.00,
    "num": 3,
    "total": 3,
    "is_success": true,
    "pay_url": "https://pay.weixin.qq.com/..."
  }
}
```

### 4. 响应字段说明

| 字段名        | 类型       | 说明                                 |
| ------------- | ---------- | ------------------------------------ |
| group_work_id | Long       | 加入的组团 ID                         |
| order_id      | Long       | 创建的拼团订单 ID                      |
| order_no      | String     | 订单编号                              |
| price         | BigDecimal | 需支付金额（拼团价）                   |
| num           | Integer    | 加入后当前参团人数                     |
| total         | Integer    | 成团所需总人数                         |
| is_success    | Boolean    | 加入后是否已满员成团                    |
| pay_url       | String     | 支付链接                              |

### 5. 业务逻辑

```
1. 鉴权：校验 token，获取 userId 和 schoolId
2. 参数校验：
   a. 查询 osh_group_work（group_work_id），校验存在性
   b. 校验 status = 0（进行中）且 expire > NOW()（未过期）
   c. 校验 num < total（还有空位）
   d. 查询关联的 osh_group_activity，校验活动仍在有效期
   e. 校验 per_user_limit（该用户在同一活动下的参团次数）
   f. 校验用户未加入过该组团（uk_work_user 唯一约束兜底）
3. 乐观锁更新参团人数（关键并发控制点）：
   UPDATE osh_group_work
   SET num = num + 1
   WHERE id = #{groupWorkId} AND num < total AND status = 0
   → 影响行数 = 0 则返回 "该团已满员或已结束"
4. 创建参团用户记录（osh_group_user）：
   a. group_work_id = group_work_id
   b. user_id = userId
   c. is_leader = 0
5. 创建拼团订单（osh_group_order），逻辑同接口六
6. 关联 order_id 回写到 osh_group_user
7. 判断是否成团（num + 1 == total）：
   a. 若成团 → UPDATE osh_group_work SET status = 1, success_time = NOW()
   b. 触发成团通知（osh_group_notification，type = 'success'）
   c. 更新该组团下所有订单 status = 'success'
   d. 推送微信群二维码（从 osh_group_activity.wechat_group_qr 获取）
8. 整个流程需要 @Transactional 事务保护
```

支付成功后，回调
用户扫码/确认支付
↓
微信/支付宝服务器 → 异步回调 → POST /api/pay/callback/wechat
↓
支付回调Service:
1. 验签(验证回调真实性)
2. 更新订单状态: PENDING → PAID
3. 更新拼团订单状态: PENDING → PAID
4. 更新拼团活动 currentNum + 1
5. 检查是否成团 (currentNum >= groupMinNum)
  - 已成团 → 通知所有参团用户
  - 未成团 → 等待
6. 返回 success 给微信/支付宝


## 八、我的拼团列表

### 1. 接口信息

- **请求方式**: GET
- **完整路径**: `/pc/group/my`
- **认证要求**: **需要登录**（需传 token）
- **接口说明**: 查询当前登录用户参与的所有拼团记录，包含组团状态、订单状态、商品信息

### 2. 请求参数

**Header:**

| Header | 类型   | 必填 | 说明           |
| ------ | ------ | ---- | -------------- |
| appid  | String | 是   | 网校 appid      |
| token  | String | 是   | 用户登录令牌    |

**Query Parameters:**

| 参数名   | 类型    | 必填 | 说明                                                                  |
| -------- | ------- | ---- | --------------------------------------------------------------------- |
| status   | Integer | 否   | 筛选组团状态：`0`-进行中、`1`-已成团、`2`-已取消/过期；不传则返回全部    |
| page     | Integer | 否   | 页码，默认 1                                                           |
| pageSize | Integer | 否   | 每页条数，默认 10                                                       |

### 3. 响应示例

**成功响应:**
```json
{
  "total": 3,
  "rows": [
    {
      "group_work_id": 1001,
      "group_activity_id": 1,
      "activity_title": "Python入门课程 3人团",
      "goods_type": "course",
      "goods_snapshot": {
        "title": "Python从入门到精通",
        "cover": "/uploads/course/python.jpg"
      },
      "group_price": 99.00,
      "num": 3,
      "total": 3,
      "group_status": 1,
      "group_status_text": "已成团",
      "expire": "2026-04-17T18:00:00",
      "is_leader": true,
      "join_time": "2026-04-15T18:00:00",
      "order_id": 5001,
      "order_no": "20260416_a1b2c3d4e",
      "order_status": "success",
      "order_status_text": "拼团成功",
      "wechat_group_qr": "/uploads/qr/group_101.jpg",
      "users": [
        {
          "username": "user001",
          "nickname": "小明",
          "avatar": "/uploads/avatar/001.jpg"
        },
        {
          "username": "user002",
          "nickname": "小红",
          "avatar": "/uploads/avatar/002.jpg"
        },
        {
          "username": "user003",
          "nickname": "小华",
          "avatar": "/uploads/avatar/003.jpg"
        }
      ]
    }
  ],
  "code": 200,
  "msg": "查询成功"
}
```

### 4. 响应字段说明

| 字段名            | 类型              | 说明                                                     |
| ----------------- | ----------------- | -------------------------------------------------------- |
| group_work_id     | Long              | 组团 ID                                                   |
| group_activity_id | Long              | 拼团活动 ID                                               |
| activity_title    | String            | 活动标题                                                  |
| goods_type        | String            | 商品类型                                                  |
| goods_snapshot    | Object            | 商品快照（含 title、cover）                                 |
| group_price       | BigDecimal        | 拼团价格                                                  |
| num               | Integer           | 当前已参团人数                                             |
| total             | Integer           | 成团总人数                                                |
| group_status      | Integer           | 组团状态：0-进行中 1-已成团 2-已取消/过期                    |
| group_status_text | String            | 组团状态文字描述                                           |
| expire            | String            | 组团过期时间                                               |
| is_leader         | Boolean           | 当前用户是否是该团的团长                                    |
| join_time         | String            | 当前用户加入时间                                           |
| order_id          | Long              | 关联订单 ID                                                |
| order_no          | String            | 订单编号                                                  |
| order_status      | String            | 订单状态：pending/paid/success/refunding/refunded/cancel   |
| order_status_text | String            | 订单状态文字描述                                           |
| wechat_group_qr   | String            | 微信群二维码 URL（仅成团后返回）                             |
| users             | List\<GroupUserVo\>| 该组团的全部参团用户列表                                    |

### 5. 业务逻辑

```
1. 鉴权：校验 token，获取 userId
2. 查询 osh_group_user 中 user_id = userId 的所有参团记录
3. 关联查询：
   osh_group_user gu
   LEFT JOIN osh_group_work gw ON gu.group_work_id = gw.id
   LEFT JOIN osh_group_activity ga ON gw.group_activity_id = ga.id
   LEFT JOIN osh_group_order go ON gu.order_id = go.id
4. 如传入 status 参数，追加 gw.status = #{status} 筛选条件
5. 按 gu.join_time DESC 排序
6. 对每条记录查询该组团下所有参团用户（可用 LEFT JOIN 或子查询批量获取）
7. 状态文字映射：
   group_status: 0→"进行中" 1→"已成团" 2→"已取消"
   order_status: pending→"待支付" paid→"已支付" success→"拼团成功"
                 refunding→"退款中" refunded→"已退款" cancel→"已取消"
8. wechat_group_qr 仅在 group_status = 1 时从 activity 表获取并返回
```

### 6. 错误码

| code | msg      | 场景                      |
| ---- | -------- | ------------------------- |
| 200  | 查询成功  | 正常返回（无数据返回空列表）|
| 401  | 请先登录  | 未传 token 或 token 过期   |

---





## 附录

### A. 公共响应格式

**R\<T\> 单对象响应**（接口二、三、四、六、七、九使用）:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": { ... }
}
```

**TableDataInfo 分页列表响应**（接口一、五、八使用）:
```json
{
  "total": 100,
  "rows": [ ... ],
  "code": 200,
  "msg": "查询成功"
}
```

### B. 订单状态流转

```
             ┌──────────┐
             │ pending   │ 待支付
             └─────┬─────┘
                   │ 支付成功
                   ▼
             ┌──────────┐
             │  paid     │ 已支付（等待成团）
             └─────┬─────┘
            ┌──────┴───────┐
            │              │
     成团成功▼         未成团 ▼
    ┌──────────┐    ┌──────────┐
    │ success   │    │ refunding │ 退款中
    └──────────┘    └─────┬─────┘
                          │ 退款完成
                          ▼
                    ┌──────────┐
                    │ refunded  │ 已退款
                    └──────────┘

   ※ 未支付超时 → cancel（已取消）
```

### C. 组团状态流转

```
    ┌──────────┐
    │  0 进行中  │ ← 发起拼团时创建
    └─────┬─────┘
     ┌────┴────┐
     │         │
  满员▼    过期 ▼
┌──────────┐ ┌──────────┐
│ 1 已成团  │ │ 2 已取消  │
└──────────┘ └──────────┘
```

### D. 涉及数据库表

| 表名                    | 说明         | 关联接口                |
| ----------------------- | ------------ | ----------------------- |
| osh_group_activity      | 拼团活动表    | 一、二、三、四、六、七    |
| osh_group_work          | 组团表        | 五、六、七、八、九        |
| osh_group_user          | 参团用户表    | 五、六、七、八、九        |
| osh_group_order         | 拼团订单表    | 六、七、八               |
| osh_group_notification  | 通知记录表    | 七（成团触发通知）        |
| osh_course              | 课程表        | 三                       |
| osh_column              | 专栏表        | 四                       |
| osh_user                | 用户表        | 五、八、九               |

### E. 接口与现有代码对照

| 接口 | 现有实现状态 | 对应 Controller / Method                              |
| ---- | ------------ | ----------------------------------------------------- |
| 一   | **待开发**   | 新增 GroupController.activityList()                     |
| 二   | **待开发**   | 新增 GroupController.activityDetail()                   |
| 三   | **已有**     | GroupController.course() → GroupServiceImpl.course()    |
| 四   | **已有**     | GroupController.column() → GroupServiceImpl.column()    |
| 五   | **已有**     | GroupController.list() → GroupServiceImpl.selectGroupList() |
| 六   | **待开发**   | 新增（建议放在 GroupController 或新建 GroupWorkController）|
| 七   | **待开发**   | 新增（同上）                                            |
| 八   | **待开发**   | 新增 GroupController.myGroups()                         |
| 九   | **待开发**   | 新增 GroupController.workDetail()                       |

> **已有接口的已知缺陷**（详见设计文档）:
> - 接口三/四：未校验活动状态和有效期
> - 接口五：查询未过滤已过期/已满员的组团；存在 N+1 查询性能问题
> - `OshGroupOrderController.add()`：使用 `@Anonymous` 无鉴权、hardcoded userId=1L、status 拼写为 "pendding"


