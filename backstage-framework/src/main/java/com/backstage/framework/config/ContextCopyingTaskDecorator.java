package com.backstage.framework.config;

import com.backstage.common.threadlocal.ThreadLocalUtil;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 线程池任务上下文透传，避免异步线程丢失用户上下文和日志链路。
 */
@Component
public class ContextCopyingTaskDecorator implements TaskDecorator
{
    @Override
    public Runnable decorate(Runnable runnable)
    {
        Map<String, Object> callerThreadLocalMap = new HashMap<>(ThreadLocalUtil.getLocalMap());
        SecurityContext callerSecurityContext = SecurityContextHolder.getContext();
        Map<String, String> callerMdcContext = MDC.getCopyOfContextMap();

        return () -> {
            Map<String, Object> executorThreadLocalMap = new HashMap<>(ThreadLocalUtil.getLocalMap());
            SecurityContext executorSecurityContext = SecurityContextHolder.getContext();
            Map<String, String> executorMdcContext = MDC.getCopyOfContextMap();
            try
            {
                ThreadLocalUtil.remove();
                for (Map.Entry<String, Object> entry : callerThreadLocalMap.entrySet())
                {
                    ThreadLocalUtil.set(entry.getKey(), entry.getValue());
                }

                SecurityContext contextToUse = SecurityContextHolder.createEmptyContext();
                contextToUse.setAuthentication(callerSecurityContext.getAuthentication());
                SecurityContextHolder.setContext(contextToUse);

                if (callerMdcContext == null || callerMdcContext.isEmpty())
                {
                    MDC.clear();
                }
                else
                {
                    MDC.setContextMap(callerMdcContext);
                }
                runnable.run();
            }
            finally
            {
                ThreadLocalUtil.remove();
                for (Map.Entry<String, Object> entry : executorThreadLocalMap.entrySet())
                {
                    ThreadLocalUtil.set(entry.getKey(), entry.getValue());
                }

                SecurityContext restoredContext = SecurityContextHolder.createEmptyContext();
                restoredContext.setAuthentication(executorSecurityContext.getAuthentication());
                SecurityContextHolder.setContext(restoredContext);

                if (executorMdcContext == null || executorMdcContext.isEmpty())
                {
                    MDC.clear();
                }
                else
                {
                    MDC.setContextMap(executorMdcContext);
                }
            }
        };
    }
}
