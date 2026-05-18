package com.backstage.system.controller.resource;

import com.backstage.common.annotation.OshUserEvent;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.vo.resource.ResourceGroupListReqVO;
import com.backstage.system.domain.vo.resource.ResourceGroupSaveVO;
import com.backstage.system.domain.vo.resource.ResourceGroupVO;
import com.backstage.system.service.resource.IResourceGroupService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 资源组 Controller
 *
 * @author backstage
 */
@ApiOperation(value = "资源组接口")
@RestController
@RequestMapping("/pc/internal/group")
public class ResourceGroupController {

    @Resource
    private IResourceGroupService groupService;

    @ApiOperation("资源组游标分页列表（按需加载）")
    // @OshUserEvent(module = "内部资源模块", actionType = "查询", description = "查询资源组列表")
    // @PreAuthorize("hasAuthority('internal:resource:list')")
    @PostMapping("/list")
    public R<List<ResourceGroupVO>> list(@RequestBody ResourceGroupListReqVO reqVO) {
        return R.ok(groupService.listByCursor(reqVO));
    }

    @ApiOperation("资源组详情")
    // @PreAuthorize("hasAuthority('internal:resource:query')")
    @GetMapping("/{id}")
    public R<ResourceGroupVO> detail(@PathVariable Long id) {
        return R.ok(groupService.getDetail(id));
    }

    @ApiOperation("新增资源组")
    // @OshUserEvent(module = "内部资源模块", actionType = "新增", description = "新增资源组")
    // @PreAuthorize("hasAuthority('internal:resource:add')")
    @PostMapping
    public R<Long> create(@RequestBody @Valid ResourceGroupSaveVO reqVO) {
        return R.ok(groupService.createGroup(reqVO));
    }

    @ApiOperation("修改资源组")
    // @OshUserEvent(module = "内部资源模块", actionType = "修改", description = "修改资源组")
    // @PreAuthorize("hasAuthority('internal:resource:edit')")
    @PutMapping
    public R<Void> update(@RequestBody @Valid ResourceGroupSaveVO reqVO) {
        groupService.updateGroup(reqVO);
        return R.ok();
    }

    @ApiOperation("删除资源组")
    // @OshUserEvent(module = "内部资源模块", actionType = "删除", description = "删除资源组")
    // @PreAuthorize("hasAuthority('internal:resource:remove')")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return R.ok();
    }
}
