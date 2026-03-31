package com.backstage.system.service.impl.common;

import com.backstage.common.enums.UploadPathEnum;
import com.backstage.common.utils.DateUtils;
import com.backstage.common.utils.ServletUtils;
import com.backstage.common.utils.ip.IpUtils;
import com.backstage.system.domain.vo.common.OssOperationLogVo;
import com.backstage.system.mapper.common.OssMapper;
import com.backstage.system.service.common.OssService;
import com.backstage.system.utils.OssUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


@Service
public class OssImpl implements OssService {

    @Autowired
    private OssUtil ossUtil;

    @Autowired
    private OssMapper ossMapper;

    @Autowired
    private OssService ossService;

    // TODO
    // 多文件上传
    // enum 固定路径类型 course_code.zip 7z
    public String upload(MultipartFile file, UploadPathEnum pathEnum) throws  Exception{


        String customPath;

        // 获取年月
        String ym = DateUtils.getDate().substring(0,7).replace("-", "");;



        if(UploadPathEnum.AVATAR.equals(pathEnum)){
            customPath = UploadPathEnum.AVATAR.getPath()+ym+"/";
            if(file.getSize() > 1024 * 1024 * 3){
                return "图片大小不能超过3M";
            }

        }else if(UploadPathEnum.COURSE_VIDEO.equals(pathEnum)){
            customPath = UploadPathEnum.COURSE_VIDEO.getPath()+ym+"/";
            if(file.getSize() > 1024 * 1024 * 200){
                return "视频大小不能超过200MB";
            }
        }else {
            return "类型不正确";
        }


        return ossUtil.uploadFile(file, customPath);
    }



    public boolean existsFileKey(String fileKey) {
        if (fileKey == null || fileKey.trim().isEmpty()) {
            return false;
        }

        LambdaQueryWrapper<OssOperationLogVo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OssOperationLogVo::getFileKey, fileKey);
        return ossMapper.exists(wrapper);
    }



    public int incrementOperationCount(String fileKey) {
        if (fileKey == null || fileKey.trim().isEmpty()) {
            return 0;
        }

        // 直接使用 updateWrapper 让 operation_count + 1
        LambdaUpdateWrapper<OssOperationLogVo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(OssOperationLogVo::getFileKey, fileKey)
                .setSql("operation_count = operation_count + 1");

        return ossMapper.update(null, updateWrapper);
    }


    public void playVideo(@RequestParam String key,
                          HttpServletRequest request,
                          HttpServletResponse response) {
        try {
            // 1. 登录校验


            // 2. 生成签名
            String r2SignedUrl = ossUtil.getSignedUrl(key, 10);

            URL url = new URL(r2SignedUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            // 3. 处理拖动 Range
            String range = request.getHeader("Range");
            if (range != null) {
                conn.setRequestProperty("Range", range);
                response.setStatus(206);
                // 新增：告诉播放器当前片段的范围
                String contentRange = conn.getHeaderField("Content-Range");
                if (contentRange != null) {
                    response.setHeader("Content-Range", contentRange);
                }
            }

            // 4. 设置视频头
            response.setContentType("video/mp4");
            response.setHeader("Accept-Ranges", "bytes");
            long contentLength = conn.getContentLengthLong();
            if (contentLength > 0) {
                response.setHeader("Content-Length", String.valueOf(contentLength));
            }

            // 5. 流式输出（捕获断开异常，不抛错）
            byte[] buffer = new byte[8192];
            try (InputStream in = conn.getInputStream();
                 ServletOutputStream out = response.getOutputStream()) {

                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
            } catch (IOException ignored) {
                // 浏览器断开连接
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            // 只打印真正的错误
            if (!e.getMessage().contains("已建立的连接")) {
                e.printStackTrace();
            }
        }
    }


    public void insertMapper(MultipartFile file, String customPath) {

        OssOperationLogVo log = new OssOperationLogVo();

        if (!ossService.existsFileKey(file.getOriginalFilename())) {
            // 获取浏览器 User-Agent
            UserAgent userAgent = UserAgent.parseUserAgentString(
                    ServletUtils.getRequest().getHeader("User-Agent")
            );
            // 获取客户端IP
            String ip = IpUtils.getIpAddr();
            // 原始文件名
            log.setOriginalName(file.getOriginalFilename());
            log.setFileKey(customPath);
            // 文件后缀
            log.setFileSuffix(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1, file.getOriginalFilename().length()));
            // 文件大小字节
            log.setFileSize(file.getSize() / 1024 / 1024);
            log.setFileType(file.getContentType());
            log.setOperationType("upload");

            // 文件访问次数
            log.setOperationCount(1);

            log.setOperationCount(1);
            log.setUsername("admin");
            log.setOperationCount(1);
            log.setIp(ip);

            // 浏览器的userAgent
            log.setUserAgent(userAgent.toString());
            log.setBucket(ossUtil.getOssProperties().getBucketName());

        }else {
            ossService.incrementOperationCount(file.getOriginalFilename());
        }


        ossMapper.insert(log);
    }


}
