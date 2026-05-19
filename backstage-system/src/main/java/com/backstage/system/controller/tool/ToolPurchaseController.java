package com.backstage.system.controller.tool;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import com.backstage.common.response.PageResponse;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.domain.vo.pay.OrderCheckoutRespVO;
import com.backstage.system.domain.vo.tool.ToolPurchaseDetailVO;
import com.backstage.system.domain.vo.tool.ToolPurchaseListVO;
import com.backstage.system.request.tool.ToolPurchaseCancelRequest;
import com.backstage.system.request.tool.ToolPurchaseCreateRequest;
import com.backstage.system.request.tool.ToolPurchaseListRequest;
import com.backstage.system.service.order.OrderService;
import com.backstage.system.service.tool.ToolPurchaseService;
import com.backstage.system.utils.UserContextUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "工具购买")
@Validated
@RestController
@RequestMapping("/pc/tool/purchase")
public class ToolPurchaseController {

    @Autowired
    private ToolPurchaseService toolPurchaseService;

    @Autowired
    private OrderService orderService;

    @ApiOperation("查询工具购买详情")
    @GetMapping("/detail")
    @Anonymous
    public R<ToolPurchaseDetailVO> detail(@RequestParam Long toolId) {
        OshUser currentUser = UserContextUtil.getCurrentUser();
        Long userId = currentUser == null ? null : currentUser.getId();
        return R.ok(toolPurchaseService.getPurchaseDetail(toolId, userId));
    }

    @ApiOperation("创建工具购买订单")
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('tool:purchase:create')")
    public R<OrderCheckoutRespVO> create(@Validated @RequestBody ToolPurchaseCreateRequest request) {
        OshUser currentUser = UserContextUtil.getCurrentUser();
        if (currentUser == null) {
            return R.fail("请先登录");
        }
        return R.ok(toolPurchaseService.createPurchaseOrder(currentUser.getId(), currentUser.getUsername(), request));
    }

    @ApiOperation("查询我的工具购买记录")
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('tool:purchase:list')")
    public R<PageResponse<ToolPurchaseListVO>> list(@RequestBody(required = false) ToolPurchaseListRequest request) {
        OshUser currentUser = UserContextUtil.getCurrentUser();
        if (currentUser == null) {
            return R.fail("请先登录");
        }
        return R.ok(toolPurchaseService.listPurchaseRecords(currentUser.getId(), request));
    }

    @ApiOperation("手动关闭工具购买订单")
    @PostMapping("/cancel")
    @PreAuthorize("hasAuthority('tool:purchase:cancel')")
    public R<String> cancel(@Validated @RequestBody ToolPurchaseCancelRequest request) {
        OshUser currentUser = UserContextUtil.getCurrentUser();
        if (currentUser == null) {
            return R.fail("请先登录");
        }
        orderService.cancelPayment(request.getPaymentNo());
        return R.ok("关单成功");
    }
}
