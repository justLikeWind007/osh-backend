package com.backstage.web.controller.pc;

import com.backstage.common.utils.SecurityUtils;

/**
 * 电子书模块基础Controller
 *
 * @author backstage
 */
public abstract class BaseBookController
{
    /**
     * 获取当前用户ID（静默失败）
     *
     * @return 用户ID，获取失败返回null
     */
    protected Long getCurrentUserId()
    {
        try
        {
            return SecurityUtils.getUserId();
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
