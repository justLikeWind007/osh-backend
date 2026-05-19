package com.bachstage.course.job_stream_from_kafka_to_es.config;

/**
 * 课程索引同步任务配置
 */
public class CourseIndexJobConfig
{
    private final String kafkaBootstrapServers;
    private final String kafkaGroupId;
    private final String topic;
    private final String esHosts;
    private final String esIndex;

    private CourseIndexJobConfig(String kafkaBootstrapServers, String kafkaGroupId, String topic,
                                 String esHosts, String esIndex)
    {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
        this.kafkaGroupId = kafkaGroupId;
        this.topic = topic;
        this.esHosts = esHosts;
        this.esIndex = esIndex;
    }

    public static CourseIndexJobConfig fromSystem()
    {
        return new CourseIndexJobConfig(
                ApplicationPropertiesConfig.read("kafka.bootstrap-servers", "KAFKA_BOOTSTRAP_SERVERS", "43.242.200.25:9092"),
                ApplicationPropertiesConfig.read("kafka.group-id", "KAFKA_GROUP_ID", "backstage-course-index-flink"),
                ApplicationPropertiesConfig.read("course.index.topic", "COURSE_INDEX_TOPIC", "osh.course.index"),
                ApplicationPropertiesConfig.read("elasticsearch.hosts", "ES_HOSTS", "http://43.242.200.25:9200"),
                ApplicationPropertiesConfig.read("elasticsearch.index", "ES_INDEX", "osh_course_search_read"));
    }

    public String getKafkaBootstrapServers()
    {
        return kafkaBootstrapServers;
    }

    public String getKafkaGroupId()
    {
        return kafkaGroupId;
    }

    public String getTopic()
    {
        return topic;
    }

    public String getEsHosts()
    {
        return esHosts;
    }

    public String getEsIndex()
    {
        return esIndex;
    }
}
