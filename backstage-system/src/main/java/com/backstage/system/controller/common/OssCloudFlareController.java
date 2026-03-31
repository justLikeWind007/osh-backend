package com.backstage.system.controller.common;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.RateLimiter;
import com.backstage.common.core.domain.R;
import com.backstage.common.enums.LimitType;
import com.backstage.common.enums.UploadPathEnum;
import com.backstage.system.utils.OssUtil;
import com.backstage.system.domain.order.OshUploadImage;
import com.backstage.system.domain.vo.common.AvaterVo;
import com.backstage.system.service.common.OssService;
import com.backstage.system.service.order.IOshUploadImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


@RestController
public class OssCloudFlareController {

    @Autowired
    private IOshUploadImageService oshUploadImageService;

    @Autowired
    private OssService ossService;

    @Autowired
    private OssUtil ossUtil;


    @Anonymous
    @PostMapping("/upload/video")
    public R uploadVideo(MultipartFile video){


        if (video.isEmpty()) {
            return R.fail("上传文件不能为空");
        }

        try {
            String url = ossService.upload(video, UploadPathEnum.COURSE_VIDEO);

            // 判断是否返回了错误信息
            if (url == null || url.contains("不能超过") || url.contains("类型不正确")) {
                return R.fail(url);
            }

            return R.ok(url);
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("上传失败：" + e.getMessage());
        }

    }

    // 上传头像 到 osh_user才对
    // 限制IP 没60秒 10次 如果加key可以相同的配额限制
    @RateLimiter(limitType = LimitType.IP, time = 60, count = 10)
    @Anonymous
    @PostMapping("/upload")
    public R<AvaterVo> upload(MultipartFile file) {


        try {
            String url = ossService.upload(file, UploadPathEnum.AVATAR);
            if (url == "图片大小不能超过3M") {
                return R.fail(url);
            }
            OshUploadImage uploadImage = new OshUploadImage();
            uploadImage.setUserId(1L);
            uploadImage.setSchoolId(1L);
            uploadImage.setFileName(file.getOriginalFilename());
            uploadImage.setFilePath(UploadPathEnum.AVATAR.getPath() + file.getOriginalFilename());
            uploadImage.setFileSize(file.getSize());
            uploadImage.setFileType(file.getContentType());
            uploadImage.setStatus(1L);

            int result = oshUploadImageService.insertOshUploadImage(uploadImage);

            if (result > 0) {
                R r = new R();
                AvaterVo avaterVo = new AvaterVo();
                avaterVo.setUrl(UploadPathEnum.AVATAR.getPath() + file.getOriginalFilename());
                avaterVo.setFileName(file.getOriginalFilename());
                avaterVo.setFileSize(String.valueOf(file.getSize()));
                avaterVo.setFileType(file.getContentType());
                r.setMsg("ok");
                return r.ok(avaterVo);
            } else {
                return R.fail(file.getOriginalFilename());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return R.fail(e.getMessage());
        }
    }

    @Anonymous
    @GetMapping("/upload/avatar")
    public R getUrl() {
        String signedUrl = ossUtil.getSignedUrl("common/image/avatar/202603/微信图片_20260327163452_147_8.jpg", 1);

        return R.ok(signedUrl);

    }


    // course/jiuyexiaoban/900MB视频-1231经过web优化.mp4
    @Anonymous
    @GetMapping("/play")
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

}
