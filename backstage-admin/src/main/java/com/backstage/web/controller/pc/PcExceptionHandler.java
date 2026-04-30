package com.backstage.web.controller.pc;

import com.backstage.common.core.domain.R;
import com.backstage.common.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * PC端异常处理器
 *
 * @author backstage
 */
@RestControllerAdvice(basePackages = "com.backstage.web.controller.pc")
@Order(1)
public class PcExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(PcExceptionHandler.class);

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public R<Void> handleServiceException(ServiceException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生业务异常'{}'", requestURI, e.getMessage());
        return R.fail(e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常", requestURI, e);
        return R.fail("系统错误，请联系管理员");
    }
}
