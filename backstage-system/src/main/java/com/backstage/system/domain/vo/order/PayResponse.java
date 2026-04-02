package com.backstage.system.domain.vo.order;

public class PayResponse {
    private int code;
    private String msg;
    private String trade_no;
    private String qrcode;
    private String payurl;

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
    public String getTrade_no() {
        return trade_no;
    }
    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }
    public String getQrcode() {
        return qrcode;
    }
    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }
    public String getPayurl() {
        return payurl;
    }
    public void setPayurl(String payurl) {
        this.payurl = payurl;
    }

    public String toString() {
        return "PayResponse [code=" + code + ", msg=" + msg + ", trade_no=" + trade_no + ", qrcode=" + qrcode
                + ", payurl=" + payurl + "]";
    }
}
