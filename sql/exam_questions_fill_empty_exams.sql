-- =============================================================================
-- Fill mock questions for every PUBLISHED exam that currently has ZERO rows
-- in osh_question (delete_flag = 0). Safe to re-run: skips exams that already
-- have at least one question.
--
-- Usage (example):
--   mysql -h127.0.0.1 -P3306 -uUSER -p YOUR_DB < sql/exam_questions_fill_empty_exams.sql
--
-- 若 Navicat 报错 Error 1175：与「安全更新模式」有关，本脚本已临时关闭 sql_safe_updates。
-- 若报 Unknown column 'delete_flag'：请先执行 sql/migrate_delete_flag.sql 统一字段。
-- =============================================================================

SET SESSION sql_safe_updates = 0;

-- 8 questions per empty exam: radio x4, checkbox x1, trueOrfalse x1, completion x1, answer x1
-- 审计字段：表上 create_by 等为 NOT NULL 且无默认值时必须显式写入（否则 1364）
INSERT INTO `osh_question` (
    `exam_id`, `title`, `score`, `type`, `remark`, `options`, `correct_answer`, `delete_flag`,
    `create_by`, `create_time`, `update_by`, `update_time`
)
SELECT
    e.id,
    t.title,
    t.score,
    t.type,
    t.remark,
    t.options,
    t.correct_answer,
    0,
    'sql_seed',
    NOW(),
    'sql_seed',
    NOW()
FROM `osh_examination` e
CROSS JOIN (
    SELECT
        '【Mock】Java 中，用于实现接口的关键字是？' AS title,
        10 AS score,
        'radio' AS type,
        '练习用自动造题' AS remark,
        '["extends","implements","interface","abstract"]' AS options,
        '1' AS correct_answer
    UNION ALL SELECT
        '【Mock】HTTP 状态码 404 表示什么？',
        10,
        'radio',
        '练习用自动造题',
        '["服务器内部错误","请求超时","资源未找到","请求被拒绝"]',
        '2'
    UNION ALL SELECT
        '【Mock】MySQL 中，用于查询数据的语句是？',
        10,
        'radio',
        '练习用自动造题',
        '["INSERT","UPDATE","SELECT","DELETE"]',
        '2'
    UNION ALL SELECT
        '【Mock】Spring Boot 默认内嵌的 Web 服务器是？',
        10,
        'radio',
        '练习用自动造题',
        '["Jetty","Undertow","Tomcat","Nginx"]',
        '2'
    UNION ALL SELECT
        '【Mock】以下哪些是 Java 的基本数据类型？（多选）',
        10,
        'checkbox',
        '练习用自动造题',
        '["int","String","boolean","double","List"]',
        '0,2,3'
    UNION ALL SELECT
        '【Mock】Java 中 String 是基本数据类型。',
        10,
        'trueOrfalse',
        '练习用自动造题',
        NULL,
        'false'
    UNION ALL SELECT
        '【Mock】Spring MVC 中，将请求路径映射到方法的注解是 ______。',
        10,
        'completion',
        '练习用自动造题',
        NULL,
        '@RequestMapping'
    UNION ALL SELECT
        '【Mock】请用一句话说明 Redis 与 MySQL 的典型使用场景。',
        15,
        'answer',
        '练习用自动造题',
        NULL,
        'Redis 多用于缓存与高频读写；MySQL 多用于持久化与复杂查询。'
) t
WHERE e.`delete_flag` = 0
  AND e.`status` = 1
  AND NOT EXISTS (
      SELECT 1
      FROM `osh_question` q
      WHERE q.`exam_id` = e.`id`
        AND q.`delete_flag` = 0
      LIMIT 1
  );

-- Sync exam header stats（避免多表 UPDATE + 派生表在 Navicat 安全更新 / 部分版本下报错，改为标量子查询）
UPDATE `osh_examination` e
SET
    `question_count` = (
        SELECT COUNT(*) FROM `osh_question` q
        WHERE q.`exam_id` = e.`id` AND q.`delete_flag` = 0
    ),
    `total_score` = COALESCE((
        SELECT SUM(q.`score`) FROM `osh_question` q
        WHERE q.`exam_id` = e.`id` AND q.`delete_flag` = 0
    ), 0),
    `pass_score` = CAST(
        CEILING(COALESCE((
            SELECT SUM(q.`score`) FROM `osh_question` q
            WHERE q.`exam_id` = e.`id` AND q.`delete_flag` = 0
        ), 0) * 0.6)
        AS SIGNED
    )
WHERE e.`delete_flag` = 0
  AND EXISTS (
      SELECT 1 FROM `osh_question` q
      WHERE q.`exam_id` = e.`id` AND q.`delete_flag` = 0
      LIMIT 1
  );

SET SESSION sql_safe_updates = 1;
