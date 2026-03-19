//package com.backstage.common.utils.file;
//
//import com.aliyun.oss.OSS;
//import com.aliyun.oss.OSSClientBuilder;
//import com.aliyun.oss.ClientException;
//import com.aliyun.oss.OSSException;
//import com.backstage.common.exception.file.FileUploadException;
//import com.backstage.common.config.properties.OssProperties;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.UUID;
//
///**
// * 阿里云 OSS 文件上传工具类
// */
//@Component
//public class OssUtil {
//
//    private static final Logger log = LoggerFactory.getLogger(OssUtil.class);
//
//    private final OssProperties ossProperties;
//
//    public OssUtil(OssProperties ossProperties) {
//        this.ossProperties = ossProperties;
//    }
//
//    /**
//     * 上传单个文件
//     *
//     * @param file 文件
//     * @return 文件访问 URL
//     * @throws FileUploadException 上传异常
//     */
//    public String uploadFile(MultipartFile file) throws FileUploadException {
//        if (file == null || file.isEmpty()) {
//            throw new FileUploadException("上传文件不能为空");
//        }
//
//        String originalFilename = file.getOriginalFilename();
//        String fileName = generateFileName(originalFilename);
//        String filePath = ossProperties.getPathPrefix() + fileName;
//
//        try (InputStream inputStream = file.getInputStream()) {
//            OSS ossClient = null;
//            try {
//                ossClient = createOssClient();
//                ossClient.putObject(
//                        ossProperties.getBucket(),
//                        filePath,
//                        inputStream
//                );
//
//                String url = getFileUrl(filePath);
//                log.info("文件上传成功：{}, URL: {}", filePath, url);
//                return url;
//            } catch (OSSException e) {
//                log.error("OSS 服务异常：{}", e.getErrorMessage(), e);
//                throw new FileUploadException("文件上传失败：OSS 服务异常 - " + e.getMessage());
//            } catch (ClientException e) {
//                log.error("OSS 客户端异常：{}", e.getMessage(), e);
//                throw new FileUploadException("文件上传失败：网络连接异常 - " + e.getMessage());
//            } finally {
//                if (ossClient != null) {
//                    try {
//                        ossClient.shutdown();
//                    } catch (Exception e) {
//                        log.warn("关闭 OSS 客户端失败", e);
//                    }
//                }
//            }
//        } catch (IOException e) {
//            log.error("文件读取失败：{}", originalFilename, e);
//            throw new FileUploadException("文件读取失败：" + e.getMessage());
//        }
//    }
//
//    /**
//     * 删除文件
//     *
//     * @param url 文件 URL
//     * @return 是否删除成功
//     */
//    public boolean deleteFile(String url) {
//        try {
//            String objectKey = extractObjectKey(url);
//            if (objectKey == null) {
//                return false;
//            }
//
//            OSS ossClient = null;
//            try {
//                ossClient = createOssClient();
//                ossClient.deleteObject(
//                        ossProperties.getBucket(),
//                        objectKey
//                );
//
//                log.info("文件删除成功：{}", objectKey);
//                return true;
//            } catch (OSSException e) {
//                log.error("OSS 服务异常：{}", e.getErrorMessage(), e);
//                return false;
//            } catch (ClientException e) {
//                log.error("OSS 客户端异常：{}", e.getMessage(), e);
//                return false;
//            } finally {
//                if (ossClient != null) {
//                    try {
//                        ossClient.shutdown();
//                    } catch (Exception e) {
//                        log.warn("关闭 OSS 客户端失败", e);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.error("文件删除失败：{}", url, e);
//            return false;
//        }
//    }
//
//    /**
//     * 获取文件访问 URL
//     *
//     * @param objectKey OSS 对象键
//     * @return 完整的访问 URL
//     */
//    private String getFileUrl(String objectKey) {
//        String baseUrl = ossProperties.getDomain();
//        if (baseUrl == null || baseUrl.isEmpty()) {
//            baseUrl = "https://" + ossProperties.getBucket() + "." +
//                      ossProperties.getEndpoint().replace("https://", "").replace("http://", "");
//        }
//
//        if (baseUrl.endsWith("/")) {
//            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
//        }
//
//        return baseUrl + "/" + objectKey;
//    }
//
//    /**
//     * 从 URL 中提取 OSS 对象键
//     *
//     * @param url 文件 URL
//     * @return OSS 对象键
//     */
//    private String extractObjectKey(String url) {
//        if (url == null || url.isEmpty()) {
//            return null;
//        }
//
//        String domain = ossProperties.getDomain();
//        if (domain != null && !domain.isEmpty()) {
//            if (url.startsWith(domain)) {
//                return url.substring(domain.length() + 1);
//            }
//        }
//
//        int lastSlashIndex = url.lastIndexOf("/");
//        if (lastSlashIndex >= 0 && lastSlashIndex < url.length() - 1) {
//            String pathPrefix = ossProperties.getPathPrefix();
//            String fileName = url.substring(lastSlashIndex + 1);
//            return pathPrefix + fileName;
//        }
//
//        return null;
//    }
//
//    /**
//     * 生成文件名
//     *
//     * @param originalFilename 原始文件名
//     * @return 新的文件名
//     */
//    private String generateFileName(String originalFilename) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
//        String datePath = sdf.format(new Date());
//
//        String uuid = UUID.randomUUID().toString().replace("-", "");
//
//        int dotIndex = originalFilename.lastIndexOf(".");
//        String extension = "";
//        if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
//            extension = originalFilename.substring(dotIndex + 1);
//        }
//
//        if (extension.isEmpty()) {
//            return datePath + uuid;
//        } else {
//            return datePath + uuid + "." + extension;
//        }
//    }
//
//    /**
//     * 创建 OSS 客户端
//     *
//     * @return OSS 客户端实例
//     */
//    private OSS createOssClient() {
//        return new OSSClientBuilder().build(
//                ossProperties.getEndpoint(),
//                ossProperties.getAccessKeyId(),
//                ossProperties.getAccessKeySecret()
//        );
//    }
//}
