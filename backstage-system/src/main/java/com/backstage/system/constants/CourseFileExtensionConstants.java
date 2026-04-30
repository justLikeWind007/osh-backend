package com.backstage.system.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 课程模块文件类型常量管理类
 */
public class CourseFileExtensionConstants {

    /**
     * ==================== 视频文件类型 ====================
     */

    /** 视频文件扩展名数组 */
    public static final String[] VIDEO_EXTENSIONS = {"mp4", "avi", "mov", "mkv", "wmv", "flv", "webm"};

    /** 视频文件扩展名列表（便于使用 contains 方法） */
    public static final List<String> VIDEO_EXTENSIONS_LIST = Collections.unmodifiableList(
            Arrays.asList(VIDEO_EXTENSIONS)
    );

    /** 视频文件最大大小：500MB */
    public static final long VIDEO_MAX_SIZE = 500 * 1024 * 1024;

    /** 视频文件类型错误提示信息 */
    public static final String VIDEO_EXTENSION_ERROR_MSG = "仅支持视频格式（mp4/avi/mov/mkv/wmv/flv/webm）";

    /** 视频文件大小超限提示信息 */
    public static final String VIDEO_SIZE_ERROR_MSG = "视频文件大小不能超过 500MB";


    /**
     * ==================== 压缩包文件类型 ====================
     */

    /** 压缩包文件扩展名数组 */
    public static final String[] ARCHIVE_EXTENSIONS = {"zip", "rar", "tar", "gz"};

    /** 压缩包文件扩展名列表（便于使用 contains 方法） */
    public static final List<String> ARCHIVE_EXTENSIONS_LIST = Collections.unmodifiableList(
            Arrays.asList(ARCHIVE_EXTENSIONS)
    );

    /** 压缩包文件最大大小：100MB */
    public static final long ARCHIVE_MAX_SIZE = 100 * 1024 * 1024;

    /** 压缩包文件类型错误提示信息 */
    public static final String ARCHIVE_EXTENSION_ERROR_MSG = "仅支持 zip/rar/tar/gz 格式的资料";

    /** 压缩包文件大小超限提示信息 */
    public static final String ARCHIVE_SIZE_ERROR_MSG = "资料文件大小不能超过 100MB";


    /**
     * ==================== 图片文件类型 ====================
     */

    /** 图片文件扩展名数组 */
    public static final String[] IMAGE_EXTENSIONS = {"bmp", "gif", "jpg", "jpeg", "png"};

    /** 图片文件扩展名列表（便于使用 contains 方法） */
    public static final List<String> IMAGE_EXTENSIONS_LIST = Collections.unmodifiableList(
            Arrays.asList(IMAGE_EXTENSIONS)
    );

    /** 图片文件最大大小：3MB */
    public static final long IMAGE_MAX_SIZE = 3 * 1024 * 1024;

    /** 图片文件类型错误提示信息 */
    public static final String IMAGE_EXTENSION_ERROR_MSG = "仅支持图片格式（bmp/gif/jpg/jpeg/png）";

    /** 图片文件大小超限提示信息 */
    public static final String IMAGE_SIZE_ERROR_MSG = "图片文件大小不能超过 3MB";


    /**
     * ==================== 文档文件类型 ====================
     */

    /** 文档文件扩展名数组 */
    public static final String[] DOCUMENT_EXTENSIONS = {"doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf"};

    /** 文档文件扩展名列表（便于使用 contains 方法） */
    public static final List<String> DOCUMENT_EXTENSIONS_LIST = Collections.unmodifiableList(
            Arrays.asList(DOCUMENT_EXTENSIONS)
    );

    /** 文档文件最大大小：50MB */
    public static final long DOCUMENT_MAX_SIZE = 50 * 1024 * 1024;

    /** 文档文件类型错误提示信息 */
    public static final String DOCUMENT_EXTENSION_ERROR_MSG = "仅支持文档格式（doc/docx/xls/xlsx/ppt/pptx/pdf）";

    /** 文档文件大小超限提示信息 */
    public static final String DOCUMENT_SIZE_ERROR_MSG = "文档文件大小不能超过 50MB";


    /**
     * ==================== 工具方法 ====================
     */

    /**
     * 验证视频文件扩展名
     * 
     * @param extension 文件扩展名
     * @return 是否为允许的视频文件
     */
    public static boolean isValidVideoExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        return VIDEO_EXTENSIONS_LIST.contains(extension.toLowerCase());
    }

    /**
     * 验证压缩包文件扩展名
     * 
     * @param extension 文件扩展名
     * @return 是否为允许的压缩包文件
     */
    public static boolean isValidArchiveExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        return ARCHIVE_EXTENSIONS_LIST.contains(extension.toLowerCase());
    }

    /**
     * 验证图片文件扩展名
     * 
     * @param extension 文件扩展名
     * @return 是否为允许的图片文件
     */
    public static boolean isValidImageExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        return IMAGE_EXTENSIONS_LIST.contains(extension.toLowerCase());
    }

    /**
     * 验证文档文件扩展名
     * 
     * @param extension 文件扩展名
     * @return 是否为允许的文档文件
     */
    public static boolean isValidDocumentExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        return DOCUMENT_EXTENSIONS_LIST.contains(extension.toLowerCase());
    }

    /**
     * 验证文件大小是否超限
     * 
     * @param fileSize 文件大小（字节）
     * @param maxSize 允许的最大大小（字节）
     * @return 是否超限
     */
    public static boolean isFileSizeExceeded(long fileSize, long maxSize) {
        return fileSize > maxSize;
    }

    /**
     * 获取文件扩展名的人性化描述
     * 
     * @param extension 文件扩展名
     * @return 描述信息
     */
    public static String getExtensionDescription(String extension) {
        if (extension == null) {
            return "未知类型";
        }
        
        String ext = extension.toLowerCase();
        if (isValidVideoExtension(ext)) {
            return "视频文件";
        } else if (isValidArchiveExtension(ext)) {
            return "压缩包文件";
        } else if (isValidImageExtension(ext)) {
            return "图片文件";
        } else if (isValidDocumentExtension(ext)) {
            return "文档文件";
        }
        return "未知类型";
    }
}
