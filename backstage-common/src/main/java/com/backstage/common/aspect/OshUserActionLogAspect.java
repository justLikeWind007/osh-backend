package com.backstage.common.aspect;

import com.backstage.common.annotation.OshUserActionLog;
import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.core.domain.OshUserActionEvent;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.common.utils.kafka.KafkaMessageUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/12
 * Time: 15:54
 */
@Aspect
@Component
public class OshUserActionLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(OshUserActionLogAspect.class);

    @Around("@annotation(oshUserActionLog)")
    public Object logUserAction(ProceedingJoinPoint joinPoint, OshUserActionLog oshUserActionLog) throws Throwable {
        String module = oshUserActionLog.module();
        String methodName = joinPoint.getSignature().getName();
        Map<String, Object> methodArgs = null;
        if (oshUserActionLog.recordArgs()) {
            Object[] args = joinPoint.getArgs();
            methodArgs = Arrays.stream(args)
                    .collect(Collectors.toMap(Object::toString, Object::toString));
        }
        String actionType = oshUserActionLog.actionType();
        String description = oshUserActionLog.description();
        OshUserActionEvent event = new OshUserActionEvent(ThreadLocalUtil.get(OshUserConstants.USER_ID,Long.class),module,methodName,
                methodArgs,actionType,description, null,null,null,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        try {
            Object result = joinPoint.proceed();
            event.setMethodResult(result != null ? result.toString() : null);
            event.setStatus("SUCCESS");
            return result;
        } catch (Throwable e) {
            event.setStatus("FAILURE");
            event.setException(e.getMessage());
            throw e;
        } finally {
            logger.info("用户行为日志: {}", event);
            KafkaMessageUtil.sendMessage(oshUserActionLog.topic(), String.valueOf(event));
        }
    }
}
