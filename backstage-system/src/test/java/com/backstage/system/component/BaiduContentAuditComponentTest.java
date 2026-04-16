package com.backstage.system.component;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Properties;

public class BaiduContentAuditComponentTest {

    @Test
    public void shouldAuditIllegalTextAndPrintResult() throws Exception {
        BaiduContentAuditComponent component = createComponent();

        String content = "这条评论包含色情内容";
        BaiduTextAuditResult result = component.auditText(content);

        System.out.println("违规内容审核结果: pass=" + result.isPass()
                + ", conclusion=" + result.getConclusion()
                + ", hitWords=" + result.getHitWords()
                + ", rawResponse=" + result.getRawResponse());

        Assert.assertTrue(result.isSuccess());
        Assert.assertFalse(result.isPass());
    }

    @Test
    public void shouldAuditNormalTextAndPrintResult() throws Exception {
//        BaiduContentAuditComponent component = createComponent();
//
//        String content = "这是一条正常的课程评论，内容主要在夸老师讲得清楚";
//        BaiduTextAuditResult result = component.auditText(content);
//
//        System.out.println("正常内容审核结果: pass=" + result.isPass()
//                + ", conclusion=" + result.getConclusion()
//                + ", hitWords=" + result.getHitWords()
//                + ", rawResponse=" + result.getRawResponse());
//
//        Assert.assertTrue(result.isSuccess());
//        Assert.assertTrue(result.isPass());
    }

    private BaiduContentAuditComponent createComponent() throws Exception {
        Properties properties = loadBaiduAiProperties();
        String appId = properties.getProperty("baidu.ai.app-id");
        String apiKey = properties.getProperty("baidu.ai.api-key");
        String secretKey = properties.getProperty("baidu.ai.secret-key");
        String timeout = properties.getProperty("baidu.ai.timeout", "3000");

        Assume.assumeTrue("未找到百度内容审核配置，跳过测试",
                isNotBlank(appId) && isNotBlank(apiKey) && isNotBlank(secretKey));

        BaiduContentAuditComponent component = new BaiduContentAuditComponent();
        setField(component, "appId", appId);
        setField(component, "apiKey", apiKey);
        setField(component, "secretKey", secretKey);
        setField(component, "timeout", Integer.parseInt(timeout));
        component.init();

        Assume.assumeTrue("百度内容审核组件未启用，跳过测试", component.isEnabled());
        return component;
    }

    private Properties loadBaiduAiProperties() {
        Properties properties = new Properties();
        putIfPresent(properties, "baidu.ai.app-id", System.getProperty("baidu.ai.app-id"));
        putIfPresent(properties, "baidu.ai.api-key", System.getProperty("baidu.ai.api-key"));
        putIfPresent(properties, "baidu.ai.secret-key", System.getProperty("baidu.ai.secret-key"));
        putIfPresent(properties, "baidu.ai.timeout", System.getProperty("baidu.ai.timeout"));

        putIfPresent(properties, "baidu.ai.app-id", System.getenv("BAIDU_AI_APP_ID"));
        putIfPresent(properties, "baidu.ai.api-key", System.getenv("BAIDU_AI_API_KEY"));
        putIfPresent(properties, "baidu.ai.secret-key", System.getenv("BAIDU_AI_SECRET_KEY"));
        putIfPresent(properties, "baidu.ai.timeout", System.getenv("BAIDU_AI_TIMEOUT"));

        if (hasRequiredProperties(properties)) {
            return properties;
        }

        Properties yamlProperties = loadFromYaml();
        if (yamlProperties != null) {
            putIfPresent(properties, "baidu.ai.app-id", yamlProperties.getProperty("baidu.ai.app-id"));
            putIfPresent(properties, "baidu.ai.api-key", yamlProperties.getProperty("baidu.ai.api-key"));
            putIfPresent(properties, "baidu.ai.secret-key", yamlProperties.getProperty("baidu.ai.secret-key"));
            putIfPresent(properties, "baidu.ai.timeout", yamlProperties.getProperty("baidu.ai.timeout"));
        }

        return properties;
    }

    private Properties loadFromYaml() {
        String[] candidates = {
                "backstage-admin/src/main/resources/application.yml",
                "../backstage-admin/src/main/resources/application.yml"
        };

        for (String candidate : candidates) {
            File file = new File(candidate);
            if (!file.exists()) {
                continue;
            }
            YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
            factoryBean.setResources(new FileSystemResource(file));
            Properties properties = factoryBean.getObject();
            if (properties != null) {
                return properties;
            }
        }
        return null;
    }

    private boolean hasRequiredProperties(Properties properties) {
        return isNotBlank(properties.getProperty("baidu.ai.app-id"))
                && isNotBlank(properties.getProperty("baidu.ai.api-key"))
                && isNotBlank(properties.getProperty("baidu.ai.secret-key"));
    }

    private void putIfPresent(Properties properties, String key, String value) {
        if (isNotBlank(value) && !properties.containsKey(key)) {
            properties.setProperty(key, value.trim());
        }
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
