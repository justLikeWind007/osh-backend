# Flink StreamingJob 测试指南

## 当前状态
✅ 代码已修复（Date 字段转为时间戳，避免 ES 日期格式错误）
✅ 项目已编译成功
✅ Kafka topic 中已有 100 条测试数据
✅ ES 索引已清空，准备接收新数据

## 测试步骤

### 1. 运行 StreamingJob（在 IDEA 中 Debug 模式）

1. 打开文件：`src/test/java/com/backstage/test_from_kafka_to_es/StreamingJob.java`
2. 在关键位置设置断点：
   - 第 108 行：`System.out.println("【创建流】收到消息: " + value);` - 查看接收到的 Kafka 消息
   - 第 115 行：`boolean pass = ...` - 查看过滤逻辑
   - 第 165 行：`String jsonString = buildJsonWithTimestamp(message);` - 查看 JSON 序列化
3. 右键点击 `StreamingJob.java` → **Debug 'StreamingJob.main()'**
4. 观察控制台输出和断点触发

### 2. 观察控制台输出

你应该看到类似这样的输出：
```
========================================
Flink StreamingJob 启动中...
========================================
配置信息:
  Kafka: 43.242.200.25:9092
  消费者组: flink-course-index-consumer
  创建 Topic: osh.course.index.create
  更新 Topic: osh.course.index.update
  ES: http://43.242.200.25:9200
  ES 索引: osh_course_index
========================================

正在连接 Kafka 和 ES...
Flink 任务已启动，等待消息...

【创建流】收到消息: {"courseId":1,"title":"测试课程-1",...}
【创建流】过滤检查 - 课程ID: 1, 状态: 1, 是否通过: true
【ES写入】课程ID: 1, 标题: 测试课程-1
...
```

### 3. 验证 ES 数据

在另一个终端运行以下命令：

```bash
# 查看索引是否创建
curl -u elastic:osh88888888 -X GET "http://43.242.200.25:9200/_cat/indices?v" | grep osh_course

# 查看文档数量（应该约 70 条，因为只有 status=1 的课程会被写入）
curl -u elastic:osh88888888 -X GET "http://43.242.200.25:9200/osh_course_index/_count?pretty"

# 查看前 3 条数据
curl -u elastic:osh88888888 -X GET "http://43.242.200.25:9200/osh_course_index/_search?pretty&size=3"

# 查看某个具体课程（例如 ID=1）
curl -u elastic:osh88888888 -X GET "http://43.242.200.25:9200/osh_course_index/_doc/1?pretty"
```

### 4. 验证日期字段格式

检查 ES 中的日期字段是否为时间戳格式（毫秒）：
```bash
curl -u elastic:osh88888888 -X GET "http://43.242.200.25:9200/osh_course_index/_doc/1?pretty" | grep -A 2 "Time"
```

应该看到类似：
```json
"createTime" : 1745334470567,
"updateTime" : 1745334470567,
```

## 预期结果

- ✅ Flink 成功消费 Kafka 中的 100 条消息
- ✅ 过滤掉 status=0 的课程（约 30 条）
- ✅ 写入 ES 约 70 条 status=1 的课程
- ✅ 日期字段以时间戳格式存储，没有解析错误
- ✅ 所有课程数据完整，包含标题、价格、标签等字段

## 调试技巧

1. **单步调试**：在断点处按 F8 单步执行，查看变量值
2. **查看 message 对象**：在断点处展开 `message` 变量，查看所有字段
3. **查看 JSON 字符串**：在 `buildJsonWithTimestamp` 方法中查看生成的 JSON
4. **观察过滤逻辑**：查看哪些课程被过滤掉（status=0）

## 常见问题

### Q: Flink 任务启动后没有输出？
A: 检查 Kafka 连接是否正常，确认 topic 中有数据

### Q: ES 写入失败？
A: 检查 ES 连接、认证信息是否正确

### Q: 日期格式错误？
A: 已修复，现在使用时间戳格式

## 下一步

测试成功后，你可以：
1. 修改过滤条件，测试不同的业务逻辑
2. 添加更多的数据转换逻辑
3. 学习 Flink 的窗口、聚合等高级功能
