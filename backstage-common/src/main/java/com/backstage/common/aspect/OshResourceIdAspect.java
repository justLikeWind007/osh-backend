package com.backstage.common.aspect;

import com.backstage.common.annotation.OshResourceId;
import com.backstage.common.constant.OshResourceConstants;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/27
 * Time: 20:04
 */
@Aspect
@Component
@Order(1)
public class OshResourceIdAspect {
    private static final Logger logger = LoggerFactory.getLogger(OshResourceIdAspect.class);

    @Before("execution(* *(..)) && @within(org.springframework.web.bind.annotation.RestController)")
    public void getResourceNo(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null) {
            for (Object arg : args) {
                if (arg != null) {
                    Field[] fields = arg.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if (field.isAnnotationPresent(OshResourceId.class)) {
                            field.setAccessible(true);
                            try {
                                Object value = field.get(arg);
                                if (value != null) {
                                    List<Long> resourceIds = convertToLongList(value);
                                    ThreadLocalUtil.set(OshResourceConstants.RESOURCE_ID, resourceIds);
                                    return;
                                }
                            } catch (IllegalAccessException e) {
                                logger.error("Failed to get field value", e);
                            }
                        }
                    }
                }
            }
        }
    }

    private List<Long> convertToLongList(Object value) {
        List<Long> result = new ArrayList<>();
        if (value instanceof Long) {
            result.add((Long) value);
        } else if (value instanceof List) {
            for (Object item : (List<?>) value) {
                result.add((Long) item);
            }
        }
        return result;
    }
}
