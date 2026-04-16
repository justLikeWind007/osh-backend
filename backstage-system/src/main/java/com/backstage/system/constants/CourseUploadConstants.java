package com.backstage.system.constants;

import java.util.Arrays;
import java.util.List;

/**
 * 课程上传文件相关常量
 * 包括：文件扩展名、文件大小限制等
 * 
 * @author ruoyi
 */
public final class CourseUploadConstants {

    /**
     * 图片相关常量
     */
    public static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList("bmp", "gif", "jpg", "jpeg", "png");
    
    /**
     * 图片最大尺寸：5MB
     */
    public static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;

    /**
     * 图片上传失败提示信息
     */
    public static final String IMAGE_FORMAT_ERROR = "封面图片仅支持 bmp、gif、jpg、jpeg、png 格式";
    public static final String IMAGE_SIZE_ERROR = "封面图片大小不能超过 5MB";

    /**
     * 视频相关常量
     */
    public static final List<String> ALLOWED_VIDEO_EXTENSIONS = Arrays.asList("mp4", "avi", "mov", "mkv", "wmv", "flv", "webm");
    
    /**
     * 视频最大尺寸：500MB
     */
    public static final long MAX_VIDEO_SIZE = 500 * 1024 * 1024;

    /**
     * 视频上传失败提示信息
     */
    public static final String VIDEO_FORMAT_ERROR = "仅支持视频格式（mp4/avi/mov/mkv/wmv/flv/webm）";
    public static final String VIDEO_SIZE_ERROR = "视频文件大小不能超过 500MB";

    /**
     * 压缩包相关常量
     */
    public static final List<String> ALLOWED_ARCHIVE_EXTENSIONS = Arrays.asList("zip", "rar", "tar", "gz", "7z");
    
    /**
     * 压缩包最大尺寸：100MB
     */
    public static final long MAX_ARCHIVE_SIZE = 100 * 1024 * 1024;

    /**
     * 压缩包上传失败提示信息
     */
    public static final String ARCHIVE_FORMAT_ERROR = "仅支持压缩包格式（zip/rar/tar/gz/7z）";
    public static final String ARCHIVE_SIZE_ERROR = "压缩包大小不能超过 100MB";

    /**
     * 通用上传错误信息关键字
     */
    public static final String UPLOAD_ERROR_SIZE_KEYWORD = "不能超过";
    public static final String UPLOAD_ERROR_TYPE_KEYWORD = "类型不正确";

    /**
     * 文件提取错误信息方法
     */
    public static boolean isUploadError(String message) {
        return message != null && (message.contains(UPLOAD_ERROR_SIZE_KEYWORD) || message.contains(UPLOAD_ERROR_TYPE_KEYWORD));
    }

    private CourseUploadConstants() {
    }
}
