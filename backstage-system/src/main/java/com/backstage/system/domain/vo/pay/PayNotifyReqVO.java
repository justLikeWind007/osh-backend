package com.backstage.system.domain.vo.pay;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 支付回调入参
 */
public class PayNotifyReqVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户ID")
    private String pid;

    @ApiModelProperty(value = "易支付订单号（聚合支付平台订单号）")
    private String trade_no;

    @ApiModelProperty(value = "商户订单号（商户系统内部的订单号）")
    private String out_trade_no;

    @ApiModelProperty(value = "支付方式")
    private String type;

    @ApiModelProperty(value = "商品名称")
    private String name;

    @ApiModelProperty(value = "商品金额")
    private String money;

    @ApiModelProperty(value = "支付状态，TRADE_SUCCESS 表示支付成功")
    private String trade_status;

    @ApiModelProperty(value = "业务扩展参数")
    private String param;

    @ApiModelProperty(value = "签名字符串")
    private String sign;

    @ApiModelProperty(value = "签名类型，默认 MD5")
    private String sign_type;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getTrade_status() {
        return trade_status;
    }

    public void setTrade_status(String trade_status) {
        this.trade_status = trade_status;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }
}
