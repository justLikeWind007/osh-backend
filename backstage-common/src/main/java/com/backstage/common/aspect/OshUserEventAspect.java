package com.backstage.common.aspect;

import com.alibaba.fastjson2.JSON;
import com.backstage.common.constant.OshResourceConstants;
import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.core.domain.OshUserEvent;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.redis.RedisCache;
import com.backstage.common.enums.ResultCode;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.common.utils.generate.GenerateUtil;
import com.backstage.common.utils.kafka.KafkaMessageUtil;
import com.backstage.common.utils.list.ListUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/12
 * Time: 15:54
 */
@Aspect
@Component
@Order(2)
public class OshUserEventAspect {
    private static final Logger logger = LoggerFactory.getLogger(OshUserEventAspect.class);

    @Autowired
    private RedisCache redisCache;

    @Around("@annotation(oshUserEvent)")
    public Object userAction(ProceedingJoinPoint joinPoint, com.backstage.common.annotation.OshUserEvent oshUserEvent) throws Throwable {
        Long userId = ThreadLocalUtil.get(OshUserConstants.USER_ID,Long.class);
        String username = ThreadLocalUtil.get(OshUserConstants.USERNAME,String.class);
        String roleCode = ThreadLocalUtil.get(OshUserConstants.ROLE_CODE,String.class);
        List<Long> resourceIds = ThreadLocalUtil.get(OshResourceConstants.RESOURCE_ID,List.class);
        String resourceType = oshUserEvent.resourceType();
        String module = oshUserEvent.module();
        String methodName = joinPoint.getSignature().getName();
        Map<String,Object> userMap = redisCache.getCacheObject(OshUserConstants.LOGIN_USER + userId);
        // 匿名接口（userId 为 null 或 Redis 中无登录信息）：跳过事件记录，直接执行主流程
        if (userId == null || userMap == null) {
            return joinPoint.proceed();
        }
        Map<String,String> asset = (Map<String,String>)userMap.get(OshUserConstants.ASSET);
        Long goldCoin = Long.valueOf(asset.get(OshUserConstants.GOLD_COIN));
        Long points = Long.valueOf(asset.get(OshUserConstants.POINTS));
        String actionType = oshUserEvent.actionType();
        String description = oshUserEvent.description();
        Long id = GenerateUtil.generateSnowflakeId();
        OshUserEvent event = new OshUserEvent(id,userId,username,roleCode,module,methodName,
                actionType, ListUtil.listToString(resourceIds),resourceType,description, null,null,goldCoin,points,
                LocalDateTime.now());
        try {
            event.setStatus(ResultCode.SUCCESS.getMsg());
            return joinPoint.proceed();
        } catch (Throwable e) {
            event.setStatus(ResultCode.FAILED.getMsg());
            event.setException(e.getMessage());
            throw e;
        } finally {
            logger.info("用户行为日志: {}", event);
            // 异步发送，不阻塞主流程
            final OshUserEvent finalEvent = event;
            final String topic = oshUserEvent.topic();
            CompletableFuture.runAsync(() ->
                KafkaMessageUtil.sendMessage(topic, JSON.toJSONString(finalEvent))
            ).exceptionally(ex -> {
                logger.error("异步发送用户行为事件失败, topic: {}, 原因: {}", topic, ex.getMessage());
                return null;
            });
        }
    }
}
