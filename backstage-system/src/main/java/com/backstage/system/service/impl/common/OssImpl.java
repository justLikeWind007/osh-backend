package com.backstage.system.service.impl.common;

import com.backstage.common.core.domain.R;
import com.backstage.common.enums.UploadPathEnum;
import com.backstage.common.utils.DateUtils;
import com.backstage.common.utils.ServletUtils;
import com.backstage.common.utils.ip.IpUtils;
import com.backstage.system.domain.vo.common.OssOperationLogVo;
import com.backstage.system.mapper.common.OssMapper;
import com.backstage.system.service.common.OssService;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.backstage.common.utils.OssUtil;


@Service
public class OssImpl implements OssService {

    @Autowired
    private OssUtil ossUtil;

    @Autowired
    private OssMapper ossMapper;


    // TODO

    // enum 固定路径类型 course_code.zip 7z
    public String upload(MultipartFile file, UploadPathEnum pathEnum) throws  Exception{

        // 获取浏览器 User-Agent
        UserAgent userAgent = UserAgent.parseUserAgentString(
                ServletUtils.getRequest().getHeader("User-Agent")
        );

        // 获取客户端IP
        String ip = IpUtils.getIpAddr();
        // 获取年月
        String ym = DateUtils.getDate().substring(0,7).replace("-", "");;
        // 最终文件路径
        String customPath;

        OssOperationLogVo log = new OssOperationLogVo();

        // 原始文件名
        log.setOriginalName(file.getOriginalFilename());

        // 文件后缀
        log.setFileSuffix(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1, file.getOriginalFilename().length()));

        // 文件大小字节
        log.setFileSize(file.getSize()/1024/1024);
        log.setFileType(file.getContentType());
        log.setOperationType("上传");

        // 操作数
        log.setOperationCount(1);
        log.setUsername("admin");
        log.setOperationCount(1);
        log.setIp(ip);

        // 浏览器的userAgent
        log.setUserAgent(userAgent.toString());


        log.setBucket(ossUtil.getOssProperties().getBucketName());


        R.fail("上传失败").setData("上传失败");

        if(UploadPathEnum.AVATAR.equals(pathEnum)){
            customPath = UploadPathEnum.AVATAR.getPath()+ym+"/";
            log.setFileKey(customPath + file.getOriginalFilename());
            if(file.getSize() > 1024 * 1024 * 3){
                return "图片大小不能超过3M";
            }
            ossMapper.insert(log);
        }else if(UploadPathEnum.COURSE_VIDEO.equals(pathEnum)){
            customPath = UploadPathEnum.COURSE_VIDEO.getPath()+ym+"/";
            //
            log.setFileKey(customPath + file.getOriginalFilename());
            if(file.getSize() > 1024 * 1024 * 200){
                return "视频大小不能超过200MB";
            }
        }else {
            return "类型不正确";
        }


        // 限制操作次数


        return ossUtil.uploadFile(file, customPath);
    }



}
