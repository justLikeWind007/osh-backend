package com.bachstage.course.job_stream_from_kafka_to_es.config;

/**
 * 课程索引同步任务配置
 */
public class CourseIndexJobConfig
{
    private final String kafkaBootstrapServers;
    private final String kafkaGroupId;
    private final String createTopic;
    private final String esHosts;
    private final String esIndex;

    private CourseIndexJobConfig(String kafkaBootstrapServers, String kafkaGroupId, String createTopic,
                                 String esHosts, String esIndex)
    {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
        this.kafkaGroupId = kafkaGroupId;
        this.createTopic = createTopic;
        this.esHosts = esHosts;
        this.esIndex = esIndex;
    }

    public static CourseIndexJobConfig fromSystem()
    {
        return new CourseIndexJobConfig(
                ApplicationPropertiesConfig.read("kafka.bootstrap-servers", "KAFKA_BOOTSTRAP_SERVERS", "43.242.200.25:9092"),
                ApplicationPropertiesConfig.read("kafka.group-id", "KAFKA_GROUP_ID", "backstage-course-index-flink"),
                ApplicationPropertiesConfig.read("course.index.create-topic", "COURSE_INDEX_CREATE_TOPIC", "osh.course.index.create"),
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

    public String getCreateTopic()
    {
        return createTopic;
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
