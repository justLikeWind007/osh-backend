package com.backstage.system.controller.tool;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.DistributeLock;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.exception.ServiceException;
import com.backstage.common.response.PageResponse;
import com.backstage.system.domain.tool.OshTool;
import com.backstage.system.domain.tool.OshToolTag;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.request.tool.ToolCollectionRequest;
import com.backstage.system.request.tool.ToolDeleteRequest;
import com.backstage.system.request.tool.ToolRecommendRequest;
import com.backstage.system.request.tool.ToolSaveRequest;
import com.backstage.system.request.tool.ToolSearchRequest;
import com.backstage.system.request.tool.ToolUsageConsumeRequest;
import com.backstage.system.request.tool.ToolVoteRequest;
import com.backstage.system.service.tool.IOshToolCollectionService;
import com.backstage.system.service.tool.IOshToolService;
import com.backstage.system.utils.UserContextUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@Api(tags = "工具管理")
@RestController
@RequestMapping("/pc/tool")
public class OshToolController extends BaseController {

    @Autowired
    private IOshToolService oshToolService;

    @Autowired
    private IOshToolCollectionService oshToolCollectionService;

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

    @ApiOperation("工具推荐列表")
    @PostMapping("/recommend")
    @Anonymous
    public R<PageResponse<OshTool>> recommend(@RequestBody ToolRecommendRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        Long userId = currentOshUser == null ? null : currentOshUser.getId();
        if (request == null) {
            request = new ToolRecommendRequest();
        }
        List<OshTool> list = oshToolService.listRecommendTools(userId, request);
        com.github.pagehelper.PageInfo<OshTool> pageInfo = new com.github.pagehelper.PageInfo<>(list);
        return R.ok(PageResponse.of(pageInfo.getList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize()), "ok");
    }

    @ApiOperation("工具标签选项")
    @GetMapping("/tags")
    @Anonymous
    public R<List<OshToolTag>> listTags() {
        return R.ok(oshToolService.listAvailableTags());
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

    @ApiOperation("扣减工具使用次数")
    @PostMapping("/use/consume")
    public R<Integer> consumeToolUsage(@Validated @RequestBody ToolUsageConsumeRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
        }
        try {
            Integer remainingCount = oshToolService.consumeToolUsage(
                    currentOshUser.getId(),
                    currentOshUser.getUsername(),
                    request.getToolId()
            );
            return R.ok(remainingCount);
        } catch (IllegalArgumentException | ServiceException ex) {
            return R.fail(ex.getMessage());
        }
    }

    @ApiOperation("点赞工具")
    @PostMapping("/vote/good")
    @PreAuthorize("hasAuthority('tool:vote:good')")
    public R<Integer> voteGoodTool(@Validated @RequestBody ToolVoteRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
        }
        try {
            Integer voteType = oshToolService.voteTool(currentOshUser.getId(), currentOshUser.getUsername(), request.getToolId(), 1);
            return R.ok(voteType);
        } catch (IllegalArgumentException | ServiceException ex) {
            return R.fail(ex.getMessage());
        }
    }

    @ApiOperation("差评工具")
    @PostMapping("/vote/bad")
    @PreAuthorize("hasAuthority('tool:vote:bad')")
    public R<Integer> voteBadTool(@Validated @RequestBody ToolVoteRequest request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        if (currentOshUser == null) {
            return R.fail("请先登录");
        }
        try {
            Integer voteType = oshToolService.voteTool(currentOshUser.getId(), currentOshUser.getUsername(), request.getToolId(), 3);
            return R.ok(voteType);
        } catch (IllegalArgumentException | ServiceException ex) {
            return R.fail(ex.getMessage());
        }
    }
}
