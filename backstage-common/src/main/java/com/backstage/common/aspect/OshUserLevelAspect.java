package com.backstage.common.aspect;

import com.backstage.common.annotation.OshUserLevel;
import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.enums.ResultCode;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/18
 * Time: 14:38
 */
@Aspect
@Component
@Order(3)
public class OshUserLevelAspect {
    private static final Logger logger = LoggerFactory.getLogger(OshUserLevelAspect.class);

    @Before("@annotation(oshUserLevel)")
    public void userLevel(JoinPoint joinPoint, OshUserLevel oshUserLevel) {
        int requiredLevel = oshUserLevel.value();
        Integer currentUserLevel;
        try {
            currentUserLevel = Integer.parseInt(ThreadLocalUtil.get(OshUserConstants.LEVEL, String.class));
        }catch (Exception e) {
            logger.warn("未获取到用户等级信息，拒绝访问: {}", joinPoint.getSignature().getName());
            throw new SecurityException(ResultCode.FAILED_NOT_LOGIN.getMsg());
        }
        if (currentUserLevel < requiredLevel) {
            logger.warn("用户等级不足: 当前等级={}, 所需等级={}, 方法={}",
                    currentUserLevel, requiredLevel, joinPoint.getSignature().getName());
            throw new SecurityException(String.format(ResultCode.FAILED_USER_PERMISSION_DENIED +"，需要等级 %d，当前等级 %d",
                    requiredLevel, currentUserLevel));
        }
        logger.debug("权限校验通过: 用户等级={}, 所需等级={}, 方法={}",
                currentUserLevel, requiredLevel, joinPoint.getSignature().getName());
    }
}
