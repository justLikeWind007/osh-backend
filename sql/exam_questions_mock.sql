-- =============================================
-- 考试模块假题目数据
-- 给 hu-20 (exam_id=246) 插入10道题，覆盖全部题型
-- 题型：radio(单选) / checkbox(多选) / trueOrfalse(判断) / completion(填空) / answer(简答)
-- options 字段：单选/多选用 JSON 数组，判断/填空/简答为 NULL
-- correct_answer：单选存选项索引(0-based)，多选存逗号分隔索引，判断存 true/false，填空/简答存参考答案
-- =============================================

INSERT INTO `osh_question`
    (`exam_id`, `title`, `score`, `type`, `remark`, `options`, `correct_answer`, `delete_flag`)
VALUES

-- ── 单选题 ×4 ──────────────────────────────────────────────────────────────
(246,
 'Java 中，以下哪个关键字用于实现接口？',
 10, 'radio',
 '基础语法题',
 '["extends","implements","interface","abstract"]',
 '1',
 0),

(246,
 'HTTP 状态码 404 表示什么？',
 10, 'radio',
 '网络基础题',
 '["服务器内部错误","请求超时","资源未找到","请求被拒绝"]',
 '2',
 0),

(246,
 'MySQL 中，以下哪个语句用于查询数据？',
 10, 'radio',
 'SQL 基础题',
 '["INSERT","UPDATE","SELECT","DELETE"]',
 '2',
 0),

(246,
 'Spring Boot 默认内嵌的 Web 服务器是？',
 10, 'radio',
 'Spring Boot 基础题',
 '["Jetty","Undertow","Tomcat","Nginx"]',
 '2',
 0),

-- ── 多选题 ×2 ──────────────────────────────────────────────────────────────
(246,
 '以下哪些是 Java 的基本数据类型？（多选）',
 10, 'checkbox',
 '多选，选出所有正确答案',
 '["int","String","boolean","double","List"]',
 '0,2,3',
 0),

(246,
 'RESTful API 常用的 HTTP 方法有哪些？（多选）',
 10, 'checkbox',
 '多选，选出所有正确答案',
 '["GET","POST","CONNECT","PUT","DELETE"]',
 '0,1,3,4',
 0),

-- ── 判断题 ×2 ──────────────────────────────────────────────────────────────
(246,
 'Java 中 String 是基本数据类型。',
 10, 'trueOrfalse',
 '判断对错',
 NULL,
 'false',
 0),

(246,
 'Git 中 git pull 等价于 git fetch + git merge。',
 10, 'trueOrfalse',
 '判断对错',
 NULL,
 'true',
 0),

-- ── 填空题 ×1 ──────────────────────────────────────────────────────────────
(246,
 'Spring MVC 中，用于将请求路径映射到方法的注解是 ______。',
 10, 'completion',
 '填写注解名称（含@符号）',
 NULL,
 '@RequestMapping',
 0),

-- ── 简答题 ×1 ──────────────────────────────────────────────────────────────
(246,
 '请简述 Redis 和 MySQL 的主要区别及各自适用场景。',
 10, 'answer',
 '言之有理即可得分',
 NULL,
 'Redis 是内存数据库，读写速度极快，适合缓存、会话、排行榜等高频读写场景；MySQL 是关系型数据库，数据持久化，适合结构化数据存储和复杂查询。',
 0);
