package com.backstage.common.core.domain;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/12
 * Time: 16:45
 */
public class OshUserActionEvent {
    private Long userId;
    private String module;
    private String methodName;
    private Map<String, Object> methodArgs;
    private String actionType;
    private String description;
    private Object methodResult;
    private String status;
    private String exception;
    private String happenTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Map<String, Object> getMethodArgs() {
        return methodArgs;
    }

    public void setMethodArgs(Map<String, Object> methodArgs) {
        this.methodArgs = methodArgs;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getMethodResult() {
        return methodResult;
    }

    public void setMethodResult(Object methodResult) {
        this.methodResult = methodResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getHappenTime() {
        return happenTime;
    }

    public void setHappenTime(String happenTime) {
        this.happenTime = happenTime;
    }

    @Override
    public String toString() {
        return "OshUserActionEvent{" +
                "userId=" + userId +
                ", module='" + module + '\'' +
                ", methodName='" + methodName + '\'' +
                ", methodArgs=" + methodArgs +
                ", actionType='" + actionType + '\'' +
                ", description='" + description + '\'' +
                ", methodResult=" + methodResult +
                ", status='" + status + '\'' +
                ", exception='" + exception + '\'' +
                ", happenTime='" + happenTime + '\'' +
                '}';
    }

    public OshUserActionEvent() {
    }

    public OshUserActionEvent(Long userId, String module, String methodName, Map<String,
            Object> methodArgs, String actionType, String description, Object methodResult,
            String status, String exception, String happenTime) {
        this.userId = userId;
        this.module = module;
        this.methodName = methodName;
        this.methodArgs = methodArgs;
        this.actionType = actionType;
        this.description = description;
        this.methodResult = methodResult;
        this.status = status;
        this.exception = exception;
        this.happenTime = happenTime;
    }
}
