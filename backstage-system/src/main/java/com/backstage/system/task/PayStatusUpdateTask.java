package com.backstage.system.task;

import com.backstage.system.domain.order.OshOrder;
import com.backstage.system.domain.order.enums.OrderStatusEnum;
import com.backstage.system.domain.order.enums.PaymentStatusEnum;
import com.backstage.system.mapper.order.OshOrderMapper;
import com.backstage.system.service.order.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class PayStatusUpdateTask {
    private static final Logger log = LoggerFactory.getLogger(FeedbackCountRepairTask.class);


    @Resource
    private OrderService orderService;


    @Scheduled(cron = "0 0/30 * * * ?")
    public void updateOrderStatus() {
        log.info("【支付-定时任务】 开始更新订单状态，如超时订单关闭");


        try{
            // 超时订单自动关闭
            List<OshOrder> expiredOrderList = orderService.list(new LambdaQueryWrapper<OshOrder>()
                    .le(OshOrder::getCreatedTime, LocalDateTime.now().minusNanos(30 * 60))
                    .eq(OshOrder::getDeleteFlag, 0)
            );

            expiredOrderList.forEach(
                    order -> {
                        order.setStatus(OrderStatusEnum.CANCELED.getCode());
                    });
            orderService.updateBatchById(expiredOrderList);
        }catch (Exception e){
            log.error("【支付-定时任务】更新订单状态异常, 异常详情:{}",e.getMessage(),e);
        }

        log.info("【支付】 更新订单状态完成，如超时订单关闭");
    }
}