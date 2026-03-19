//package com.backstage.common.config.properties;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
///**
// * 阿里云 OSS 配置属性
// */
//@Component
//public class OssProperties {
//
//    /**
//     * OSS endpoint
//     */
//    @Value("${aliyun.oss.endpoint}")
//    private String endpoint;
//
//    /**
//     * OSS accessKeyId
//     */
//    @Value("${aliyun.oss.accessKeyId}")
//    private String accessKeyId;
//
//    /**
//     * OSS accessKeySecret
//     */
//    @Value("${aliyun.oss.accessKeySecret}")
//    private String accessKeySecret;
//
//    /**
//     * OSS bucketName
//     */
//    @Value("${aliyun.oss.bucketName}")
//    private String bucketName;
//
//    /**
//     * OSS 访问域名（用于返回可访问的 URL）
//     */
//    @Value("${aliyun.oss.domain}")
//    private String domain;
//
//    /**
//     * 文件路径前缀
//     */
//    @Value("${aliyun.oss.pathPrefix:upload/}")
//    private String pathPrefix;
//
//    public String getEndpoint() {
//        return endpoint;
//    }
//
//    public void setEndpoint(String endpoint) {
//        this.endpoint = endpoint;
//    }
//
//    public String getAccessKeyId() {
//        return accessKeyId;
//    }
//
//    public void setAccessKeyId(String accessKeyId) {
//        this.accessKeyId = accessKeyId;
//    }
//
//    public String getAccessKeySecret() {
//        return accessKeySecret;
//    }
//
//    public void setAccessKeySecret(String accessKeySecret) {
//        this.accessKeySecret = accessKeySecret;
//    }
//
//    public String getBucket() {
//        return bucketName;
//    }
//
//    public void setBucket(String bucketName) {
//        this.bucketName = bucketName;
//    }
//
//    public String getDomain() {
//        return domain;
//    }
//
//    public void setDomain(String domain) {
//        this.domain = domain;
//    }
//
//    public String getPathPrefix() {
//        return pathPrefix;
//    }
//
//    public void setPathPrefix(String pathPrefix) {
//        this.pathPrefix = pathPrefix;
//    }
//}
