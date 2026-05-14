package com.backstage.system.domain.vo.order;

import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * 支付渠道响应。
 */
public class PayResponse {

    private int code;

    private String msg;

    @JsonAlias("trade_no")
    private String tradeNo;

    private String qrcode;

    @JsonAlias("payurl")
    private String payUrl;

    @JsonAlias("out_trade_no")
    private String outTradeNo;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    @Override
    public String toString() {
        return "PayResponse [code=" + code + ", msg=" + msg + ", tradeNo=" + tradeNo + ", qrcode=" + qrcode
                + ", payUrl=" + payUrl + ", outTradeNo=" + outTradeNo + "]";
    }
}
