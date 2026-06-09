package com.backstage.system.controller.resource;

import com.backstage.common.annotation.OshUserEvent;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.resource.Link;
import com.backstage.system.domain.vo.resource.ResourceLinkVO;
import com.backstage.system.service.resource.IResourceLinkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 资源链接 Controller
 *
 * @author backstage
 */
@ApiOperation(value = "资源链接接口")
@RestController
@RequestMapping("/pc/internal/link")
public class ResourceLinkController {

    @Resource
    private IResourceLinkService linkService;

    @ApiOperation("链接分页列表")
//    @OshUserEvent(module = "内部资源模块", actionType = "查询", description = "查询链接分页")
//    @PreAuthorize("hasAuthority('internal:resource:list')")
    @GetMapping("/page")
    public R<Page<Link>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "20") long pageSize) {
        Page<Link> page = new Page<>(pageNum, pageSize);
        return R.ok(linkService.pageLink(keyword, page));
    }

    @ApiOperation("全量链接列表（下拉选择）")
//    @PreAuthorize("hasAuthority('internal:resource:query')")
    @GetMapping("/list")
    public R<List<Link>> list(@RequestParam(required = false) String keyword) {
        Page<Link> page = new Page<>(1, 1000);
        return R.ok(linkService.pageLink(keyword, page).getRecords());
    }

    @ApiOperation("链接详情")
//    @PreAuthorize("hasAuthority('internal:resource:query')")
    @GetMapping("/{id}")
    public R<Link> detail(@PathVariable Long id) {
        return R.ok(linkService.getLink(id));
    }

    @ApiOperation("新增链接")
//    @OshUserEvent(module = "内部资源模块", actionType = "新增", description = "新增链接")
//    @PreAuthorize("hasAuthority('internal:resource:add')")
    @PostMapping
    public R<Long> create(@RequestBody Link link) {
        return R.ok(linkService.createLink(link));
    }

    @ApiOperation("修改链接")
//    @OshUserEvent(module = "内部资源模块", actionType = "修改", description = "修改链接")
//    @PreAuthorize("hasAuthority('internal:resource:edit')")
    @PutMapping
    public R<Void> update(@RequestBody Link link) {
        linkService.updateLink(link);
        return R.ok();
    }

    @ApiOperation("删除链接")
//    @OshUserEvent(module = "内部资源模块", actionType = "删除", description = "删除链接")
//    @PreAuthorize("hasAuthority('internal:resource:remove')")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        linkService.deleteLink(id);
        return R.ok();
    }

    @ApiOperation("按ID集合查询链接VO")
//    @PreAuthorize("hasAuthority('internal:resource:query')")
    @PostMapping("/vo-by-ids")
    public R<List<ResourceLinkVO>> listVOByIds(@RequestBody List<Long> ids) {
        return R.ok(linkService.listVOByIds(ids));
    }
}
