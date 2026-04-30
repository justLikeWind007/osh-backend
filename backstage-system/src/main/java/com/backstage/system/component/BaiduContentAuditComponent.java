package com.backstage.system.component;

import com.backstage.common.utils.StringUtils;
import com.baidu.aip.contentcensor.AipContentCensor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 百度智能云内容审核组件。
 */
@Component
public class BaiduContentAuditComponent {

    @Value("${baidu.ai.app-id:}")
    private String appId;

    @Value("${baidu.ai.api-key:}")
    private String apiKey;

    @Value("${baidu.ai.secret-key:}")
    private String secretKey;

    @Value("${baidu.ai.timeout:3000}")
    private Integer timeout;

    private AipContentCensor aipContentCensor;

    @PostConstruct
    public void init() {
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(apiKey) || StringUtils.isEmpty(secretKey)) {
            return;
        }

        AipContentCensor contentCensor = new AipContentCensor(appId, apiKey, secretKey);
        int actualTimeout = timeout == null || timeout <= 0 ? 3000 : timeout;
        contentCensor.setConnectionTimeoutInMillis(actualTimeout);
        contentCensor.setSocketTimeoutInMillis(actualTimeout);
        this.aipContentCensor = contentCensor;
    }

    public boolean isEnabled() {
        return aipContentCensor != null;
    }

    public boolean isTextCompliant(String content) {
        return auditText(content).isPass();
    }

    public BaiduTextAuditResult auditText(String content) {
        String text = StringUtils.trim(content);
        if (StringUtils.isEmpty(text)) {
            return BaiduTextAuditResult.emptyPass();
        }
        if (aipContentCensor == null) {
            return BaiduTextAuditResult.failure("百度内容审核未配置，请检查 baidu.ai 配置项");
        }

        JSONObject response = aipContentCensor.textCensorUserDefined(text);
        return buildTextAuditResult(response);
    }

    private BaiduTextAuditResult buildTextAuditResult(JSONObject response) {
        if (response == null) {
            return BaiduTextAuditResult.failure("百度内容审核返回为空");
        }

        BaiduTextAuditResult result = new BaiduTextAuditResult();
        result.setRawResponse(response.toString());

        int errorCode = response.optInt("error_code", 0);
        if (errorCode != 0) {
            result.setSuccess(false);
            result.setPass(false);
            result.setErrorCode(errorCode);
            result.setErrorMsg(response.optString("error_msg"));
            return result;
        }

        Integer conclusionType = response.has("conclusionType") ? response.optInt("conclusionType") : null;
        result.setSuccess(true);
        result.setConclusion(response.optString("conclusion"));
        result.setConclusionType(conclusionType);
        result.setPass(conclusionType != null && conclusionType == 1);
        result.setHitWords(extractHitWords(response.optJSONArray("data")));
        return result;
    }

    private List<String> extractHitWords(JSONArray dataArray) {
        Set<String> hitWords = new LinkedHashSet<>();
        if (dataArray == null) {
            return new ArrayList<>();
        }

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject dataObject = dataArray.optJSONObject(i);
            if (dataObject == null) {
                continue;
            }

            JSONArray hitsArray = dataObject.optJSONArray("hits");
            if (hitsArray != null) {
                for (int j = 0; j < hitsArray.length(); j++) {
                    JSONObject hitObject = hitsArray.optJSONObject(j);
                    if (hitObject == null) {
                        continue;
                    }
                    addWords(hitWords, hitObject.optJSONArray("words"));
                    addWord(hitWords, hitObject.optString("msg"));
                }
            }

            addWord(hitWords, dataObject.optString("msg"));
        }
        return new ArrayList<>(hitWords);
    }

    private void addWords(Set<String> hitWords, JSONArray wordsArray) {
        if (wordsArray == null) {
            return;
        }
        for (int i = 0; i < wordsArray.length(); i++) {
            addWord(hitWords, wordsArray.optString(i));
        }
    }

    private void addWord(Set<String> hitWords, String word) {
        if (StringUtils.isNotEmpty(word)) {
            hitWords.add(word);
        }
    }
}
