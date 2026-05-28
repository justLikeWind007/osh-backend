package com.backstage.system.service.tool;

/**
 * 资源编号判重检查器
 */
public interface ResourceNoDuplicateChecker {

    /**
     * 编号是否已存在
     *
     * @param no 资源编号
     * @return true-已存在，false-不存在
     */
    boolean exists(String no);
}
