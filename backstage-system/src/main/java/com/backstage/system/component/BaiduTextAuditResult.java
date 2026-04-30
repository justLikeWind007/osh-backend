package com.backstage.system.component;

import java.util.ArrayList;
import java.util.List;

/**
 * 百度文本审核结果。
 */
public class BaiduTextAuditResult {

    private boolean success;

    private boolean pass;

    private Integer conclusionType;

    private String conclusion;

    private Integer errorCode;

    private String errorMsg;

    private List<String> hitWords = new ArrayList<>();

    private String rawResponse;

    public static BaiduTextAuditResult emptyPass() {
        BaiduTextAuditResult result = new BaiduTextAuditResult();
        result.setSuccess(true);
        result.setPass(true);
        result.setConclusion("EMPTY_CONTENT");
        result.setConclusionType(1);
        return result;
    }

    public static BaiduTextAuditResult failure(String errorMsg) {
        BaiduTextAuditResult result = new BaiduTextAuditResult();
        result.setSuccess(false);
        result.setPass(false);
        result.setErrorMsg(errorMsg);
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

    public Integer getConclusionType() {
        return conclusionType;
    }

    public void setConclusionType(Integer conclusionType) {
        this.conclusionType = conclusionType;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<String> getHitWords() {
        return hitWords;
    }

    public void setHitWords(List<String> hitWords) {
        this.hitWords = hitWords;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }
}
