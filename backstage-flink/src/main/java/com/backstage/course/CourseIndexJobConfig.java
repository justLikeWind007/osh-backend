package com.backstage.course;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CourseIndexJobConfig
{
    private static final String DEFAULT_CONFIG_FILE = "application.properties";

    private final String kafkaBootstrapServers;
    private final String kafkaGroupId;
    private final String createTopic;
    private final String updateTopic;
    private final String kafkaStartMode;
    private final String esHosts;
    private final String esIndex;
    private final String esUsername;
    private final String esPassword;
    private final int parallelism;
    private final int esBulkFlushMaxActions;
    private final String jobName;

    private CourseIndexJobConfig(String kafkaBootstrapServers, String kafkaGroupId, String createTopic,
            String updateTopic, String kafkaStartMode, String esHosts, String esIndex, String esUsername,
            String esPassword, int parallelism, int esBulkFlushMaxActions, String jobName)
    {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
        this.kafkaGroupId = kafkaGroupId;
        this.createTopic = createTopic;
        this.updateTopic = updateTopic;
        this.kafkaStartMode = kafkaStartMode;
        this.esHosts = esHosts;
        this.esIndex = esIndex;
        this.esUsername = esUsername;
        this.esPassword = esPassword;
        this.parallelism = parallelism;
        this.esBulkFlushMaxActions = esBulkFlushMaxActions;
        this.jobName = jobName;
    }

    public static CourseIndexJobConfig fromSystem()
    {
        return fromProperties(loadProperties());
    }

    public static CourseIndexJobConfig fromProperties(Properties properties)
    {
        return new CourseIndexJobConfig(
                read(properties, "kafka.bootstrap-servers", "KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"),
                read(properties, "kafka.group-id", "KAFKA_GROUP_ID", "backstage-course-index-flink"),
                read(properties, "course.index.create-topic", "COURSE_INDEX_CREATE_TOPIC", "osh.course.index.create"),
                read(properties, "course.index.update-topic", "COURSE_INDEX_UPDATE_TOPIC", "osh.course.index.update"),
                read(properties, "kafka.start-mode", "KAFKA_START_MODE", "earliest"),
                read(properties, "elasticsearch.hosts", "ES_HOSTS", "http://localhost:9200"),
                read(properties, "elasticsearch.index", "ES_INDEX", "osh_course_search_read"),
                read(properties, "elasticsearch.username", "ES_USERNAME", ""),
                read(properties, "elasticsearch.password", "ES_PASSWORD", ""),
                readInt(properties, "flink.parallelism", "FLINK_PARALLELISM", 1),
                readInt(properties, "elasticsearch.bulk-flush-max-actions", "ES_BULK_FLUSH_MAX_ACTIONS", 200),
                read(properties, "flink.job-name", "FLINK_JOB_NAME", "backstage-course-index-job"));
    }

    public static Properties loadProperties()
    {
        Properties properties = new Properties();
        String configLocation = readRaw("flink.config.location", "FLINK_CONFIG_LOCATION");
        if (configLocation != null)
        {
            try (InputStream inputStream = new FileInputStream(configLocation))
            {
                properties.load(inputStream);
                return properties;
            }
            catch (IOException e)
            {
                throw new IllegalStateException("Failed to load flink config file: " + configLocation, e);
            }
        }

        try (InputStream inputStream = CourseIndexJobConfig.class.getClassLoader()
                .getResourceAsStream(DEFAULT_CONFIG_FILE))
        {
            if (inputStream != null)
            {
                properties.load(inputStream);
            }
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Failed to load classpath config: " + DEFAULT_CONFIG_FILE, e);
        }
        return properties;
    }

    private static String read(Properties properties, String propertyKey, String envKey, String defaultValue)
    {
        String systemValue = System.getProperty(propertyKey);
        if (systemValue != null && !systemValue.trim().isEmpty())
        {
            return systemValue.trim();
        }

        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.trim().isEmpty())
        {
            return envValue.trim();
        }

        String propertyValue = properties.getProperty(propertyKey);
        if (propertyValue != null && !propertyValue.trim().isEmpty())
        {
            return propertyValue.trim();
        }

        return defaultValue;
    }

    private static String readRaw(String systemKey, String envKey)
    {
        String systemValue = System.getProperty(systemKey);
        if (systemValue != null && !systemValue.trim().isEmpty())
        {
            return systemValue.trim();
        }

        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.trim().isEmpty())
        {
            return envValue.trim();
        }
        return null;
    }

    private static int readInt(Properties properties, String propertyKey, String envKey, int defaultValue)
    {
        String value = read(properties, propertyKey, envKey, String.valueOf(defaultValue));
        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Invalid integer config for " + propertyKey + ": " + value, e);
        }
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

    public String getEsUsername()
    {
        return esUsername;
    }

    public String getEsPassword()
    {
        return esPassword;
    }

    public int getParallelism()
    {
        return parallelism;
    }

    public int getEsBulkFlushMaxActions()
    {
        return esBulkFlushMaxActions;
    }

    public String getJobName()
    {
        return jobName;
    }
}
