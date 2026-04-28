package com.backstage.framework.consumer;

import com.backstage.common.constant.KafkaConstants;
import com.backstage.common.core.domain.OshUserEvent;
import com.backstage.system.mapper.user.OshUserEventMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/26
 * Time: 20:17
 */
@Component
public class OshUserEventConsumer {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OshUserEventMapper oshUserEventMapper;

    private static final Logger logger = LoggerFactory.getLogger(OshUserEventConsumer.class);
    @KafkaListener(topics = KafkaConstants.USER_ACTION_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserEvent(String message) {
        try {
            logger.info("【Kafka消费者】收到用户行为事件：{}", message);

            OshUserEvent event = objectMapper.readValue(message, OshUserEvent.class);
            oshUserEventMapper.insert(event);
            logger.info("event落库:{}", event.toString());

        } catch (Exception e) {
            logger.error("【Kafka消费者】消息处理异常：", e);
        }
    }
}
