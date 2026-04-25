-- 为 osh_course_section 表添加缺失的字段
-- 使用存储过程方式检查字段是否存在

DELIMITER $$

-- 添加 cover 字段（封面图片 URL）
CREATE PROCEDURE add_cover_column()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS 
                   WHERE TABLE_SCHEMA='backstage' AND TABLE_NAME='osh_course_section' AND COLUMN_NAME='cover') THEN
        ALTER TABLE osh_course_section ADD COLUMN cover VARCHAR(500) DEFAULT NULL COMMENT '封面图片 URL' AFTER media_url;
    END IF;
END$$

-- 添加 video_codec 字段（视频编码格式）
CREATE PROCEDURE add_video_codec_column()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS 
                   WHERE TABLE_SCHEMA='backstage' AND TABLE_NAME='osh_course_section' AND COLUMN_NAME='video_codec') THEN
        ALTER TABLE osh_course_section ADD COLUMN video_codec VARCHAR(50) DEFAULT NULL COMMENT '视频编码格式（如 H.264, H.265）' AFTER cover;
    END IF;
END$$

-- 添加 video_bitrate 字段（视频比特率）
CREATE PROCEDURE add_video_bitrate_column()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS 
                   WHERE TABLE_SCHEMA='backstage' AND TABLE_NAME='osh_course_section' AND COLUMN_NAME='video_bitrate') THEN
        ALTER TABLE osh_course_section ADD COLUMN video_bitrate INT DEFAULT NULL COMMENT '视频比特率（单位：kbps）' AFTER video_codec;
    END IF;
END$$

-- 添加 video_resolution 字段（视频分辨率）
CREATE PROCEDURE add_video_resolution_column()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS 
                   WHERE TABLE_SCHEMA='backstage' AND TABLE_NAME='osh_course_section' AND COLUMN_NAME='video_resolution') THEN
        ALTER TABLE osh_course_section ADD COLUMN video_resolution VARCHAR(20) DEFAULT NULL COMMENT '视频分辨率（如 1920x1080）' AFTER video_bitrate;
    END IF;
END$$

-- 添加 file_size 字段（文件大小）
CREATE PROCEDURE add_file_size_column()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS 
                   WHERE TABLE_SCHEMA='backstage' AND TABLE_NAME='osh_course_section' AND COLUMN_NAME='file_size') THEN
        ALTER TABLE osh_course_section ADD COLUMN file_size BIGINT DEFAULT NULL COMMENT '文件大小（字节）' AFTER video_resolution;
    END IF;
END$$

-- 添加 subtitle_url 字段（字幕文件 URL）
CREATE PROCEDURE add_subtitle_url_column()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS 
                   WHERE TABLE_SCHEMA='backstage' AND TABLE_NAME='osh_course_section' AND COLUMN_NAME='subtitle_url') THEN
        ALTER TABLE osh_course_section ADD COLUMN subtitle_url VARCHAR(500) DEFAULT NULL COMMENT '字幕文件 URL' AFTER file_size;
    END IF;
END$$

DELIMITER ;

-- 调用存储过程
CALL add_cover_column();
CALL add_video_codec_column();
CALL add_video_bitrate_column();
CALL add_video_resolution_column();
CALL add_file_size_column();
CALL add_subtitle_url_column();

-- 删除存储过程
DROP PROCEDURE IF EXISTS add_cover_column;
DROP PROCEDURE IF EXISTS add_video_codec_column;
DROP PROCEDURE IF EXISTS add_video_bitrate_column;
DROP PROCEDURE IF EXISTS add_video_resolution_column;
DROP PROCEDURE IF EXISTS add_file_size_column;
DROP PROCEDURE IF EXISTS add_subtitle_url_column;
