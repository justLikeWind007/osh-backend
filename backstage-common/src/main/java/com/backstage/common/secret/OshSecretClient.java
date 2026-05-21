package com.backstage.common.secret;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

/**
 * osh-secret 客户端
 * 从 osh-secret 服务拉取指定环境的所有密钥。
 * 该类不依赖 Spring，可在 EnvironmentPostProcessor 早期阶段使用。
 */
public class OshSecretClient {

    private final String serverUrl;
    private final String accessToken;

    public OshSecretClient(String serverUrl, String accessToken) {
        this.serverUrl = serverUrl;
        this.accessToken = accessToken;
    }

    /**
     * 拉取指定项目、指定环境的所有密钥
     *
     * @param appId 项目标识
     * @param env   环境标识（dev / staging / prod）
     * @return key -> value 映射
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> fetchAll(String appId, String env) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(serverUrl + "/api/secret/" + appId + "/" + env + "/all");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("X-API-Token", accessToken);
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.err.println("[osh-secret] HTTP 状态码: " + responseCode);
                return Collections.emptyMap();
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            JSONObject body = JSON.parseObject(sb.toString());
            Integer code = body.getInteger("code");
            if (code == null || code != 200) {
                System.err.println("[osh-secret] 业务错误: " + body.getString("msg"));
                return Collections.emptyMap();
            }

            Object data = body.get("data");
            if (data instanceof Map) {
                return (Map<String, String>) data;
            }
            return Collections.emptyMap();
        } catch (Exception e) {
            System.err.println("[osh-secret] 拉取密钥失败: " + e.getMessage());
            return Collections.emptyMap();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
