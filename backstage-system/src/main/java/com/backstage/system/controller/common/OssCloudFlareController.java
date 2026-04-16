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
import com.backstage.system.utils.UserContextUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;


@ApiOperation("上传接口")
@RestController
@RequestMapping("/pc")
public class OssCloudFlareController {

    private final Logger log = LoggerFactory.getLogger(OssCloudFlareController.class);

    @Autowired
    private IOshUploadImageService oshUploadImageService;

    @Autowired
    private OssService ossService;

    @Autowired
    private OssUtil ossUtil;

    @Autowired
    private UserContextUtil userContextUtil;

    /**
     * 限制IP 每60秒 10次 如果加key可以相同的配额限制
     *
     * @param file        文件
     * @param type        上传场景模块类型
     * @param resultId     资源id
     * @param previewFlag 是否需要预览, 默认false
     * @param limitMinute 生成文件预览链接的超时时间, 分钟数, 默认30分钟
     */
    @RateLimiter(limitType = LimitType.IP, time = 60, count = 10)
    @Anonymous
    @ApiParam(value = "上传文件", required = true)
    @ApiOperation("上传接口")
    @PostMapping("/upload")
    public R<Object> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam(value = "preview", required = false, defaultValue = "false") Boolean previewFlag,
            @RequestParam(value = "minute", required = false, defaultValue = "30") Integer limitMinute,
            @RequestParam(value = "id", required = false) String resultId) {

        if (file.isEmpty()) {
            return R.fail("上传文件不能为空");
        }
        if (type.equals("video")) {
            try {
                String url = ossService.upload(file, UploadPathEnum.COURSE_VIDEO, id);
                // 判断是否返回了错误信息
                if (url == null || url.contains("不能超过") || url.contains("类型不正确")) {
                    return R.fail(url);
                }
                return R.ok(url);
            } catch (Exception e) {
                log.error("上传失败", e);
                return R.fail("上传失败：" + e.getMessage());
            }
        } else {
            try {
                String url = ossService.upload(file, UploadPathEnum.IMAGE, id);
                if (Objects.equals(url, "图片大小不能超过3M")) {
                    return R.fail(url);
                }
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
                    AvaterVo avaterVo = new AvaterVo();
                    if (previewFlag) {
                        avaterVo.setPreviewUrl(ossService.getLimitedUrl(url, limitMinute));
                    }
                    avaterVo.setUrl(url);
                    avaterVo.setFileName(file.getOriginalFilename());
                    avaterVo.setFileSize(String.valueOf(file.getSize()));
                    avaterVo.setFileType(file.getContentType());
                    return R.ok(avaterVo);
                } else {
                    return R.fail(file.getOriginalFilename());
                }

            } catch (Exception e) {
                log.error("上传失败", e);
                return R.fail(e.getMessage());
            }
        }
    }

    @Anonymous
    @GetMapping("/upload/avatar")
    public R getUrl() {
        String signedUrl = ossService.getLimitedUrl("common/video/course/946/202604/81c67441-f771-4eb9-be25-86576cd13794_test11.mp4", 1);

        return R.ok(signedUrl);

    }


    // course/jiuyexiaoban/900MB视频-1231经过web优化.mp4



}
