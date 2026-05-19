package com.backstage.system.service.site.impl;

import com.backstage.system.domain.site.OshSiteInfo;

import java.util.Map;

/**
 * Parsed demo site configuration, extracted from the site_config JSON field.
 * Centralizes all config field extraction and SSH auth resolution logic.
 */
public class DemoSiteConfig {

    private final Long siteId;
    private final String siteName;

    // SSH connection
    private final String backendHost;
    private final int backendPort;
    private final String backendUser;
    private final String backendPassword;
    private final String privateKey;
    private final String loginMethod;
    private final RemoteShellExecutor.AuthMethod authMethod;
    private final String sshCredential;

    // Scripts
    private final String startupScript;
    private final String healthCheckScript;
    private final String stopScript;

    // Frontend info
    private final String frontendUrl;
    private final String loginUsername;
    private final String loginPassword;

    // Raw config map (for direct access if needed)
    private final Map<String, Object> rawConfig;

    private DemoSiteConfig(OshSiteInfo siteInfo, Map<String, Object> config) {
        this.siteId = siteInfo.getId();
        this.siteName = siteInfo.getSiteName();
        this.rawConfig = config;

        this.backendHost = (String) config.get("backendHost");
        this.backendPort = config.containsKey("backendPort")
                ? ((Number) config.get("backendPort")).intValue() : 22;
        this.backendUser = (String) config.get("backendUser");
        this.backendPassword = (String) config.get("backendPassword");
        this.startupScript = (String) config.get("startupScript");
        this.healthCheckScript = (String) config.get("healthCheckScript");
        this.stopScript = (String) config.get("stopScript");
        this.frontendUrl = (String) config.get("frontendUrl");
        this.loginUsername = (String) config.get("loginUsername");
        this.loginPassword = (String) config.get("loginPassword");

        this.loginMethod = (String) config.getOrDefault("loginMethod", "password");
        this.privateKey = (String) config.get("privateKey");
        this.authMethod = "privateKey".equals(loginMethod)
                ? RemoteShellExecutor.AuthMethod.PRIVATE_KEY
                : RemoteShellExecutor.AuthMethod.PASSWORD;
        this.sshCredential = authMethod == RemoteShellExecutor.AuthMethod.PRIVATE_KEY
                ? privateKey : backendPassword;
    }

    /**
     * Factory method: parse config from OshSiteInfo entity.
     * Does NOT validate — validation is done separately in the service.
     */
    public static DemoSiteConfig from(OshSiteInfo siteInfo) {
        return new DemoSiteConfig(siteInfo, siteInfo.getSiteConfig());
    }

    // ---- getters ----

    public Long getSiteId() { return siteId; }
    public String getSiteName() { return siteName; }
    public String getBackendHost() { return backendHost; }
    public int getBackendPort() { return backendPort; }
    public String getBackendUser() { return backendUser; }
    public String getBackendPassword() { return backendPassword; }
    public String getPrivateKey() { return privateKey; }
    public String getLoginMethod() { return loginMethod; }
    public RemoteShellExecutor.AuthMethod getAuthMethod() { return authMethod; }
    public String getSshCredential() { return sshCredential; }
    public String getStartupScript() { return startupScript; }
    public String getHealthCheckScript() { return healthCheckScript; }
    public String getStopScript() { return stopScript; }
    public String getFrontendUrl() { return frontendUrl; }
    public String getLoginUsername() { return loginUsername; }
    public String getLoginPassword() { return loginPassword; }
    public Map<String, Object> getRawConfig() { return rawConfig; }
}
