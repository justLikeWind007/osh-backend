package com.backstage.system.utils;

import com.backstage.common.config.PayConfig;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

public class  SignUtil {

    // MD5签名
    public static String createSign(Map<String, String> params) {
        // 自动排序
        TreeMap<String, String> treeMap = new TreeMap<>(params);
        // 不会频繁创建String对象
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : treeMap.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();

            // sign、sign_type、空值 不参与签名
            if (k.equals("sign") || k.equals("sign_type") || v == null || v.isEmpty()) {
                continue;
            }
            sb.append(k).append("=").append(v).append("&");
        }

        if (sb.length() > 0) {
            // 删除末尾的&
            sb.deleteCharAt(sb.length() - 1);
        }

        // 拼接KEY 然后md5加密
        String str = sb.toString() + PayConfig.KEY;

        // 转小写
        return md5(str).toLowerCase();
    }

    public static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(str.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                // 用 & 0xff 把高 24 位清零，只保留低 8 位
                String hex = Integer.toHexString(b & 0xff);
                // 保证每个字节 2位二进制 a = 0a
                if (hex.length() == 1)
                    sb.append("0");
                sb.append(hex);
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

}
