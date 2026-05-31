-- 实用网站标签表新增 use_count 字段
-- 参考课程模块 osh_course_tag 表结构，用于记录标签被关联的网站数量
-- 执行时间：2026-05-30

ALTER TABLE osh_website_tag
    ADD COLUMN use_count INT NOT NULL DEFAULT 0 COMMENT '使用次数（关联该标签的网站数量）' AFTER sort_order;
