package com.backstage.system.service.tool;

import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.core.redis.RedisCache;
import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.tool.OshTool;
import com.backstage.system.domain.tool.OshToolPackage;
import com.backstage.system.domain.tool.OshToolPurchaseRecord;
import com.backstage.system.domain.user.OshUserAsset;
import com.backstage.system.mapper.tool.OshToolMapper;
import com.backstage.system.mapper.tool.OshToolPackageMapper;
import com.backstage.system.mapper.tool.OshToolPurchaseRecordMapper;
import com.backstage.system.mapper.user.OshUserAssetMapper;
import com.backstage.system.mapper.user.OshUserAssetRecordMapper;
import com.backstage.system.request.tool.ToolPurchaseCreateRequest;
import com.backstage.system.service.impl.tool.ToolPurchaseServiceImpl;
import com.backstage.system.service.order.OrderCheckoutService;
import com.backstage.system.domain.vo.pay.OrderCheckoutRespVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ToolPurchaseServiceImplTest {

    @InjectMocks
    private ToolPurchaseServiceImpl toolPurchaseService;

    @Mock
    private OshToolMapper oshToolMapper;

    @Mock
    private OshToolPackageMapper oshToolPackageMapper;

    @Mock
    private OshToolPurchaseRecordMapper oshToolPurchaseRecordMapper;

    @Mock
    private OshUserAssetMapper oshUserAssetMapper;

    @Mock
    private OshUserAssetRecordMapper oshUserAssetRecordMapper;

    @Mock
    private RedisCache redisCache;

    @Mock
    private OrderCheckoutService orderCheckoutService;

    @Test
    public void shouldCreateCashOnlyToolPurchaseOrderWithSnapshotRecord() {
        ToolPurchaseCreateRequest request = new ToolPurchaseCreateRequest();
        request.setToolId(1001L);
        request.setPackageId(2001L);
        request.setPayType(1);
        request.setChannel("wxpay");

        OshTool tool = new OshTool();
        tool.setId(1001L);
        tool.setToolName("AI海报生成器");
        tool.setStatus(4);
        tool.setResourceType("CASH_ONLY");

        OshToolPackage toolPackage = new OshToolPackage();
        toolPackage.setId(2001L);
        toolPackage.setToolId(1001L);
        toolPackage.setPackageName("体验包");
        toolPackage.setUseCount(10);
        toolPackage.setPrice(new BigDecimal("9.90"));
        toolPackage.setPointCost(0);
        toolPackage.setPayType(1);
        toolPackage.setStatus(1);

        OrderCheckoutRespVO checkoutRespVO = new OrderCheckoutRespVO();
        checkoutRespVO.setOrderNo("O20260517001");
        checkoutRespVO.setPaymentNo("P20260517001");
        checkoutRespVO.setPrice(new BigDecimal("9.90"));
        checkoutRespVO.setNeedPay(true);

        when(oshToolMapper.selectToolById(1001L)).thenReturn(tool);
        when(oshToolPackageMapper.selectPackageById(2001L)).thenReturn(toolPackage);
        when(orderCheckoutService.checkout(any())).thenReturn(checkoutRespVO);
        when(oshToolPurchaseRecordMapper.insertToolPurchaseRecord(any(OshToolPurchaseRecord.class))).thenReturn(1);

        OrderCheckoutRespVO result = toolPurchaseService.createPurchaseOrder(9L, "normal", request);

        assertEquals("O20260517001", result.getOrderNo());
        verify(oshToolPurchaseRecordMapper).insertToolPurchaseRecord(argThat(record ->
                "O20260517001".equals(record.getOrderNo())
                        && Integer.valueOf(1).equals(record.getPackagePayTypeSnapshot())
                        && Integer.valueOf(10).equals(record.getPackageUseCountSnapshot())
                        && new BigDecimal("9.90").compareTo(record.getPackageCashAmountSnapshot()) == 0
        ));
        verify(oshUserAssetMapper, never()).updateById(any(OshUserAsset.class));
    }
    

    @Test(expected = ServiceException.class)
    public void shouldRejectCreateOrderWhenPointsAreInsufficientForCashPointPackage() {
        ToolPurchaseCreateRequest request = new ToolPurchaseCreateRequest();
        request.setToolId(1002L);
        request.setPackageId(2002L);
        request.setPayType(3);
        request.setChannel("alipay");

        OshTool tool = new OshTool();
        tool.setId(1002L);
        tool.setToolName("短视频脚本助手");
        tool.setStatus(4);
        tool.setResourceType("CASH_POINT");

        OshToolPackage toolPackage = new OshToolPackage();
        toolPackage.setId(2002L);
        toolPackage.setToolId(1002L);
        toolPackage.setPackageName("推荐包");
        toolPackage.setUseCount(50);
        toolPackage.setPrice(new BigDecimal("29.90"));
        toolPackage.setPointCost(100);
        toolPackage.setPayType(3);
        toolPackage.setStatus(1);

        OshUserAsset userAsset = new OshUserAsset();
        userAsset.setUserId(9L);
        userAsset.setPoints(99L);

        when(oshToolMapper.selectToolById(1002L)).thenReturn(tool);
        when(oshToolPackageMapper.selectPackageById(2002L)).thenReturn(toolPackage);
        when(oshUserAssetMapper.selectById(9L)).thenReturn(userAsset);

        try {
            toolPurchaseService.createPurchaseOrder(9L, "normal", request);
        } finally {
            verify(orderCheckoutService, never()).checkout(any());
        }
    }
}
