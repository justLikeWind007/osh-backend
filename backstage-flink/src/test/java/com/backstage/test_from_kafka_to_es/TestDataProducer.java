package com.backstage.test_from_kafka_to_es;

import com.alibaba.fastjson2.JSON;
import com.backstage.test_from_kafka_to_es.course.CourseIndexMessage;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

/**
 * 测试数据生产者 - 发送 100 条课程数据到 Kafka
 * 
 * 运行方式：
 * 1. 在 IDEA 中右键运行此类的 main 方法
 * 2. 或使用命令：mvn exec:java -Dexec.mainClass="com.backstage.test_from_kafka_to_es.TestDataProducer"
 */
public class TestDataProducer {
    
    public static void main(String[] args) {
        String kafkaServers = "43.242.200.25:9092";
        String topic = "osh.course.index.create";
        
        // 配置 Kafka Producer
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        
        try {
            System.out.println("========================================");
            System.out.println("开始发送 100 条课程数据到 Kafka");
            System.out.println("Topic: " + topic);
            System.out.println("Kafka: " + kafkaServers);
            System.out.println("========================================\n");
            
            int successCount = 0;
            int failCount = 0;
            
            for (int i = 1; i <= 100; i++) {
                CourseIndexMessage message = createTestCourse(i);
                
                String jsonMessage = JSON.toJSONString(message);
                ProducerRecord<String, String> record = new ProducerRecord<>(
                    topic, 
                    String.valueOf(message.getCourseId()), 
                    jsonMessage
                );
                
                // 同步发送，方便调试
                try {
                    producer.send(record).get();
                    successCount++;
                    
                    String statusText = message.getStatus() == 1 ? "✓ 已发布(会被索引)" : "✗ 未发布(会被过滤)";
                    System.out.printf("[%3d/100] 发送成功 - ID: %d, 标题: %s, 状态: %s\n", 
                        i, message.getCourseId(), message.getTitle(), statusText);
                    
                } catch (Exception e) {
                    failCount++;
                    System.err.printf("[%3d/100] 发送失败 - ID: %d, 错误: %s\n", 
                        i, message.getCourseId(), e.getMessage());
                }
                
                // 每 20 条暂停一下
                if (i % 20 == 0) {
                    System.out.println();
                    Thread.sleep(500);
                }
            }
            
            producer.flush();
            
            System.out.println("\n========================================");
            System.out.println("发送完成！");
            System.out.println("成功: " + successCount + " 条");
            System.out.println("失败: " + failCount + " 条");
            System.out.println("预计被 Flink 过滤后写入 ES 的数量: ~70 条（status=1 的课程）");
            System.out.println("========================================");
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
    
    /**
     * 创建测试课程数据
     * 规则：
     * - 70% 的课程状态为 1（已发布，会被索引）
     * - 30% 的课程状态为 0（未发布，会被过滤）
     */
    private static CourseIndexMessage createTestCourse(int index) {
        CourseIndexMessage message = new CourseIndexMessage();
        
        message.setCourseId((long) index);
        message.setTitle("测试课程-" + index);
        message.setIntro("这是第 " + index + " 个测试课程的简介");
        message.setServiceContent("课程服务内容");
        message.setCover("/images/course-" + index + ".jpg");
        message.setPrice(new BigDecimal(99 + index));
        message.setTPrice(new BigDecimal(199 + index));
        message.setType("video");
        message.setSubCount(0);
        message.setRemark("测试备注");
        message.setTotalDuration(3600 + index * 10);
        message.setFreeLessonCount(2);
        message.setVideoCount(10 + index % 5);
        message.setSalesCount(index * 10);
        message.setViewCount((long) (index * 100));
        message.setLikeCount(index * 5);
        message.setCommentCount(index * 2);
        message.setQuestionCount(index);
        message.setCollectionCount(index * 3);
        message.setRatingScore(new BigDecimal("4.5"));
        message.setFreeType(0);
        message.setAfterServiceDays(365);
        message.setResourceType(1);
        message.setLevel(index % 3 + 1);
        
        // 70% 的课程状态为 1（已发布），30% 为 0（未发布）
        message.setStatus(index % 10 < 7 ? 1 : 0);
        
        message.setExamId(0);
        message.setDeleteFlag(0);
        message.setTagNames(Arrays.asList("Java", "后端", "Flink"));
        message.setTagNamesText("Java,后端,Flink");
        message.setSearchText(message.getTitle() + " " + message.getIntro());
        
        // 使用时间戳而不是格式化的日期字符串，避免 ES 日期解析错误
        Date now = new Date();
        message.setCreateTime(now);
        message.setUpdateTime(now);
        message.setOperator("system");
        
        return message;
    }
}
