package com.backstage.system.service.site.impl;    // ---- 内部类型 ----

public enum DemoStartStatus {
    SUBMITTED("已提交"),
    STARTING("启动中"),
    RUNNING("启动成功"),
    FAILED("启动失败"),
    TIMEOUT("启动超时");

    private final String name;

    DemoStartStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}