# @DistributeLock 注解设计与使用说明

## 1. 作用概述

`@DistributeLock` 是当前系统提供的分布式锁注解，用于在方法执行前自动加 Redis 分布式锁，方法执行后按配置释放锁或等待锁自然过期。

当前实现由 `DistributeLockAspect` 切面完成，底层使用 Redisson 的 `RLock`。

它主要解决的问题是：在多实例部署、接口重复提交、异步任务重复消费、后台操作并发执行等场景下，避免同一份业务资源被多个线程或多个服务节点同时处理。

## 2. 设计原理

### 2.1 基于 AOP 拦截方法

只要方法上标记了：

```java
@DistributeLock(...)
```

`DistributeLockAspect` 就会在方法执行前拦截。

执行流程如下：

1. 读取注解参数
2. 计算锁 key
3. 从 Redisson 获取 `RLock`
4. 根据 `waitTime` 和 `expireTime` 选择加锁方式
5. 加锁成功后执行原方法
6. 根据 `releaseImmediately` 决定是否在 `finally` 中立即释放锁
7. 加锁失败时抛出 `DistributeLockException`

### 2.2 锁 key 的组成

最终锁 key 由三部分组成：

```text
scene#key
```

如果 `includeUserId = true`，并且当前线程中能拿到用户 ID，则会变成：

```text
scene#user:用户ID#key
```

例如：

```java
@DistributeLock(scene = "resource", key = "operation")
```

当前用户 ID 为 `10001` 时，最终锁 key 是：

```text
resource#user:10001#operation
```

如果 `includeUserId = false`，最终锁 key 是：

```text
resource#operation
```

### 2.3 key 和 keyExpression 的优先级

注解中可以通过两种方式指定锁 key：

```java
key = "固定值"
```

或者：

```java
keyExpression = "#request.id"
```

优先级规则：

1. 优先使用 `key`
2. 如果 `key` 是默认值 `NONE`，才会使用 `keyExpression`
3. 如果两个都没配置，会抛出 `DistributeLockException`

例如：

```java
@DistributeLock(scene = "course:update", keyExpression = "#request.id", includeUserId = false)
public R<Long> update(@RequestBody CourseUpdateRequest request) {
    ...
}
```

当 `request.id = 10` 时，最终锁 key 是：

```text
course:update#10
```

## 3. 注解参数说明

当前注解定义：

```java
public @interface DistributeLock {

    String scene() default "";

    String key() default "NONE";

    String keyExpression() default "NONE";

    boolean includeUserId() default true;

    int expireTime() default -1;

    int waitTime() default -1;

    boolean releaseImmediately() default true;
}
```

## 4. 参数详细解释

### 4.1 scene：业务场景

`scene` 用来区分不同业务域。

推荐写法：

```java
@DistributeLock(scene = "course:update", keyExpression = "#request.id")
```

效果：

- 课程更新和订单支付不会因为 key 相同而误抢同一把锁
- 日志中可以直接看出锁属于哪个业务场景
- 后续排查 Redis 锁 key 更清楚

不推荐：

```java
@DistributeLock(key = "operation")
```

虽然能用，但场景为空，排查问题时不直观。

### 4.2 key：固定锁 key

`key` 适合锁定某一类固定操作。

示例：

```java
@DistributeLock(scene = "resource", key = "operation", expireTime = 10000, waitTime = 3000)
public R<Long> save(@RequestBody CourseCreateRequest request) {
    ...
}
```

效果：

- 同一个用户的 `resource#operation` 操作会串行执行
- 如果 3 秒内拿不到锁，会抛出异常
- 锁最多持有 10 秒
- 方法执行结束后默认立即释放锁

适合场景：

- 防止用户重复点击提交
- 限制某类资源操作并发
- 不需要按具体业务 ID 细分锁粒度

### 4.3 keyExpression：动态锁 key

`keyExpression` 使用 SpEL 表达式从方法参数中取值。

示例：

```java
@DistributeLock(scene = "course:update", keyExpression = "#request.id", includeUserId = false, waitTime = 0)
public R<Long> update(@RequestBody CourseUpdateRequest request) {
    ...
}
```

效果：

- 同一个课程 ID 的更新请求互斥
- 不同课程 ID 可以并发执行
- 不区分用户，因为课程是公共资源
- 拿不到锁立即失败

例如：

```text
用户 A 修改 courseId=1
用户 B 修改 courseId=1
```

这两个请求会抢同一把锁：

```text
course:update#1
```

但：

```text
用户 A 修改 courseId=1
用户 B 修改 courseId=2
```

这两个请求使用不同锁：

```text
course:update#1
course:update#2
```

可以并发执行。

### 4.4 includeUserId：是否拼接用户 ID

默认值：

```java
includeUserId = true
```

表示锁 key 中会拼接当前用户 ID。

示例：

```java
@DistributeLock(scene = "website_submit", key = "website_submit_lock", includeUserId = true, waitTime = 0)
public int submitWebsite(WebsiteSubmitDTO submitDto) {
    ...
}
```

效果：

```text
website_submit#user:10001#website_submit_lock
website_submit#user:10002#website_submit_lock
```

用户 `10001` 和用户 `10002` 使用不同锁，因此互不影响。

适合场景：

- 每个用户不能重复提交
- 每个用户自己的操作要串行
- 不同用户之间可以并发

例如：

```text
用户 A 连续点击两次“提交网站”
```

第二次请求会因为抢不到同一把锁而失败。

但：

```text
用户 A 提交网站
用户 B 提交网站
```

两个用户可以同时提交。

如果锁的是公共资源，要关闭用户维度：

```java
@DistributeLock(scene = "course:update", keyExpression = "#request.id", includeUserId = false)
```

否则不同用户修改同一门课程时会拿到不同锁，达不到公共资源互斥的效果。

### 4.5 waitTime：等待拿锁时间

`waitTime` 单位是毫秒。

当前实现规则：

| waitTime | 效果 |
|---|---|
| `-1` | 阻塞等待，直到拿到锁 |
| `0` | 立即尝试拿锁，拿不到马上失败 |
| `> 0` | 最多等待指定毫秒数，超时失败 |

#### 效果一：阻塞等待

```java
@DistributeLock(scene = "job:sync", key = "full", waitTime = -1)
public void syncData() {
    ...
}
```

效果：

- 如果当前锁被占用，后续请求会一直等待
- 等前一个任务执行完释放锁后，后一个任务继续执行

适合：

- 后台任务
- 队列消费
- 不希望直接失败的内部流程

不太适合普通接口，因为用户可能一直卡住。

#### 效果二：快速失败

```java
@DistributeLock(scene = "order:pay", keyExpression = "#request.orderId", includeUserId = false, waitTime = 0)
public R<Void> pay(@RequestBody PayRequest request) {
    ...
}
```

效果：

- 同一订单正在支付时，重复支付请求立即失败
- 前端可以提示“处理中，请勿重复提交”

适合：

- 支付
- 提交表单
- 审核按钮
- 用户可感知的接口操作

#### 效果三：等待一段时间

```java
@DistributeLock(scene = "resource", key = "operation", expireTime = 10000, waitTime = 3000)
public R<Long> save(@RequestBody CourseCreateRequest request) {
    ...
}
```

效果：

- 如果锁被占用，最多等 3 秒
- 3 秒内锁释放，则继续执行
- 超过 3 秒仍拿不到锁，则失败

适合：

- 希望减少失败率
- 但又不希望接口无限等待的场景

### 4.6 expireTime：锁过期时间

`expireTime` 单位是毫秒。

当前实现规则：

| expireTime | 效果 |
|---|---|
| `-1` | 不手动指定租约时间，由 Redisson 看门狗自动续期 |
| `> 0` | 指定锁最多持有时间，到期后自动释放 |

#### 效果一：默认自动续期

```java
@DistributeLock(scene = "job:import", key = "excel", waitTime = -1)
public void importExcel() {
    ...
}
```

效果：

- 方法执行时间不确定时，Redisson 会自动续期
- 避免业务还没执行完锁就过期

适合：

- 执行时间不可预估的任务
- 大文件处理
- 第三方接口耗时不稳定的流程

#### 效果二：指定过期时间

```java
@DistributeLock(scene = "resource", key = "operation", expireTime = 10000, waitTime = 3000)
public R<Long> save(@RequestBody CourseCreateRequest request) {
    ...
}
```

效果：

- 锁最多持有 10 秒
- 即使服务异常退出，锁也会在 10 秒后自动释放
- 正常情况下，方法执行完会立即释放锁

适合：

- 执行时间比较确定的接口
- 需要避免异常情况下锁长时间存在的场景

注意：

- `expireTime` 设置太短，可能方法没执行完锁就过期，导致并发请求进入
- `expireTime` 设置太长，异常时其他请求等待时间会变长

### 4.7 releaseImmediately：是否方法结束立即释放锁

默认值：

```java
releaseImmediately = true
```

表示方法执行结束后，如果当前线程还持有锁，会在 `finally` 中立即释放。

#### 效果一：方法结束立即释放

```java
@DistributeLock(
    scene = "resource",
    key = "operation",
    expireTime = 10000,
    waitTime = 3000,
    releaseImmediately = true
)
public R<Long> save(@RequestBody CourseCreateRequest request) {
    ...
}
```

效果：

- 请求进入前加锁
- 方法执行期间其他相同锁请求不能进入
- 方法结束后立即解锁
- 后续请求可以马上进入

适合大多数普通接口。

#### 效果二：方法结束后不立即释放，等过期时间结束

```java
@DistributeLock(
    scene = "course:audit",
    key = "api",
    expireTime = 60000,
    waitTime = 0,
    releaseImmediately = false
)
public R<Long> audit(@RequestBody CourseAuditRequest request) {
    ...
}
```

效果：

- 请求执行完成后不会主动 `unlock`
- 锁会保留到 `expireTime` 到期
- 60 秒内相同锁 key 的请求都会失败或等待

适合：

- 审核类接口防连点
- 需要做冷却时间的接口
- 不希望方法刚执行完就立刻被再次触发的操作

注意：

当 `releaseImmediately = false` 时，必须显式配置 `expireTime`，否则系统会抛出异常：

```text
expireTime must be configured when releaseImmediately is false
```

原因是如果不设置过期时间，又不立即释放锁，锁可能长期不释放。

## 5. 能做到什么效果

### 5.1 防止重复提交

示例：

```java
@DistributeLock(scene = "website_submit", key = "website_submit_lock", includeUserId = true, waitTime = 0)
public int submitWebsite(WebsiteSubmitDTO submitDto) {
    ...
}
```

效果：

- 同一个用户重复点击提交，只会有一个请求进入
- 第二个请求拿不到锁会立即失败
- 不同用户提交互不影响

适合：

- 网站提交
- 表单提交
- 报名
- 申请入驻
- 领取奖励

### 5.2 同一资源串行修改

示例：

```java
@DistributeLock(scene = "course:update", keyExpression = "#request.id", includeUserId = false, waitTime = 0)
public R<Long> update(@RequestBody CourseUpdateRequest request) {
    ...
}
```

效果：

- 同一门课程只能被一个请求修改
- 不同课程可以并发修改
- 不同用户修改同一门课程也会互斥

适合：

- 修改课程
- 修改订单
- 修改工具
- 更新审核状态
- 调整库存

### 5.3 全局互斥操作

示例：

```java
@DistributeLock(scene = "system:sync", key = "all", includeUserId = false, waitTime = 0, expireTime = 60000)
public void syncAllData() {
    ...
}
```

效果：

- 整个系统同一时刻只允许一个同步任务执行
- 即使多节点部署，也只有一个节点能拿到锁

适合：

- 全量同步
- 批量导入
- 重新生成缓存
- 定时任务防多实例重复执行

### 5.4 用户维度串行

示例：

```java
@DistributeLock(scene = "coupon:receive", key = "submit", includeUserId = true, waitTime = 0)
public R<Void> receiveCoupon() {
    ...
}
```

效果：

- 同一用户领取动作串行
- 不同用户可以同时领取
- 锁粒度是“用户 + 场景 + 操作”

适合：

- 领取优惠券
- 用户签到
- 用户报名
- 用户提交申请

### 5.5 业务冷却时间

示例：

```java
@DistributeLock(
    scene = "sms:send",
    key = "code",
    includeUserId = true,
    expireTime = 60000,
    waitTime = 0,
    releaseImmediately = false
)
public R<Void> sendSmsCode() {
    ...
}
```

效果：

- 用户发送一次验证码后，60 秒内不能再次发送
- 即使方法很快执行完成，也不会立即释放锁
- 锁自然过期后才允许再次发送

适合：

- 短信验证码
- 邮件验证码
- 审核提交冷却
- 防频繁点击按钮

### 5.6 多节点互斥

假设系统部署了 3 个后端实例：

```text
server-1
server-2
server-3
```

三个实例同时收到同一订单支付请求。

示例：

```java
@DistributeLock(scene = "order:pay", keyExpression = "#request.orderId", includeUserId = false, waitTime = 0)
public R<Void> pay(@RequestBody PayRequest request) {
    ...
}
```

效果：

- 三个实例都会去 Redis 抢同一把锁
- 只有一个实例能成功执行支付逻辑
- 其他实例拿锁失败，直接返回失败
- 避免多节点环境下重复处理同一订单

## 6. 当前系统中的实际使用

### 6.1 课程新增/修改

当前代码：

```java
@DistributeLock(scene = "resource", key = "operation", expireTime = 10000, waitTime = 3000, releaseImmediately = true)
public R<Long> save(@RequestBody CourseCreateRequest request) {
    ...
}
```

实际效果：

- 同一用户的资源操作会串行
- 最多等待 3 秒拿锁
- 锁最多持有 10 秒
- 方法执行结束后立即释放锁

由于 `includeUserId` 默认是 `true`，所以不同用户之间不会互相阻塞。

### 6.2 工具新增/修改

当前代码：

```java
@DistributeLock(scene = "resource", key = "operation", expireTime = 10000, waitTime = 3000, releaseImmediately = true)
public R<Long> save(@RequestBody ToolSaveRequest request) {
    ...
}
```

实际效果和课程资源操作类似：

- 防止同一用户重复提交资源操作
- 给 3 秒等待窗口
- 正常执行结束后释放锁

### 6.3 课程审核

当前代码：

```java
@DistributeLock(scene = "course:audit", key = "api", expireTime = 60000, waitTime = 0, releaseImmediately = false)
public R<Long> audit(@RequestBody CourseAuditRequest request) {
    ...
}
```

实际效果：

- 审核接口抢不到锁立即失败
- 抢到锁后，即使方法执行完成，也不会立即释放
- 锁会保留 60 秒
- 60 秒内同一用户不能再次触发同一审核锁

这个写法更像“接口冷却锁”，不是单纯的方法执行期间互斥。

## 7. 推荐使用规范

### 7.1 优先明确 scene

推荐：

```java
scene = "course:update"
scene = "order:pay"
scene = "website:submit"
```

不推荐大量使用：

```java
scene = "resource"
key = "operation"
```

粗粒度锁虽然简单，但可能导致无关操作互相影响。

### 7.2 公共资源锁要关闭 includeUserId

如果锁的是课程、订单、工具、库存等公共资源，建议：

```java
includeUserId = false
```

示例：

```java
@DistributeLock(scene = "course:update", keyExpression = "#request.id", includeUserId = false)
```

否则不同用户操作同一资源时，锁 key 不同，无法实现公共资源互斥。

### 7.3 用户自己的操作才使用 includeUserId=true

适合：

```java
@DistributeLock(scene = "website:submit", key = "submit", includeUserId = true)
```

这表示“每个用户自己的提交串行”。

### 7.4 接口请求建议使用 waitTime=0 或短等待

推荐：

```java
waitTime = 0
```

或者：

```java
waitTime = 3000
```

普通接口不建议无限阻塞等待，否则用户体验不好。

### 7.5 releaseImmediately=false 必须配置 expireTime

正确：

```java
@DistributeLock(scene = "sms:send", key = "code", expireTime = 60000, waitTime = 0, releaseImmediately = false)
```

错误：

```java
@DistributeLock(scene = "sms:send", key = "code", releaseImmediately = false)
```

错误原因：

```text
不立即释放锁时，必须让锁有明确过期时间。
```

### 7.6 分布式锁不是数据库约束的替代品

分布式锁可以减少重复请求和并发冲突，但不能替代：

- 数据库唯一索引
- 事务
- 状态机校验
- 乐观锁
- 幂等表

推荐组合：

```text
分布式锁：入口防重
业务校验：过程防错
数据库约束：最终兜底
```

## 8. 常见问题

### 8.1 为什么拿不到锁会抛异常？

当前实现中，如果 `tryLock` 返回 `false`，会抛出：

```text
DistributeLockException: acquire lock failed...
```

业务层可以通过全局异常处理统一转换成用户可读提示，例如：

```text
操作正在处理中，请勿重复提交
```

### 8.2 keyExpression 取不到参数怎么办？

例如：

```java
@DistributeLock(keyExpression = "#request.id")
```

要求方法参数名能被正确发现，并且确实存在 `request` 参数。

正确示例：

```java
public R<Long> update(@RequestBody CourseUpdateRequest request) {
    ...
}
```

如果参数名或字段路径写错，表达式解析结果可能不是预期值。

### 8.3 expireTime 该设置多久？

建议按业务最大执行时间设置，并留一点余量。

例如：

- 普通保存接口：`10000`
- 审核冷却：`60000`
- 短信验证码：`60000`
- 大批量任务：优先使用默认自动续期，或者设置足够长的时间

### 8.4 什么时候用固定 key，什么时候用动态 key？

固定 key：

```java
key = "operation"
```

适合锁一类操作。

动态 key：

```java
keyExpression = "#request.id"
```

适合锁具体业务资源。

一般建议优先使用动态 key，因为锁粒度更小，并发性能更好。
