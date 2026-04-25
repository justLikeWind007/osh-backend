-- 为 osh_course_section 表添加 section_type 字段（与代码保持一致）
-- 解决 Unknown column 'section_type' 错误

ALTER TABLE `osh_course_section` 
ADD COLUMN `section_type` varchar(20) DEFAULT NULL COMMENT '章节类型：video/audio/text/live' AFTER `is_free`;

-- 验证字段是否添加成功
-- SHOW COLUMNS FROM `osh_course_section` LIKE 'section_type';
