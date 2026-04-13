-- 为 osh_course_section 表添加缺失的字段
-- 直接执行 ALTER TABLE 语句

-- 添加 cover 字段（封面图片 URL）
ALTER TABLE osh_course_section ADD COLUMN cover VARCHAR(500) DEFAULT NULL COMMENT '封面图片 URL' AFTER media_url;

-- 添加 video_codec 字段（视频编码格式）
ALTER TABLE osh_course_section ADD COLUMN video_codec VARCHAR(50) DEFAULT NULL COMMENT '视频编码格式（如 H.264, H.265）' AFTER cover;

-- 添加 video_bitrate 字段（视频比特率）
ALTER TABLE osh_course_section ADD COLUMN video_bitrate INT DEFAULT NULL COMMENT '视频比特率（单位：kbps）' AFTER video_codec;

-- 添加 video_resolution 字段（视频分辨率）
ALTER TABLE osh_course_section ADD COLUMN video_resolution VARCHAR(20) DEFAULT NULL COMMENT '视频分辨率（如 1920x1080）' AFTER video_bitrate;

-- 添加 file_size 字段（文件大小）
ALTER TABLE osh_course_section ADD COLUMN file_size BIGINT DEFAULT NULL COMMENT '文件大小（字节）' AFTER video_resolution;

-- 添加 subtitle_url 字段（字幕文件 URL）
ALTER TABLE osh_course_section ADD COLUMN subtitle_url VARCHAR(500) DEFAULT NULL COMMENT '字幕文件 URL' AFTER file_size;
