package com.backstage.system.controller.tool;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.DistributeLock;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.enums.UploadPathEnum;
import com.backstage.common.exception.ServiceException;
import com.backstage.common.response.PageResponse;
import com.backstage.system.constants.CourseUploadConstants;
import com.backstage.system.domain.tool.OshTool;
import com.backstage.system.domain.tool.OshToolTag;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.request.tool.ToolCollectionRequest;
import com.backstage.system.request.tool.ToolDeleteRequest;
import com.backstage.system.request.tool.ToolSaveRequest;
import com.backstage.system.request.tool.ToolSearchRequest;
import com.backstage.system.service.common.OssService;
import com.backstage.system.service.tool.IOshToolCollectionService;
import com.backstage.system.service.tool.IOshToolService;
import com.backstage.system.utils.UserContextUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Api(tags = "工具管理")
@RestController
@RequestMapping("/pc/tool")
public class OshToolController extends BaseController {

    @Autowired
    private IOshToolService oshToolService;

    @Autowired
    private IOshToolCollectionService oshToolCollectionService;

    @Autowired
    private OssService ossService;

    @ApiOperation("工具搜索")
    @PostMapping("/search")
    @Anonymous
    public R<PageResponse<OshTool>> search(@RequestBody ToolSearchRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        Long userId = currentOshUser == null ? null : currentOshUser.getId();
        if (request == null) {
            request = new ToolSearchRequest();
        }
        if (Integer.valueOf(1).equals(request.getCollectionFlag()) && userId == null) {
            return R.ok(PageResponse.of(Collections.emptyList(), 0L, request.getPageNum(), request.getPageSize()), "ok");
        }
        List<OshTool> list = oshToolService.pageQuerySearchTool(userId, request);
        com.github.pagehelper.PageInfo<OshTool> pageInfo = new com.github.pagehelper.PageInfo<>(list);
        return R.ok(PageResponse.of(pageInfo.getList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize()), "ok");
    }

    @ApiOperation("工具标签选项")
    @GetMapping("/tags")
    @Anonymous
    public R<List<OshToolTag>> listTags() {
        return R.ok(oshToolService.listAvailableTags());
    }

    @ApiOperation("上传工具封面")
    @PostMapping("/cover/upload")
    @PreAuthorize("hasAuthority('tool:create') or hasAuthority('tool:update')")
    public R<Map<String, Object>> uploadToolCover(
            @ApiParam("封面文件") @RequestParam("file") MultipartFile file,
            @ApiParam("封面名称") @RequestParam(value = "coverName", required = false) String coverName) {
        String fileName = file.getOriginalFilename();
        String extension = "";
        if (fileName != null && fileName.contains(".")) {
            extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        }
        if (!CourseUploadConstants.ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            throw new ServiceException(CourseUploadConstants.IMAGE_FORMAT_ERROR);
        }
        if (file.getSize() > CourseUploadConstants.MAX_IMAGE_SIZE) {
            throw new ServiceException(CourseUploadConstants.IMAGE_SIZE_ERROR);
        }

        try {
            String relativePath = ossService.upload(file, UploadPathEnum.TOOL_COVER, "covers");
            if (relativePath == null || CourseUploadConstants.isUploadError(relativePath)) {
                throw new ServiceException(relativePath);
            }
            Map<String, Object> coverInfo = new HashMap<>();
            coverInfo.put("coverName", StringUtils.defaultIfBlank(coverName, fileName));
            coverInfo.put("url", ossService.getLimitedUrl(relativePath, 1440));
            coverInfo.put("relativePath", relativePath);
            coverInfo.put("size", file.getSize());
            coverInfo.put("type", extension);
            return R.ok(coverInfo);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException("上传工具封面失败：" + ex.getMessage());
        }
    }

    @ApiOperation("获取工具封面临时URL")
    @GetMapping("/cover/url")
    @Anonymous
    public R<String> getToolCoverUrl(
            @ApiParam("工具封面相对路径") @RequestParam("path") String path,
            @ApiParam("URL有效期（分钟），默认30") @RequestParam(value = "minute", required = false, defaultValue = "30") Integer minute) {
        if (StringUtils.isBlank(path)) {
            return R.fail("封面路径不能为空");
        }
        int validMinute = Math.min(minute != null ? minute : 30, 120);
        return R.ok(oshToolService.getToolLogoUrl(path, validMinute));
    }

    @ApiOperation("批量获取工具封面临时URL")
    @GetMapping("/cover/urls")
    @Anonymous
    public R<Map<String, String>> batchGetToolCoverUrls(
            @ApiParam("工具封面相对路径列表，最多50个，逗号分隔") @RequestParam("paths") String paths,
            @ApiParam("URL有效期（分钟），默认30") @RequestParam(value = "minute", required = false, defaultValue = "30") Integer minute) {
        List<String> pathList = new ArrayList<>();
        if (StringUtils.isNotBlank(paths)) {
            for (String path : paths.split(",")) {
                if (StringUtils.isNotBlank(path)) {
                    pathList.add(path.trim());
                }
            }
        }
        if (pathList.isEmpty()) {
            return R.ok(new HashMap<>(), "ok");
        }
        if (pathList.size() > 50) {
            pathList = pathList.subList(0, 50);
        }
        int validMinute = Math.min(minute != null ? minute : 30, 120);
        return R.ok(oshToolService.batchGetToolLogoUrlsByPaths(pathList, validMinute), "ok");
    }

    @ApiOperation("工具详情")
    @GetMapping("/detail/{id}")
    @Anonymous
    public R<OshTool> getToolDetail(@NotNull @PathVariable("id") Long id) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        Long userId = currentOshUser == null ? null : currentOshUser.getId();
        OshTool tool = oshToolService.getToolDetail(id, userId);
        return tool == null ? R.fail("工具不存在") : R.ok(tool);
    }

    @ApiOperation("新增/修改工具")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('tool:create')")
    @DistributeLock(scene = "resource", key = "operation", expireTime = 10000, waitTime = 3000, releaseImmediately = true)
    public R<Long> save(@Validated @RequestBody ToolSaveRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
        }
        try {
            Long toolId = request.getId() == null
                    ? oshToolService.createTool(request, currentOshUser)
                    : oshToolService.updateTool(request, currentOshUser);
            return R.ok(toolId);
        } catch (IllegalArgumentException ex) {
            return R.fail(ex.getMessage());
        }
    }

    @ApiOperation("修改工具")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('tool:update')")
    @DistributeLock(scene = "resource", key = "operation", expireTime = 10000, waitTime = 3000, releaseImmediately = true)
    public R<Long> update(@Validated @RequestBody ToolSaveRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
        }
        if (request.getId() == null) {
            return R.fail("工具ID不能为空");
        }
        try {
            return R.ok(oshToolService.updateTool(request, currentOshUser));
        } catch (IllegalArgumentException ex) {
            return R.fail(ex.getMessage());
        }
    }

    @ApiOperation("批量删除工具")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('tool:delete')")
    public R<String> deleteTools(@Validated @RequestBody ToolDeleteRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
        }
        try {
            oshToolService.deleteToolsByIds(request.getIds(), currentOshUser);
            return R.ok("删除成功");
        } catch (IllegalArgumentException ex) {
            return R.fail(ex.getMessage());
        }
    }

    @ApiOperation("收藏工具")
    @PostMapping("/collection/add")
    @PreAuthorize("hasAuthority('tool:collection:add')")
    public R<String> collectTool(@Validated @RequestBody ToolCollectionRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
        }
        oshToolCollectionService.collectTool(currentOshUser.getId(), currentOshUser.getUsername(), request.getToolId());
        return R.ok("收藏工具成功");
    }

    @ApiOperation("取消收藏工具")
    @PostMapping("/collection/remove")
    @PreAuthorize("hasAuthority('tool:collection:remove')")
    public R<String> removeToolCollection(@Validated @RequestBody ToolCollectionRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
        }
        oshToolCollectionService.removeToolCollection(currentOshUser.getId(), currentOshUser.getUsername(), request.getToolId());
        return R.ok("取消工具收藏成功");
    }
}
