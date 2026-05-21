package com.backstage.common.secret;

import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * osh-secret Bootstrap 配置
 *
 * 通过 Spring Cloud Bootstrap 机制注册为 PropertySourceLocator，
 * 这样返回的 PropertySource 会被包装为 bootstrapProperties，
 * 优先级高于 Nacos 配置。
 */
@Configuration
public class OshSecretBootstrapConfiguration {

    @Bean
    public PropertySourceLocator oshSecretPropertySourceLocator() {
        return new OshSecretPropertySourceLocator();
    }

    public static class OshSecretPropertySourceLocator implements PropertySourceLocator {

        @Override
        public PropertySource<?> locate(Environment environment) {
            boolean enabled = Boolean.parseBoolean(environment.getProperty("osh.secret.enabled", "false"));
            if (!enabled) {
                return new MapPropertySource("osh-secret", new HashMap<>());
            }

            String serverUrl = environment.getProperty("osh.secret.server-url");
            String accessToken = environment.getProperty("osh.secret.access-token");
            String appId = environment.getProperty("osh.secret.app-id", "");
            String env = environment.getProperty("osh.secret.env",
                    environment.getProperty("spring.profiles.active", "dev"));

            if (serverUrl == null || serverUrl.isEmpty() || accessToken == null || accessToken.isEmpty()) {
                System.out.println("[osh-secret] 配置不完整，跳过密钥加载");
                return new MapPropertySource("osh-secret", new HashMap<>());
            }

            if (appId.isEmpty()) {
                System.err.println("[osh-secret] osh.secret.app-id 未配置，跳过");
                return new MapPropertySource("osh-secret", new HashMap<>());
            }

            System.out.println("[osh-secret] PropertySourceLocator: 从 " + serverUrl + " 拉取 appId=" + appId + ", env=" + env + " 密钥...");

            OshSecretClient client = new OshSecretClient(serverUrl, accessToken);
            Map<String, String> secrets = client.fetchAll(appId, env);

            if (secrets.isEmpty()) {
                System.err.println("[osh-secret] 未获取到任何密钥");
                return new MapPropertySource("osh-secret", new HashMap<>());
            }

            System.out.println("[osh-secret] 成功加载 " + secrets.size() + " 个密钥");
            Map<String, Object> properties = new HashMap<>(secrets);
            return new MapPropertySource("osh-secret", properties);
        }
    }
}
