package com.backstage.common.enums;

/**
 * 归档文件扩展名枚举
 * 用于校验上传资料文件类型
 */
public enum ArchiveExtensionEnum {

    ZIP("zip", "ZIP压缩包"),
    RAR("rar", "RAR压缩包"),
    TAR("tar", "TAR归档文件"),
    GZ("gz", "GZ压缩包"),
    SEVEN_Z("7z", "7-Zip压缩包");

    private final String extension;
    private final String description;

    ArchiveExtensionEnum(String extension, String description) {
        this.extension = extension;
        this.description = description;
    }

    public String getExtension() {
        return extension;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据扩展名获取枚举
     */
    public static ArchiveExtensionEnum fromExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return null;
        }
        String ext = extension.toLowerCase();
        for (ArchiveExtensionEnum archiveExtension : values()) {
            if (archiveExtension.extension.equals(ext)) {
                return archiveExtension;
            }
        }
        return null;
    }

    /**
     * 判断扩展名是否允许
     */
    public static boolean isAllowed(String extension) {
        return fromExtension(extension) != null;
    }

    /**
     * 获取所有允许的扩展名字符串列表
     */
    public static java.util.List<String> getAllowedExtensions() {
        java.util.List<String> extensions = new java.util.ArrayList<>();
        for (ArchiveExtensionEnum archiveExtension : values()) {
            extensions.add(archiveExtension.extension);
        }
        return extensions;
    }
}
