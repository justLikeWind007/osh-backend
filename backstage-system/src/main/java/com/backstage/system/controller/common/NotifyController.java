package com.backstage.system.controller.common;

import com.backstage.common.annotation.Anonymous;
import com.backstage.system.domain.servergroup.OshGroupOrder;
import com.backstage.system.domain.servergroup.OshGroupUserInitiated;
import com.backstage.system.mapper.servergroup.OshGroupServerMapper;
import com.backstage.system.mapper.servergroup.OshGroupUserInitiatedMapper;
import com.backstage.system.utils.SignUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/notify")
public class NotifyController {
    
    private static final Logger log = LoggerFactory.getLogger(NotifyController.class);
    
    @Autowired
    private OshGroupServerMapper groupServerMapper;
    
    @Autowired
    private OshGroupUserInitiatedMapper userInitiatedMapper;
    
    @Anonymous
    @GetMapping("/pay")
    public String notify(HttpServletRequest request) {
        try {
            // 接收回调参数
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> req = request.getParameterMap();
            for (String key : req.keySet()) {
                params.put(key, req.get(key)[0]);
            }

            log.info("收到支付回调: {}", params);

            // 验签 防止伪造
            String sign = params.get("sign");
            String localSign = SignUtil.createSign(params);

            if (!sign.equals(localSign)) {
                log.error("支付回调验签失败");
                return "FAIL";
            }

            // 支付成功
            String tradeStatus = params.get("trade_status");
            if ("TRADE_SUCCESS".equals(tradeStatus)) {
                String outTradeNo = params.get("out_trade_no");
                log.info("订单支付成功，订单号: {}", outTradeNo);
                
                // 处理支付成功逻辑
                handlePaymentSuccess(outTradeNo);
                
                return "success";
            }
            return "FAIL";
        } catch (Exception e) {
            log.error("支付回调处理异常", e);
            return "FAIL";
        }
    }
    
    /**
     * 处理支付成功逻辑
     */
    private void handlePaymentSuccess(String orderNo) {
        try {
            // 1. 查询订单
            OshGroupOrder order = groupServerMapper.selectGroupOrderByOrderNo(orderNo);
            if (order == null) {
                log.error("订单不存在: {}", orderNo);
                return;
            }
            
            // 2. 检查订单状态（避免重复处理）
            if ("paid".equals(order.getStatus()) || "success".equals(order.getStatus())) {
                log.info("订单已处理，跳过: {}", orderNo);
                return;
            }
            
            // 3. 更新订单状态为已支付
            int updateOrderResult = groupServerMapper.updateOrderStatus(
                order.getId(), 
                "paid",
                LocalDateTime.now()
            );
            
            if (updateOrderResult <= 0) {
                log.error("更新订单状态失败: {}", orderNo);
                return;
            }
            
            log.info("订单状态更新为已支付: {}", orderNo);
            
            // 4. 更新参团记录状态
            if (order.getGroupWorkId() != null) {
                groupServerMapper.updateGroupWorkStatus(order.getGroupWorkId(), 1); // 1-已成团
                log.info("参团记录状态更新为已成团: {}", order.getGroupWorkId());
            }
            
            // 5. 如果是系统活动拼团，更新活动人数和状态
            if (order.getGroupActivityId() != null) {
                updateSystemActivity(order);
            }
            
            // 6. 如果是用户发起拼团，更新发起记录人数和状态
            if (order.getGroupWorkId() != null) {
                updateUserInitiated(order.getGroupWorkId());
            }
            
            // TODO: 7. 发送拼团成功通知（WebSocket/短信/邮件）
            // TODO: 8. 开通服务器权限
            // TODO: 9. 记录操作日志
            
            log.info("订单支付处理完成: {}", orderNo);
            
        } catch (Exception e) {
            log.error("处理支付成功逻辑失败，订单号: {}", orderNo, e);
            throw new RuntimeException("处理支付成功逻辑失败", e);
        }
    }
    
    /**
     * 更新系统活动拼团人数和状态
     */
    private void updateSystemActivity(OshGroupOrder order) {
        com.backstage.system.domain.servergroup.OshGroupActivity activity = groupServerMapper.selectGroupActivityById(order.getGroupActivityId());
        if (activity == null) {
            log.warn("拼团活动不存在，ID: {}", order.getGroupActivityId());
            return;
        }
        
        // 增加人数
        int newCurrentNum = activity.getCurrentNum() + 1;
        
        // 判断是否达到最低人数（成团）
        boolean willSuccess = newCurrentNum >= activity.getGroupMinNum();
        
        if (willSuccess) {
            // 成团：更新人数、状态、服务器时间
            LocalDateTime serverStartTime = LocalDateTime.now();
            LocalDateTime serverEndTime = serverStartTime.plusMonths(activity.getTotalDuration());
            
            int updateResult = groupServerMapper.updateGroupActivityWithLock(
                order.getGroupActivityId(),
                newCurrentNum,
                2, // 拼团成功
                serverStartTime,
                serverEndTime,
                LocalDateTime.now()
            );
            
            if (updateResult > 0) {
                log.info("系统活动拼团成团，活动ID: {}, 当前人数: {}", order.getGroupActivityId(), newCurrentNum);
            }
        } else {
            // 未成团：只更新人数
            int updateResult = groupServerMapper.incrementGroupActivityCurrentNum(
                order.getGroupActivityId(),
                newCurrentNum,
                activity.getStatus(),
                LocalDateTime.now()
            );
            
            if (updateResult > 0) {
                log.info("系统活动拼团人数增加，活动ID: {}, 当前人数: {}", order.getGroupActivityId(), newCurrentNum);
            }
        }
        
        // 如果达到人数上限，更新状态为已结束
        if (newCurrentNum >= activity.getGroupMaxNum()) {
            groupServerMapper.updateGroupActivityStatusEnded(order.getGroupActivityId(), LocalDateTime.now());
            log.info("系统活动拼团人数已满，状态更新为已结束，活动ID: {}", order.getGroupActivityId());
        }
    }
    
    /**
     * 更新用户发起拼团人数和状态
     */
    private void updateUserInitiated(Long initiatedId) {
        OshGroupUserInitiated initiated = userInitiatedMapper.selectById(initiatedId);
        if (initiated == null) {
            // 可能不是用户发起的拼团，忽略
            return;
        }
        
        // 增加人数
        int newCurrentNum = initiated.getCurrentNum() + 1;
        
        int updateResult = userInitiatedMapper.updateCurrentNum(initiatedId, newCurrentNum);
        
        if (updateResult > 0) {
            log.info("用户发起拼团人数增加，记录ID: {}, 当前人数: {}", initiatedId, newCurrentNum);
            
            // 判断是否达到最低人数（成团）
            boolean willSuccess = newCurrentNum >= initiated.getMinNum();
            
            if (willSuccess && initiated.getGroupStatus() != 1) {
                // 成团：设置服务器时间
                LocalDateTime serverStartTime = LocalDateTime.now();
                LocalDateTime serverExpireTime = serverStartTime.plusMonths(initiated.getDuration());
                
                // 更新状态和服务器时间
                userInitiatedMapper.updateGroupStatus(initiatedId, 1);
                userInitiatedMapper.updateServerTime(initiatedId, serverStartTime, serverExpireTime);
                log.info("用户发起拼团成团，记录ID: {}, serverStartTime: {}, serverExpireTime: {}", 
                        initiatedId, serverStartTime, serverExpireTime);
            }
            
            // 如果达到人数上限，更新状态为已结束
            if (newCurrentNum >= initiated.getMaxNum()) {
                userInitiatedMapper.updateGroupStatus(initiatedId, 2);
                log.info("用户发起拼团人数已满，状态更新为已结束，记录ID: {}", initiatedId);
            }
        }
    }
}