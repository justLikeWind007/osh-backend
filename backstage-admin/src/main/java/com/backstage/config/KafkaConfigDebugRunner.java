package com.backstage.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 启动时输出 Kafka 相关配置的实际来源，便于排查配置覆盖问题。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class KafkaConfigDebugRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(KafkaConfigDebugRunner.class);

    private final ConfigurableEnvironment environment;

    public KafkaConfigDebugRunner(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        String systemGroupId = System.getProperty("spring.kafka.consumer.group-id");
        String envGroupId = environment.getProperty("spring.kafka.consumer.group-id");
        String bootstrapServers = environment.getProperty("spring.kafka.bootstrap-servers");
        String autoOffsetReset = environment.getProperty("spring.kafka.consumer.auto-offset-reset");

        log.info("[KafkaConfigDebug] System.getProperty spring.kafka.consumer.group-id={}", systemGroupId);
        log.info("[KafkaConfigDebug] Environment spring.kafka.consumer.group-id={}", envGroupId);
        log.info("[KafkaConfigDebug] Environment spring.kafka.bootstrap-servers={}", bootstrapServers);
        log.info("[KafkaConfigDebug] Environment spring.kafka.consumer.auto-offset-reset={}", autoOffsetReset);

        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            Object groupIdValue = propertySource.getProperty("spring.kafka.consumer.group-id");
            if (groupIdValue != null) {
                log.info("[KafkaConfigDebug] PropertySource {} -> spring.kafka.consumer.group-id={}",
                        propertySource.getName(), groupIdValue);
            }
        }
    }
}
