package com.backstage.common.config;

public class PayConfig {

    // 商户ID
    public static final String PID = "1000";

    // 商户KEY
    public static final String KEY = "287p6Y8N7PNY02nvNo80vvO7nYy4162P";

    // 易支付接口
    public static final String API_URL = "http://43.242.200.25:1717/mapi.php";

    // 回调地址 需要回调到后端的话，内网无法进来
    public static final String NOTIFY_URL = "http://公网IP/notify/pay";
    public static final String RETURN_URL = "http://公网IP/return";
}
