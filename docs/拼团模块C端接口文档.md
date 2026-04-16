# 拼团模块 功能接口文档

**基础路径**: `/pc/group`、`/pc/order/group`

## 一、查询拼团列表接口

- **请求方式**: GET
- **完整路径**: `/pc/group/activity/list`
- **认证要求**: 匿名可访问（`@Anonymous`）
- **接口说明**: 查询正在进行中的拼团活动列表，支持按类型筛选，按 sort_order 降序排列

 2. Query Parameters:

| 参数名   | 类型    | 必填 | 说明                                                                 
| -------- | ------- | ---- | -------------------------------------------------------------------
| type     | String  | 否   | 拼团类型筛选：`server`-服务器、`course`-课程、`book`-电子书
| page     | Integer | 否   | 页码，默认 1                                                         
| pageSize | Integer | 否   | 每页条数，默认 10                                                   

3. 响应示例

**成功响应:**
{
  "total": 12,
  "rows": [
    {
      "id": 1,
      "title": "服务器5人拼团",
      "type": "server",
      "goods_id": 101,
      "goods_detail": { 
            "cpu": "4核",
            "memory": "8GB",
            "storage": "100GB SSD"
      }
      "price": 199.00,
      "duration_time": 365,
      "start_time": "2026-04-01 00:00:00",
      "end_time": "2027-04-01 00:00:00",
      "status": 1,
      "current_group_num": 3，
      "group_num": 5
    }
  ],
  "code": 200,
  "msg": "查询成功"
}

### 4. 响应字段说明

| 字段名              | 类型       | 说明                                      |
| ------------------- | ---------- | ----------------------------------------- |
| id                  | Long       | 拼团ID                                |
| title               | String     | 拼团标题                                |
| type                | String     | 拼团类型：server/course/column           |
| goods_id            | Long       | 关联商品 ID                                 |
| goods_snapshot      | Object     | 商品快照（含 title、cover 等核心信息）       |
| original_price      | BigDecimal | 商品原价                                    |
| price               | BigDecimal | 拼团价格                                    |
| p_num               | Integer    | 成团人数                                    |
| duration_hours      | Integer    | 单次组团有效期（小时）                        |
| start_time          | String     | 活动开始时间                                 |
| end_time            | String     | 活动结束时间                                 |
| status              | Integer    | 活动状态：0-草稿 1-上架 2-下架 3-已结束       |
| active_group_count  | Integer    | 当前进行中的组团数（动态计算，非表字段）       |
| success_group_count | Integer    | 已成团数量（动态计算，非表字段）               |

### 5. 业务逻辑

```
2. 查询条件：status = 1（上架）AND NOW() BETWEEN start_time AND end_time
3. 如传入 type，追加 type 条件筛选
4. 按 sort_order DESC, created_time DESC 排序
5. 分页返回，使用 PageUtils 分页
6. active_group_count 通过子查询计算：
   SELECT COUNT(*) FROM osh_group_work WHERE group_activity_id = ? AND status = 0
7. success_group_count 通过子查询计算：
   SELECT COUNT(*) FROM osh_group_work WHERE group_activity_id = ? AND status = 1
```
## 二、查询我的拼团列表接口——拼团活动详情、

- **请求方式**: GET
- **完整路径**: `/pc/group/activity/mylist`
- **认证要求**: 匿名可访问（`@Anonymous`）
- **接口说明**: 查询当前用户参与的拼团活动列表（进行中、已完成、未完成），支持按类型筛选，按 sort_order 降序排列


 2. Query Parameters:

| 参数名   | 类型    | 必填 | 说明                                                                 
| -------- | ------- | ---- | -------------------------------------------------------------------
| type     | String  | 否   | 拼团类型筛选：`server`-服务器、`course`-课程、`book`-电子书
| page     | Integer | 否   | 页码，默认 1                                                         
| pageSize | Integer | 否   | 每页条数，默认 10                                                   

3. 响应示例

**成功响应:**
{
  "total": 12,
  "rows": [
    {
      "id": 1,
      "title": "服务器5人拼团",
      "type": "server",
      "goods_id": 101,
      "goods_detail": { 
            "cpu": "4核",
            "memory": "8GB",
            "storage": "100GB SSD"
            -- 服务器的配置文件下载链接 / 服务器的配置信息
            （判断status=2 成功后，才展示）
      }
      "price": 199.00,
      "duration_time": 365,
      "start_time": "2026-04-01 00:00:00",
      "end_time": "2027-04-01 00:00:00",
      "status": 1,
      "success_msg": "恭喜您拼团成功！请扫描二维码加入学习群。",
      -- "wechat_group_qr": "/uploads/qr/group_101.jpg",
      "current_group_num": 3，
      "group_num": 5
    }
  ],
  "code": 200,
  "msg": "查询成功"
}



### 4. 响应字段说明

| 字段名            | 类型       | 说明                                       |
| ----------------- | ---------- | ------------------------------------------ |
| id                | Long       | 拼团活动 ID                                 |
| title             | String     | 活动标题                                    |
| type              | String     | 商品类型                                    |
| goods_id          | Long       | 商品 ID                                     |
| goods_snapshot    | Object     | 商品快照 JSON                               |
| original_price    | BigDecimal | 商品原价                                    |
| price             | BigDecimal | 拼团价                                      |
| p_num             | Integer    | 成团人数                                    |
| max_groups        | Integer    | 最大组团数（0=不限）                          |
| per_user_limit    | Integer    | 每人限购次数                                 |
| duration_hours    | Integer    | 单次组团有效期（小时）                        |
| start_time        | String     | 活动开始时间                                 |
| end_time          | String     | 活动结束时间                                 |
| status            | Integer    | 活动状态                                    |
| auto_refund       | Integer    | 未成团是否自动退款：0-否 1-是                 |
| success_msg       | String     | 成团推送消息模板                              |
| wechat_group_qr   | String     | 微信群二维码 URL                              |
| active_group_count | Integer   | 进行中组团数（动态计算）                      |
| success_group_count| Integer   | 已成团数量（动态计算）                        |
| total_order_count | Integer    | 累计订单数（动态计算）                        |

### 5. 业务逻辑

```
1. 根据 activityId 查询 osh_group_activity 主表
2. 校验活动存在性 → 不存在返回 "活动不存在"
3. 校验活动状态 → status != 1（非上架）返回 "活动已下架"
4. 聚合统计数据：active_group_count、success_group_count、total_order_count
5. 返回完整活动详情
```

### 6. 错误码

| code | msg          | 场景                    |
| ---- | ------------ | ----------------------- |
| 200  | 操作成功      | 正常返回                 |
| 500  | 活动不存在    | activityId 无对应记录    |
| 500  | 活动已下架    | 活动 status != 1         |

---

## 三、课程拼团详情

### 1. 接口信息

- **请求方式**: GET
- **完整路径**: `/pc/group/course/read`
- **认证要求**: 匿名可访问（`@Anonymous`）
- **接口说明**: 查询某个课程的完整信息 + 关联的拼团活动信息，用于拼团课程详情页展示
- **现有实现**: [GroupController.java](backstage-system/src/main/java/com/backstage/system/controller/group/GroupController.java:34)

### 2. 请求参数

**Header:**

| Header | 类型   | 必填 | 说明       |
| ------ | ------ | ---- | ---------- |
| appid  | String | 否   | 网校 appid |
| token  | String | 否   | 用户令牌    |

**Query Parameters:**

| 参数名  | 类型 | 必填 | 说明                            |
| ------- | ---- | ---- | ------------------------------- |
| id      | Long | 是   | 课程 ID（osh_course.id）         |
| groupId | Long | 是   | 拼团活动 ID（osh_group_activity.id） |

### 3. 响应示例

**成功响应:**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 101,
    "title": "Python从入门到精通",
    "cover": "/uploads/course/python.jpg",
    "description": "零基础学Python，30天入门",
    "teacher_name": "张老师",
    "price": 299.00,
    "status": 1,
    "group": {
      "id": 1,
      "type": "course",
      "goods_id": 101,
      "price": "99.00",
      "p_num": 3,
      "start_time": "2026-04-01T00:00:00",
      "end_time": "2026-05-01T00:00:00"
    }
  }
}
```

### 4. 响应字段说明

**顶层字段**（继承自 `OshCourse`）:

| 字段名       | 类型       | 说明                |
| ------------ | ---------- | ------------------- |
| id           | Long       | 课程 ID              |
| title        | String     | 课程标题             |
| cover        | String     | 课程封面 URL          |
| description  | String     | 课程描述             |
| teacher_name | String     | 讲师名               |
| price        | BigDecimal | 课程原价             |
| status       | Integer    | 课程状态             |

**group 子对象**（`GroupActivity`）:

| 字段名     | 类型          | 说明                  |
| ---------- | ------------- | --------------------- |
| id         | Long          | 拼团活动 ID            |
| type       | String        | 商品类型：course       |
| goods_id   | Long          | 关联商品 ID            |
| price      | String        | 拼团价格               |
| p_num      | Integer       | 成团人数               |
| start_time | LocalDateTime | 活动开始时间            |
| end_time   | LocalDateTime | 活动结束时间            |

### 5. 业务逻辑

```
现有实现（GroupServiceImpl.course()）：
1. 通过 groupMapper.getGroupActivityById(groupId) 获取拼团活动
2. 通过 courseMapper.selectCourseById(id) 获取课程详情
3. 使用 BeanUtils.copyProperties 将课程属性拷贝到 GroupCourseVo
4. 将 GroupActivity 设置到 vo.group 字段
5. 返回 GroupCourseVo

建议增强：
- 校验 activity.type == "course" 且 activity.goods_id == id
- 校验活动状态为上架且在有效期内
- 增加用户已购/已参团状态判断（需 token）
```

### 6. 错误码

| code | msg          | 场景                        |
| ---- | ------------ | --------------------------- |
| 200  | 操作成功      | 正常返回                     |
| 500  | 课程不存在    | id 对应的课程不存在           |
| 500  | 活动不存在    | groupId 对应的活动不存在      |
| 500  | 活动已结束    | 活动不在有效期内              |

---

## 四、专栏拼团详情

### 1. 接口信息

- **请求方式**: GET
- **完整路径**: `/pc/group/column/read`
- **认证要求**: 匿名可访问（`@Anonymous`）
- **接口说明**: 查询某个专栏的完整信息 + 关联的拼团活动信息，用于拼团专栏详情页展示
- **现有实现**: [GroupController.java](backstage-system/src/main/java/com/backstage/system/controller/group/GroupController.java:43)

### 2. 请求参数

**Header:**

| Header | 类型   | 必填 | 说明       |
| ------ | ------ | ---- | ---------- |
| appid  | String | 否   | 网校 appid |
| token  | String | 否   | 用户令牌    |

**Query Parameters:**

| 参数名  | 类型 | 必填 | 说明                              |
| ------- | ---- | ---- | --------------------------------- |
| id      | Long | 是   | 专栏 ID（osh_column.id）           |
| groupId | Long | 是   | 拼团活动 ID（osh_group_activity.id） |

### 3. 响应示例

**成功响应:**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 201,
    "title": "前端工程师进阶专栏",
    "cover": "/uploads/column/frontend.jpg",
    "description": "系统学习前端高级技术",
    "price": 199.00,
    "total_section": 30,
    "group": {
      "id": 2,
      "type": "column",
      "goods_id": 201,
      "price": "69.00",
      "p_num": 5,
      "start_time": "2026-04-01T00:00:00",
      "end_time": "2026-05-01T00:00:00"
    }
  }
}
```

### 4. 响应字段说明

**顶层字段**（继承自 `ColumnDetailVo`）:

| 字段名        | 类型       | 说明         |
| ------------- | ---------- | ------------ |
| id            | Long       | 专栏 ID       |
| title         | String     | 专栏标题      |
| cover         | String     | 封面 URL      |
| description   | String     | 专栏描述      |
| price         | BigDecimal | 专栏原价      |
| total_section | Integer    | 总章节数      |

**group 子对象**（`GroupActivity`）:

| 字段名     | 类型          | 说明                  |
| ---------- | ------------- | --------------------- |
| id         | Long          | 拼团活动 ID            |
| type       | String        | 商品类型：column       |
| goods_id   | Long          | 关联商品 ID            |
| price      | String        | 拼团价格               |
| p_num      | Integer       | 成团人数               |
| start_time | LocalDateTime | 活动开始时间            |
| end_time   | LocalDateTime | 活动结束时间            |

### 5. 业务逻辑

```
现有实现（GroupServiceImpl.column()）：
1. 通过 groupMapper.getGroupActivityById(groupId) 获取拼团活动
2. 通过 columnMapper.getColumnDetail(id) 获取专栏详情
3. 使用 BeanUtils.copyProperties 将专栏属性拷贝到 GroupColumnVo
4. 将 GroupActivity 设置到 vo.group 字段
5. 返回 GroupColumnVo

建议增强（同课程拼团详情）：
- 校验 activity.type == "column" 且 activity.goods_id == id
- 校验活动状态及有效期
```

### 6. 错误码

| code | msg          | 场景                        |
| ---- | ------------ | --------------------------- |
| 200  | 操作成功      | 正常返回                     |
| 500  | 专栏不存在    | id 对应的专栏不存在           |
| 500  | 活动不存在    | groupId 对应的活动不存在      |
| 500  | 活动已结束    | 活动不在有效期内              |

---

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

### 6. 错误码

| code | msg                    | 场景                               |
| ---- | ---------------------- | ---------------------------------- |
| 200  | 操作成功                | 正常加入                            |
| 401  | 请先登录                | 未传 token 或 token 过期             |
| 500  | 组团不存在              | group_work_id 无对应记录             |
| 500  | 该团已满员或已结束       | 组团已满/已过期/status != 0          |
| 500  | 您已参加过该团           | 用户已在该组团中（唯一约束冲突）      |
| 500  | 您已超过该活动的限购次数  | 用户参团次数 >= per_user_limit       |
| 500  | 活动已结束              | 关联活动不在有效期内                  |

---

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

## 九、组团详情

### 1. 接口信息

- **请求方式**: GET
- **完整路径**: `/pc/group/work/detail`
- **认证要求**: 匿名可访问（`@Anonymous`），但已登录用户可获得额外信息
- **接口说明**: 查询单个组团的详细信息，包含参团用户列表、组团进度、关联活动和商品信息。可用于分享页面展示

### 2. 请求参数

**Header:**

| Header | 类型   | 必填 | 说明                      |
| ------ | ------ | ---- | ------------------------- |
| appid  | String | 是   | 网校 appid                 |
| token  | String | 否   | 用户令牌（已登录则传）      |

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
    "id": 1001,
    "group_activity_id": 1,
    "activity_title": "Python入门课程 3人团",
    "goods_type": "course",
    "goods_id": 101,
    "goods_snapshot": {
      "title": "Python从入门到精通",
      "cover": "/uploads/course/python.jpg"
    },
    "original_price": 299.00,
    "group_price": 99.00,
    "num": 2,
    "total": 3,
    "remain": 1,
    "status": 0,
    "status_text": "进行中",
    "expire": "2026-04-17T18:00:00",
    "created_time": "2026-04-16T18:00:00",
    "leader": {
      "username": "user001",
      "nickname": "小明",
      "avatar": "/uploads/avatar/001.jpg"
    },
    "users": [
      {
        "username": "user001",
        "nickname": "小明",
        "avatar": "/uploads/avatar/001.jpg",
        "is_leader": true,
        "join_time": "2026-04-16T18:00:00"
      },
      {
        "username": "user002",
        "nickname": "小红",
        "avatar": "/uploads/avatar/002.jpg",
        "is_leader": false,
        "join_time": "2026-04-16T18:30:00"
      }
    ],
    "current_user_joined": false,
    "can_join": true,
    "wechat_group_qr": null
  }
}
```

### 4. 响应字段说明

| 字段名              | 类型              | 说明                                          |
| ------------------- | ----------------- | --------------------------------------------- |
| id                  | Long              | 组团 ID                                        |
| group_activity_id   | Long              | 关联拼团活动 ID                                 |
| activity_title      | String            | 活动标题                                       |
| goods_type          | String            | 商品类型                                       |
| goods_id            | Long              | 商品 ID                                        |
| goods_snapshot      | Object            | 商品快照                                       |
| original_price      | BigDecimal        | 商品原价                                       |
| group_price         | BigDecimal        | 拼团价格                                       |
| num                 | Integer           | 已参团人数                                      |
| total               | Integer           | 成团所需总人数                                  |
| remain              | Integer           | 还差几人成团（total - num）                     |
| status              | Integer           | 组团状态：0-进行中 1-已成团 2-已取消/过期        |
| status_text         | String            | 状态文字描述                                    |
| expire              | String            | 组团过期时间                                    |
| created_time        | String            | 组团发起时间                                    |
| leader              | GroupUserVo       | 团长信息                                       |
| users               | List              | 参团用户列表（含 is_leader 和 join_time）        |
| current_user_joined | Boolean           | 当前登录用户是否已加入该团（未登录时为 false）    |
| can_join            | Boolean           | 当前是否可以加入（综合状态、人数、过期时间判断）  |
| wechat_group_qr     | String            | 微信群二维码 URL（仅已成团时返回）                |

### 5. 业务逻辑

```
1. 查询 osh_group_work（groupWorkId），校验存在性
2. 关联查询 osh_group_activity 获取活动信息和商品快照
3. 查询 osh_group_user + osh_user 获取参团用户列表（含 is_leader、join_time）
4. 提取 is_leader = 1 的用户作为 leader
5. 计算 remain = total - num
6. 判断 can_join：
   a. status == 0（进行中）
   b. num < total（未满员）
   c. expire > NOW()（未过期）
   d. 关联活动 status == 1 且在有效期内
   e. 当前用户未加入该团（current_user_joined == false）
   f. 当前用户未超过 per_user_limit
7. 若传入 token 且有效：
   a. 获取 userId
   b. 查询 osh_group_user 中是否有 (group_work_id, userId) 的记录
   c. 设置 current_user_joined
8. wechat_group_qr 仅在 status == 1（已成团）时返回
9. 状态文字映射：0→"进行中" 1→"已成团" 2→"已取消"
```

### 6. 错误码

| code | msg        | 场景                         |
| ---- | ---------- | ---------------------------- |
| 200  | 操作成功    | 正常返回                      |
| 500  | 组团不存在  | groupWorkId 无对应记录         |

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
