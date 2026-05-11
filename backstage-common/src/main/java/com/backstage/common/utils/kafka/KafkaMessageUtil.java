package com.backstage.common.utils.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
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

    private static KafkaTemplate<String, String> kafkaTemplate;

    public KafkaMessageUtil(KafkaTemplate<String, String> kafkaTemplate) {
        KafkaMessageUtil.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 向Kafka发送消息（异步，不阻塞主流程）
     * 发送结果通过回调记录日志，发送失败不影响调用方
     *
     * @param topic 主题名称
     * @param value 消息内容
     */
    public static void sendMessage(String topic, String value) {
        kafkaTemplate.send(topic, value).addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                logger.info("成功发送消息到topic: {}, offset: {}, 消息内容: {}",
                        topic, result.getRecordMetadata().offset(), value);
            }
            @Override
            public void onFailure(Throwable ex) {
                logger.error("发送消息失败, topic: {}, 消息内容: {}, 错误信息: {}", topic, value, ex.getMessage(), ex);
            }
        });
    }

    public static void sendMessage(String topic, String key, String value) {
        kafkaTemplate.send(topic, key, value).addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                logger.info("成功发送消息到topic: {}, key: {}, offset: {}, 消息内容: {}",
                        topic, key, result.getRecordMetadata().offset(), value);
            }

            @Override
            public void onFailure(Throwable ex) {
                logger.error("发送消息失败, topic: {}, key: {}, 消息内容: {}, 错误信息: {}", topic, key, value, ex.getMessage(), ex);
            }
        });
    }
}
