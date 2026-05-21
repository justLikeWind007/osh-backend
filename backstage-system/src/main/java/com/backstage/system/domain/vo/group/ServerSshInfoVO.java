package com.backstage.system.domain.vo.group;

import java.time.LocalDateTime;

/**
 * 服务器SSH连接信息VO
 * 
 * @author system
 * @date 2026-05-14
 */
public class ServerSshInfoVO {
    
    /** 服务器IP地址 */
    private String ip;
    
    /** SSH端口 */
    private Integer port;
    
    /** 用户名 */
    private String username;
    
    /** 密码（加密后显示） */
    private String password;
    
    /** 连接协议 */
    private String protocol;
    
    /** 服务器状态：running-运行中 stopped-已停止 expired-已过期 */
    private String status;
    
    /** 到期时间 */
    private String expireTime;
    
    /** 连接指南 */
    private String connectGuide;
    
    /** 备注信息 */
    private String remark;
    
    public ServerSshInfoVO() {
    }
    
    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public Integer getPort() {
        return port;
    }
    
    public void setPort(Integer port) {
        this.port = port;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getProtocol() {
        return protocol;
    }
    
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }
    
    public String getConnectGuide() {
        return connectGuide;
    }
    
    public void setConnectGuide(String connectGuide) {
        this.connectGuide = connectGuide;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
}