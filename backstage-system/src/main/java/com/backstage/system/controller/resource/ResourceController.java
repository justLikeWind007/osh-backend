package com.backstage.system.controller.resource;

import com.backstage.common.annotation.OshUserEvent;
import com.backstage.common.core.domain.R;
import com.backstage.common.enums.UploadPathEnum;
import com.backstage.system.domain.vo.resource.ResourceVO;
import com.backstage.system.service.common.OssService;
import com.backstage.system.service.resource.IResourceService;
import com.backstage.system.utils.OssUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 内部资源 Controller
 *
 * @author backstage
 */
@ApiOperation(value = "内部资源接口")
@RestController
@RequestMapping("/pc/internal/resource")
public class ResourceController {

    @Resource
    private IResourceService resourceService;

    @Resource
    private OssService ossService;

    @Autowired
    private OssUtil ossUtil;

    @ApiOperation("资源分页列表")
    @OshUserEvent(module = "内部资源模块", actionType = "查询", description = "查询资源分页")
    // @PreAuthorize("hasAuthority('internal:resource:list')")
    @GetMapping("/page")
    public R<Page<com.backstage.system.domain.resource.Resource>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "20") long pageSize) {
        Page<com.backstage.system.domain.resource.Resource> page = new Page<>(pageNum, pageSize);
        return R.ok(resourceService.pageResource(keyword, page));
    }

    @ApiOperation("全量资源列表（下拉选择）")
    // @PreAuthorize("hasAuthority('internal:resource:query')")
    @GetMapping("/list")
    public R<List<com.backstage.system.domain.resource.Resource>> list(@RequestParam(required = false) String keyword) {
        Page<com.backstage.system.domain.resource.Resource> page = new Page<>(1, 1000);
        return R.ok(resourceService.pageResource(keyword, page).getRecords());
    }

    @ApiOperation("资源详情")
    // @PreAuthorize("hasAuthority('internal:resource:query')")
    @GetMapping("/{id}")
    public R<com.backstage.system.domain.resource.Resource> detail(@PathVariable Long id) {
        return R.ok(resourceService.getResource(id));
    }

    @ApiOperation("新增资源")
    @OshUserEvent(module = "内部资源模块", actionType = "新增", description = "新增资源")
    // @PreAuthorize("hasAuthority('internal:resource:add')")
    @PostMapping
    public R<Long> create(@RequestBody java.util.Map<String, Object> params) {
        com.backstage.system.domain.resource.Resource resource = new com.backstage.system.domain.resource.Resource();
        resource.setName((String) params.get("name"));
        resource.setType((String) params.get("type"));
        resource.setRemark((String) params.get("remark"));

        // 获取groupId（如果存在）
        Long groupId = null;
        if (params.get("groupId") != null) {
            groupId = Long.valueOf(params.get("groupId").toString());
        }

        return R.ok(resourceService.createResource(resource, groupId));
    }

    @ApiOperation("修改资源")
    @OshUserEvent(module = "内部资源模块", actionType = "修改", description = "修改资源")
    // @PreAuthorize("hasAuthority('internal:resource:edit')")
    @PutMapping
    public R<Void> update(@RequestBody com.backstage.system.domain.resource.Resource resource) {
        resourceService.updateResource(resource);
        return R.ok();
    }

    @ApiOperation("删除资源")
    @OshUserEvent(module = "内部资源模块", actionType = "删除", description = "删除资源")
    // @PreAuthorize("hasAuthority('internal:resource:remove')")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return R.ok();
    }

    @ApiOperation("按ID集合查询资源VO")
    // @PreAuthorize("hasAuthority('internal:resource:query')")
    @PostMapping("/vo-by-ids")
    public R<List<ResourceVO>> listVOByIds(@RequestBody List<Long> ids) {
        return R.ok(resourceService.listVOByIds(ids));
    }

    @ApiOperation("上传资源文件")
    @OshUserEvent(module = "内部资源模块", actionType = "上传", description = "上传资源文件")
    @PostMapping("/upload")
    public R<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 生成唯一文件ID（使用时间戳）
            String fileId = String.valueOf(System.currentTimeMillis());
            String customPath = UploadPathEnum.RESOURCE.getPath() + fileId + "/";
            // 调用OSS服务上传文件
            String filePath = ossUtil.uploadFile(file, customPath);

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("filePath", filePath);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());

            return R.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("文件上传失败：" + e.getMessage());
        }
    }

    @ApiOperation("下载资源文件")
    // @OshUserEvent(module = "内部资源模块", actionType = "下载", description = "下载资源文件")
    @PostMapping("/download/{id}")
    public void downloadFile(@PathVariable Long id, HttpServletResponse response) {
        try {
            // 获取资源详情
            com.backstage.system.domain.resource.Resource resource = resourceService.getResource(id);
            if (resource == null || resource.getFilePath() == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("资源文件不存在");
                return;
            }

            String filePath = resource.getFilePath();
            String resourceName = resource.getName();
            
            // 从文件路径中提取原始文件名（含扩展名）
            String originalFileName = filePath.substring(filePath.lastIndexOf('/') + 1);
            
            // 提取扩展名
            String extension = "";
            int extIndex = originalFileName.lastIndexOf('.');
            if (extIndex > 0) {
                extension = originalFileName.substring(extIndex);
            }
            
            // 拼接文件名：资源名称 + 扩展名
            String fileName = resourceName;
            if (!resourceName.endsWith(extension)) {
                fileName = resourceName + extension;
            }

            // 使用S3客户端获取文件
            com.amazonaws.services.s3.AmazonS3Client s3Client = ossUtil.createS3Client();
            com.amazonaws.services.s3.model.S3Object s3Object = s3Client.getObject(
                ossUtil.getOssProperties().getBucketName(), 
                filePath
            );
            
            // 获取文件的Content-Type
            String contentType = s3Object.getObjectMetadata().getContentType();
            if (contentType == null || contentType.equals("application/octet-stream")) {
                // 如果OSS没有存储Content-Type，根据文件扩展名判断
                contentType = getContentTypeByFileName(fileName);
            }
            
            InputStream inputStream = s3Object.getObjectContent();
            
            // 设置响应头
            response.setContentType(contentType);
            response.setCharacterEncoding("UTF-8");
            
            // 处理文件名（支持中文）
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
            
            // 写入响应流
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
            }
            
            response.getOutputStream().flush();
            inputStream.close();
            s3Object.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("文件下载失败：" + e.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 根据文件名获取Content-Type
     */
    private String getContentTypeByFileName(String fileName) {
        if (fileName == null) {
            return "application/octet-stream";
        }
        
        String extension = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        
        switch (extension) {
            // 文本文件
            case "txt": return "text/plain";
            case "csv": return "text/csv";
            case "html": case "htm": return "text/html";
            case "xml": return "text/xml";
            case "json": return "application/json";
            
            // 文档
            case "pdf": return "application/pdf";
            case "doc": return "application/msword";
            case "docx": return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls": return "application/vnd.ms-excel";
            case "xlsx": return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt": return "application/vnd.ms-powerpoint";
            case "pptx": return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            
            // 图片
            case "jpg": case "jpeg": return "image/jpeg";
            case "png": return "image/png";
            case "gif": return "image/gif";
            case "bmp": return "image/bmp";
            case "svg": return "image/svg+xml";
            case "webp": return "image/webp";
            
            // 视频
            case "mp4": return "video/mp4";
            case "avi": return "video/x-msvideo";
            case "mov": return "video/quicktime";
            case "wmv": return "video/x-ms-wmv";
            case "flv": return "video/x-flv";
            case "mkv": return "video/x-matroska";
            
            // 音频
            case "mp3": return "audio/mpeg";
            case "wav": return "audio/wav";
            case "aac": return "audio/aac";
            case "flac": return "audio/flac";
            
            // 压缩文件
            case "zip": return "application/zip";
            case "rar": return "application/x-rar-compressed";
            case "7z": return "application/x-7z-compressed";
            case "tar": return "application/x-tar";
            case "gz": return "application/gzip";
            
            // 代码文件
            case "js": return "application/javascript";
            case "ts": return "application/typescript";
            case "java": return "text/x-java-source";
            case "py": return "text/x-python";
            case "c": case "cpp": return "text/x-c";
            case "css": return "text/css";
            case "sql": return "text/x-sql";
            
            // 默认
            default: return "application/octet-stream";
        }
    }

}
