package com.backstage.system.controller.order;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.AjaxResult;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.order.OshUploadImage;
import com.backstage.system.service.order.IOshUploadImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@RestController
public class OssCloudFlare {

    @Autowired
    private IOshUploadImageService oshUploadImageService;


    @Value("${x-file-storage.cloudflare-r2.access-key}")
    private String accessKey;

    @Value("${x-file-storage.cloudflare-r2.secret-key}")
    private String secretKey;

    @Value("${x-file-storage.cloudflare-r2.endpoint}")
    private String endpoint;

    @Value("${x-file-storage.cloudflare-r2.bucket-name}")
    private String bucket;

    @Value("${x-file-storage.cloudflare-r2.public-domain}")
    private String publicDomain;

    @Value("${x-file-storage.cloudflare-r2.base-path}")
    private String basePath;

    @Anonymous
    @PostMapping("/upload")
    public AjaxResult upload(MultipartFile file) {
        try {
            BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            AmazonS3Client s3 = new AmazonS3Client(credentials);

            s3.setEndpoint(endpoint);
            s3.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).build());

            String fileName = basePath + UUID.randomUUID() + "_" + file.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            // 上传到指定路径
            PutObjectRequest request = new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata);
            s3.putObject(request);

            // 返回可访问地址（自动带路径）
            String url = publicDomain + "/" + fileName;


            OshUploadImage uploadImage = new OshUploadImage();
            uploadImage.setUserId(1L);
            uploadImage.setSchoolId(1L);
            uploadImage.setFileName(file.getOriginalFilename());
            uploadImage.setFilePath(url);
            uploadImage.setFileSize(file.getSize());
            uploadImage.setFileType(file.getContentType());
            uploadImage.setStatus(1L);

            int result = oshUploadImageService.insertOshUploadImage(uploadImage);

            if (result > 0) {
                AjaxResult ajax = AjaxResult.success();
                ajax.put("url", url);
                ajax.put("fileName", file.getOriginalFilename());
                ajax.put("fileSize", file.getSize());
                return ajax;
            } else {
                return AjaxResult.error("上传失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(e.getMessage());
        }
    }
}