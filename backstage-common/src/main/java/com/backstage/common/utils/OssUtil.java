package com.backstage.common.utils;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.backstage.common.config.OssProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;

@Component
public class OssUtil {

    @Autowired
    private OssProperties ossProperties;

    // 创建连接
    public AmazonS3Client createS3Client() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(
                ossProperties.getAccessKey(),
                ossProperties.getSecretKey()
        );
        AmazonS3Client s3 = new AmazonS3Client(credentials);
        s3.setEndpoint(ossProperties.getEndpoint());
        s3.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).build());
        return s3;
    }

    // 上传文件默认配置路径
    public String uploadFile(MultipartFile file) throws Exception {
        return uploadFile(file, null);
    }

    // 上传文件 带路径
    public String uploadFile(MultipartFile file, String customPath) throws Exception {
        AmazonS3Client s3 = createS3Client();

        String path = customPath != null ? customPath : ossProperties.getBasePath();

        String fileName = path + UUID.randomUUID() + "_" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        PutObjectRequest request = new PutObjectRequest(
                ossProperties.getBucketName(),
                fileName,
                file.getInputStream(),
                metadata
        );

        s3.putObject(request);

        return ossProperties.getPublicDomain() + "/" + fileName;
    }

    // 生成文件名 不带前缀
    public String generateFileName(MultipartFile file) {
        return generateFileName(file, null);
    }

    // 生成文件名 带前缀
    public String generateFileName(MultipartFile file, String prefix) {
        String originalFilename = file.getOriginalFilename();
        if (prefix != null && !prefix.isEmpty()) {
            return prefix + "_" + UUID.randomUUID() + "_" + originalFilename;
        }
        return UUID.randomUUID() + "_" + originalFilename;
    }

    // 获取文件完整路径
    public String getFullFilePath(String fileName) {
        String path = ossProperties.getBasePath() + fileName;
        return ossProperties.getPublicDomain() + "/" + path;
    }

    // 获取OSS配置信息
    public OssProperties getOssProperties() {
        return ossProperties;
    }

    // 删除文件
    public boolean deleteFile(String fileName) {
        try {
            AmazonS3Client s3 = createS3Client();
            s3.deleteObject(ossProperties.getBucketName(), fileName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 判断文件是否存在
    public boolean doesFileExist(String fileName) {
        AmazonS3Client s3 = createS3Client();
        return s3.doesObjectExist(ossProperties.getBucketName(), fileName);
    }

    // 重命名文件 本质是删除再上传
    public void renameFile(String oldFileName, String newFileName) {
        try {
            AmazonS3Client s3 = createS3Client();

            CopyObjectRequest copyRequest = new CopyObjectRequest(
                    ossProperties.getBucketName(),
                    oldFileName,
                    ossProperties.getBucketName(),
                    newFileName
            );
            s3.copyObject(copyRequest);

            s3.deleteObject(ossProperties.getBucketName(), oldFileName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("重命名失败：" + e.getMessage());
        }
    }



    // 获取文件签名URL
    public String getSignedUrl(String fileKey, int expireMinutes) {
        AmazonS3Client s3 = createS3Client();

        // 过期时间 分钟*60*1000 毫秒
        long expireTime = System.currentTimeMillis() + (long) expireMinutes * 60 * 1000;
        Date expiration = new Date(expireTime);

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                ossProperties.getBucketName(),
                fileKey
        ).withExpiration(expiration);

        return s3.generatePresignedUrl(request).toString();
    }

}
