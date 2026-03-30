package com.backstage.system.service.common;


import org.springframework.web.multipart.MultipartFile;

public interface OssService {

    // 上传文件 不带路径
//    String addFile(MultipartFile file);
//
    // 上传文件 带路径
    String addCommonAvaterImage(MultipartFile file, String customPath) throws Exception;

    // 删除文件 不带路径
//    String deleteFile(String fileName);
//
    // 删除文件 带路径
//    String deleteFile(String fileName, String customPath);


    // 更新文件 带自定义路径
//    String updateFile(MultipartFile file, String oldFileName, String customPath);

}
