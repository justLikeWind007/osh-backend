package com.backstage.system.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "search.es")
public class SearchEsProperties {

    private boolean enabled = true;

    private long fallbackTimeoutMillis = 800L;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getFallbackTimeoutMillis() {
        return fallbackTimeoutMillis;
    }

    public void setFallbackTimeoutMillis(long fallbackTimeoutMillis) {
        this.fallbackTimeoutMillis = fallbackTimeoutMillis;
    }
}
