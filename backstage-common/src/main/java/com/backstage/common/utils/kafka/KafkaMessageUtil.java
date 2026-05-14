package com.backstage.common.utils.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.TimeUnit;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/12
 * Time: 16:32
 */
@Component
public class KafkaMessageUtil {

    private static final Logger logger = LoggerFactory.getLogger(KafkaMessageUtil.class);
    private static final long SEND_TIMEOUT_SECONDS = 10L;

    private static KafkaTemplate<String, String> kafkaTemplate;

    public KafkaMessageUtil(KafkaTemplate<String, String> kafkaTemplate) {
        KafkaMessageUtil.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 向Kafka发送消息
     *
     * @param topic 主题名称
     * @param value 消息内容
     */
    public static void sendMessage(String topic, String value) {
        try {
            kafkaTemplate.send(topic, value);
            logger.info("成功发送消息到topic: {}, 消息内容: {}", topic, value);
        } catch (Exception e) {
            logger.error("发送消息失败, topic: {}, 消息内容: {}, 错误信息: {}", topic, value, e.getMessage(), e);
            throw new RuntimeException("发送Kafka消息失败", e);
        }
    }

    public static void sendMessage(String topic, String key, String value) {
        try {
            kafkaTemplate.send(topic, key, value);
            logger.info("成功发送消息到topic: {}, key: {}, 消息内容: {}", topic, key, value);
        } catch (Exception e) {
            logger.error("发送消息失败, topic: {}, key: {}, 消息内容: {}, 错误信息: {}", topic, key, value, e.getMessage(), e);
            throw new RuntimeException("发送Kafka消息失败", e);
        }
    }

    public static void sendMessageSync(String topic, String key, String value) {
        try {
            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, value);
            SendResult<String, String> result = future.get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            logger.info("成功发送消息到topic: {}, key: {}, partition: {}, offset: {}, 消息内容: {}",
                    topic, key, result.getRecordMetadata().partition(), result.getRecordMetadata().offset(), value);
        } catch (Exception e) {
            logger.error("发送消息失败, topic: {}, key: {}, 消息内容: {}, 错误信息: {}", topic, key, value, e.getMessage(), e);
            throw new RuntimeException("发送Kafka消息失败", e);
        }
    }
}
