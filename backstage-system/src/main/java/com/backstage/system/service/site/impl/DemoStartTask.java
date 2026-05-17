package com.backstage.system.service.site.impl;

import java.util.LinkedHashMap;
import java.util.Map;

public class DemoStartTask {
    final Long siteId;
    final String frontendUrl;
    final String loginUsername;
    final String loginPassword;

    volatile DemoStartStatus status = DemoStartStatus.SUBMITTED;
    volatile String pid = "";
    volatile String message = "启动任务已创建";
    volatile int checkCount = 0;
    volatile String healthCheckOutput = "";

    DemoStartTask(Long siteId, String frontendUrl, String loginUsername, String loginPassword) {
        this.siteId = siteId;
        this.frontendUrl = frontendUrl;
        this.loginUsername = loginUsername;
        this.loginPassword = loginPassword;
    }

    boolean isInProgress() {
        return status == DemoStartStatus.SUBMITTED || status == DemoStartStatus.STARTING;
    }

    synchronized void updateStarting(String pid) {
        this.status = DemoStartStatus.STARTING;
        this.pid = pid;
        this.message = "启动脚本已提交（PID: " + pid + "），正在检查服务状态...";
    }

    synchronized void updateCheck(int count, String output) {
        this.checkCount = count;
        this.healthCheckOutput = output;
        this.message = "第 " + count + " 次检查…";
    }

    synchronized void updateRunning(String output) {
        this.status = DemoStartStatus.RUNNING;
        this.healthCheckOutput = output;
        this.message = "服务已成功启动";
    }

    synchronized void updateTimeout(int maxRetries, int intervalSeconds) {
        this.status = DemoStartStatus.TIMEOUT;
        this.message = "服务启动超时（已等待 " + (maxRetries * intervalSeconds) + " 秒），请手动检查";
    }

    synchronized void updateFailed(String reason) {
        this.status = DemoStartStatus.FAILED;
        this.message = reason;
    }

    synchronized Map<String, Object> toResultMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("siteId", siteId);
        map.put("status", status.name());
        map.put("statusName", status.getName());
        map.put("pid", pid);
        map.put("message", message);
        map.put("checkCount", checkCount);
        map.put("healthCheckOutput", healthCheckOutput);

        if (status == DemoStartStatus.RUNNING) {
            map.put("frontendUrl", frontendUrl);
            map.put("loginUsername", loginUsername);
            map.put("loginPassword", loginPassword);
        }
        return map;
    }
}