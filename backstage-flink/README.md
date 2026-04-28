# Flink 课程索引项目

## 项目说明

从 Kafka 消费课程数据，过滤后写入 Elasticsearch。

**核心功能：**
- 从 Kafka 读取课程创建/更新消息
- 过滤：只保留 `status=1`（已发布）的课程
- 写入 Elasticsearch 进行索引

**数据流程：**
```
Kafka → Flink 消费 → 过滤(status=1) → Elasticsearch
```

---

## 快速开始

### 1. 启动 Flink 任务（Debug 模式）

在 IDEA 中：
1. 打开 `src/test/java/com/backstage/test_from_kafka_to_es/StreamingJob.java`
2. 右键选择 **"Debug 'StreamingJob.main()'"**
3. 等待任务启动

**设置断点（可选）：**
- 第 95 行：过滤逻辑 `return pass;`
- 第 138 行：ES 写入 `.source(...)`

### 2. 发送测试数据

在 IDEA 中：
1. 打开 `src/test/java/com/backstage/test_from_kafka_to_es/TestDataProducer.java`
2. 右键选择 **"Run 'TestDataProducer.main()'"**
3. 会发送 100 条测试数据（70% status=1，30% status=0）

### 3. 观察结果

**控制台日志：**
```
【创建流】收到消息: {"courseId":1,...}
【创建流】过滤检查 - 课程ID: 1, 状态: 1, 是否通过: true
【ES写入】课程ID: 1, 标题: 测试课程-1
```

**查询 ES：**
```bash
# 查看文档数量（应该约 70 条）
curl -u elastic:osh88888888 "http://43.242.200.25:9200/osh_course_index/_count"

# 查看数据
curl -u elastic:osh88888888 "http://43.242.200.25:9200/osh_course_index/_search?size=10&pretty"
```

---

## 配置信息

**Kafka：**
- 地址：`43.242.200.25:9092`
- Topic：`osh.course.index.create` / `osh.course.index.update`
- 消费者组：`osh-course-index-group`

**Elasticsearch：**
- 地址：`http://43.242.200.25:9200`
- 索引：`osh_course_index`
- 认证：`elastic / osh88888888`

---

## 项目结构

```
backstage-flink/
├── src/
│   └── test/java/com/backstage/test_from_kafka_to_es/
│       ├── StreamingJob.java          # 主任务：消费 Kafka，过滤，写入 ES
│       ├── TestDataProducer.java      # 测试数据生产者：发送 100 条测试数据
│       └── course/
│           ├── CourseIndexJobConfig.java    # 配置类
│           └── CourseIndexMessage.java      # 课程消息实体
├── pom.xml                            # Maven 配置
└── README.md                          # 本文档
```

---

## 调试技巧

**IDEA 调试快捷键：**
- **F8**：单步执行（Step Over）
- **F7**：进入方法（Step Into）
- **F9**：继续执行到下一个断点（Resume）
- **Ctrl+F8**：切换断点

**观察变量：**
- 在断点处查看 `message` 对象的所有字段
- 查看 `message.getStatus()` 的值
- 查看 `pass` 变量（true/false）

---

## 常见问题

**Q: 断点不触发？**
- 确保已运行 `TestDataProducer` 发送数据
- 检查 Kafka 是否可访问

**Q: 数据没有写入 ES？**
- 检查过滤逻辑（只有 status=1 会写入）
- 查看 Flink 任务的控制台日志
- 确认 ES 可访问

**Q: 想重新测试？**
```bash
# 删除 ES 索引
curl -u elastic:osh88888888 -X DELETE "http://43.242.200.25:9200/osh_course_index"

# 重新运行 StreamingJob 和 TestDataProducer
```

---

## 学习要点

### Flink 数据流
```java
DataStream<String> source = env.addSource(...)  // 数据源
    .map(...)      // 转换
    .filter(...)   // 过滤
    .addSink(...); // 输出
```

### 过滤器
```java
.filter(message -> {
    return message.getStatus() == 1;  // true=保留，false=丢弃
})
```

### Kafka 消费
- `bootstrap.servers`：Kafka 地址
- `group.id`：消费者组
- `auto.offset.reset`：latest（最新）/ earliest（最早）

### ES 写入
- `IndexRequest`：创建文档
- `UpdateRequest`：更新文档（upsert）
- `bulkFlushMaxActions`：批量大小

---

## 编译和打包

```bash
# 编译
mvn clean compile

# 打包（生成 jar）
mvn clean package

# 运行（命令行方式）
mvn exec:java -Dexec.mainClass="com.backstage.test_from_kafka_to_es.StreamingJob"
```

---

## 技术栈

- **Flink**: 1.8.0
- **Kafka**: Flink Kafka Connector
- **Elasticsearch**: 6.8.23 客户端
- **Java**: 8
- **FastJSON**: 2.x

---

祝学习愉快！🚀
