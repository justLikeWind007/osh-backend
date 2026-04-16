package com.backstage.common.config;

public class PayConfig {

    // 商户ID
    public static final String PID = "1000";

    // 商户KEY
    public static final String KEY = "287p6Y8N7PNY02nvNo80vvO7nYy4162P";

    // 易支付接口 获取支付url
    public static final String API_URL = "http://43.242.200.25:1717/mapi.php";

    // 查询订单支付状态的接口
    public static final String STATUS = "http://43.242.200.25:1717/api.php";

    // 回调地址 需要回调到后端的话，内网无法进来
    public static final String NOTIFY_URL = "http://2d62305b.r5.cpolar.top/notify/pay";

    // 支付成功的跳转地址 (感觉没必要，前端直接轮询自己跳转就好了)
    public static final String RETURN_URL = "http://127.0.0.1:5173/return";
}
