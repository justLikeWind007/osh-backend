package com.backstage.system.service.impl.tool;

import com.backstage.common.async.AsyncExecutorNames;
import com.backstage.common.async.AsyncTaskSupport;
import com.backstage.common.enums.AnnouncementChannelEnum;
import com.backstage.system.domain.tool.OshToolPurchaseRecord;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.domain.websocket.WsNotifyMessage;
import com.backstage.system.mapper.tool.OshToolAnnouncementMapper;
import com.backstage.system.mapper.user.OshUserMapper;
import com.backstage.system.service.tool.ToolPurchaseAnnouncementPublisher;
import com.backstage.system.service.websocket.WebSocketNotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.Executor;

@Service
public class ToolPurchaseAnnouncementPublisherImpl implements ToolPurchaseAnnouncementPublisher {

    private static final Logger log = LoggerFactory.getLogger(ToolPurchaseAnnouncementPublisherImpl.class);
    private static final String SYSTEM_OPERATOR = "system";
    private static final String TOOL_PURCHASE_SUCCESS = "TOOL_PURCHASE_SUCCESS";
    private static final String TOOL_USER_NOTICE_REFRESH = "TOOL_USER_NOTICE_REFRESH";

    @Resource
    private OshToolAnnouncementMapper oshToolAnnouncementMapper;

    @Resource
    private OshUserMapper oshUserMapper;

    @Resource
    private WebSocketNotifyService webSocketNotifyService;

    @Resource
    private AsyncTaskSupport asyncTaskSupport;

    @Resource
    @Qualifier(AsyncExecutorNames.NOTIFICATION)
    private Executor notificationTaskExecutor;

    @Override
    public void publishPurchaseSuccess(OshToolPurchaseRecord record) {
        if (record == null) {
            return;
        }
        log.info("提交工具购买成功异步副作用任务, orderNo={}, userId={}, toolId={}",
                record.getOrderNo(), record.getUserId(), record.getToolId());
        asyncTaskSupport.runAsync(() -> doPublish(record), notificationTaskExecutor)
                .exceptionally(ex -> {
                    log.warn("工具购买公告异步发布失败, orderNo={}, error={}", record.getOrderNo(), ex.getMessage(), ex);
                    return null;
                });
    }

    private void doPublish(OshToolPurchaseRecord record) {
        log.info("开始执行工具购买成功异步副作用, orderNo={}, userId={}, toolId={}",
                record.getOrderNo(), record.getUserId(), record.getToolId());
        String username = resolveUsername(record.getUserId());
        String toolName = safeText(record.getToolNameSnapshot());
        String packageName = safeText(record.getPackageNameSnapshot());
        String title = username + "购买了「" + toolName + " " + packageName + "」";
        String jumpUrl = "/tool/detail/" + record.getToolId();

        sendPersonalSuccessNotification(record, toolName, packageName, jumpUrl);
        log.info("工具购买成功个人通知发送完成, orderNo={}, userId={}, title={}",
                record.getOrderNo(), record.getUserId(), "您购买的「" + toolName + " " + packageName + "」已到账");

        oshToolAnnouncementMapper.insertToolAnnouncement(
                title,
                jumpUrl,
                AnnouncementChannelEnum.USER_NOTICE.getCode(),
                SYSTEM_OPERATOR
        );
        log.info("工具购买成功业务公告写入完成, orderNo={}, toolId={}, title={}",
                record.getOrderNo(), record.getToolId(), title);

        WsNotifyMessage broadcast = new WsNotifyMessage();
        broadcast.setType(TOOL_USER_NOTICE_REFRESH);
        broadcast.setTitle(title);
        broadcast.setContent(null);
        broadcast.setJumpUrl(jumpUrl);
        broadcast.setBizId(record.getToolId() == null ? null : String.valueOf(record.getToolId()));
        webSocketNotifyService.broadcast(broadcast);
        log.info("工具购买成功公告广播完成, orderNo={}, toolId={}, type={}",
                record.getOrderNo(), record.getToolId(), TOOL_USER_NOTICE_REFRESH);
    }

    private void sendPersonalSuccessNotification(OshToolPurchaseRecord record,
                                                 String toolName,
                                                 String packageName,
                                                 String jumpUrl) {
        if (record.getUserId() == null) {
            return;
        }
        WsNotifyMessage personal = new WsNotifyMessage();
        personal.setType(TOOL_PURCHASE_SUCCESS);
        personal.setTitle("您购买的「" + toolName + " " + packageName + "」已到账");
        personal.setContent("工具使用次数已发放成功，点击可前往工具详情页查看。");
        personal.setJumpUrl(jumpUrl);
        personal.setBizId(record.getToolId() == null ? null : String.valueOf(record.getToolId()));
        webSocketNotifyService.send(record.getUserId(), personal);
    }

    private String resolveUsername(Long userId) {
        if (userId == null) {
            return "有用户";
        }
        OshUser user = oshUserMapper.selectUserById(userId);
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return "有用户";
        }
        return user.getUsername();
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }
}
