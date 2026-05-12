package com.backstage.system.controller.audit;

import com.backstage.common.core.domain.R;
import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.audit.ResourceAuditApproveRequest;
import com.backstage.system.domain.audit.ResourceAuditPageVO;
import com.backstage.system.domain.audit.ResourceAuditRequest;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.service.audit.IResourceAuditService;
import com.backstage.system.utils.UserContextUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "统一资源审核")
@RestController
@RequestMapping("/pc/audit")
public class ResourceAuditController {

    @Autowired
    private IResourceAuditService resourceAuditService;

    @ApiOperation("分页查询待审核资源")
    @PostMapping("/pending")
    @PreAuthorize("hasAuthority('audit:list')")
    public R<ResourceAuditPageVO> pending(@Validated @RequestBody ResourceAuditRequest request) {
        try {
            return R.ok(resourceAuditService.pagePending(request));
        } catch (IllegalArgumentException | ServiceException ex) {
            return R.fail(ex.getMessage());
        }
    }

    @ApiOperation("审核通过资源")
    @PostMapping("/approve")
    @PreAuthorize("hasAuthority('audit:approve')")
    public R<String> approve(@Validated @RequestBody ResourceAuditApproveRequest request) {
        OshUser currentUser = UserContextUtil.getCurrentUser();
        if (currentUser == null) {
            return R.fail("请先登录");
        }
        try {
            resourceAuditService.approve(request.getResourceType(), request.getResourceId(), currentUser.getUsername(), currentUser.getId());
            return R.ok("审核通过");
        } catch (IllegalArgumentException | ServiceException ex) {
            return R.fail(ex.getMessage());
        }
    }
}
