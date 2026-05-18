package com.backstage.system.service.order.handler;

import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.order.enums.ProductTypeEnum;
import com.backstage.system.domain.tool.OshToolPurchaseRecord;
import com.backstage.system.mapper.tool.OshToolPurchaseRecordMapper;
import com.backstage.system.mapper.tool.OshToolQuotaMapper;
import com.backstage.system.service.order.OrderPaidHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Component
public class ToolPaidHandler implements OrderPaidHandler {

    private static final String SYSTEM_OPERATOR = "system";
    private static final int GRANT_STATUS_SUCCESS = 1;

    @Resource
    private OshToolPurchaseRecordMapper oshToolPurchaseRecordMapper;

    @Resource
    private OshToolQuotaMapper oshToolQuotaMapper;

    @Override
    public String bizType() {
        return ProductTypeEnum.TOOL.name();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handle(String orderNo) {
        OshToolPurchaseRecord record = oshToolPurchaseRecordMapper.selectByOrderNo(orderNo);
        if (record == null) {
            throw new ServiceException("工具购买记录不存在");
        }
        if (Integer.valueOf(GRANT_STATUS_SUCCESS).equals(record.getGrantStatus())) {
            return;
        }
        try {
            int updated = oshToolQuotaMapper.increaseUserToolQuota(
                    record.getToolId(),
                    record.getUserId(),
                    record.getPackageUseCountSnapshot(),
                    SYSTEM_OPERATOR
            );
            if (updated <= 0) {
                oshToolQuotaMapper.insertUserToolQuota(
                        record.getUserId(),
                        record.getToolId(),
                        record.getPackageUseCountSnapshot(),
                        SYSTEM_OPERATOR
                );
            }
            oshToolPurchaseRecordMapper.updateOrderStatusByOrderNo(orderNo, 1, SYSTEM_OPERATOR);
            int success = oshToolPurchaseRecordMapper.updateGrantSuccess(record.getId(), LocalDateTime.now(), SYSTEM_OPERATOR);
            if (success <= 0) {
                throw new ServiceException("更新工具购买发放状态失败");
            }
        } catch (Exception ex) {
            oshToolPurchaseRecordMapper.updateGrantFailed(record.getId(), ex.getMessage(), SYSTEM_OPERATOR);
            throw ex;
        }
    }
}
