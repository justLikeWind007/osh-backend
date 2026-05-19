package com.backstage.system.service.tool;

import com.backstage.common.response.PageResponse;
import com.backstage.system.domain.vo.pay.OrderCheckoutRespVO;
import com.backstage.system.domain.vo.tool.ToolPurchaseDetailVO;
import com.backstage.system.domain.vo.tool.ToolPurchaseListVO;
import com.backstage.system.request.tool.ToolPurchaseCreateRequest;
import com.backstage.system.request.tool.ToolPurchaseListRequest;

public interface ToolPurchaseService {

    ToolPurchaseDetailVO getPurchaseDetail(Long toolId, Long userId);

    OrderCheckoutRespVO createPurchaseOrder(Long userId, String operator, ToolPurchaseCreateRequest request);

    PageResponse<ToolPurchaseListVO> listPurchaseRecords(Long userId, ToolPurchaseListRequest request);

    void cancelPendingPurchase(String paymentNo);
}
