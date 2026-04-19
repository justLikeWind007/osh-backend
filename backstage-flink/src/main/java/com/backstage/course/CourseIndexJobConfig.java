package com.backstage.course;

public class CourseIndexJobConfig
{
    private final String kafkaBootstrapServers;
    private final String kafkaGroupId;
    private final String createTopic;
    private final String updateTopic;
    private final String kafkaStartMode;
    private final String esHosts;
    private final String esIndex;

    private CourseIndexJobConfig(String kafkaBootstrapServers, String kafkaGroupId, String createTopic,
            String updateTopic, String kafkaStartMode, String esHosts, String esIndex)
    {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
        this.kafkaGroupId = kafkaGroupId;
        this.createTopic = createTopic;
        this.updateTopic = updateTopic;
        this.kafkaStartMode = kafkaStartMode;
        this.esHosts = esHosts;
        this.esIndex = esIndex;
    }

    public static CourseIndexJobConfig fromSystem()
    {
        return new CourseIndexJobConfig(
                read("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"),
                read("KAFKA_GROUP_ID", "backstage-course-index-flink"),
                read("COURSE_INDEX_CREATE_TOPIC", "osh.course.index.create"),
                read("COURSE_INDEX_UPDATE_TOPIC", "osh.course.index.update"),
                read("KAFKA_START_MODE", "earliest"),
                read("ES_HOSTS", "http://localhost:9200"),
                read("ES_INDEX", "osh_course_search_read"));
    }

    private static String read(String key, String defaultValue)
    {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.trim().isEmpty())
        {
            return systemValue.trim();
        }
        String envValue = System.getenv(key);
        if (envValue != null && !envValue.trim().isEmpty())
        {
            return envValue.trim();
        }
        return defaultValue;
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

    public String getUpdateTopic()
    {
        return updateTopic;
    }

    public String getEsHosts()
    {
        return esHosts;
    }

    public String getKafkaStartMode()
    {
        return kafkaStartMode;
    }

    public String getEsIndex()
    {
        return esIndex;
    }
}
