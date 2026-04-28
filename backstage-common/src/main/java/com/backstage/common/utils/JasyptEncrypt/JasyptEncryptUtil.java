package com.backstage.common.utils.JasyptEncrypt;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.NoIvGenerator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/26
 * Time: 18:57
 */
public class JasyptEncryptUtil {

    public static void main(String[] args) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();

        try {
            String key;
            Path linuxPath = Paths.get("/opt/secure/jasypt.key");

            // 和线上配置完全一样！
            if (Files.exists(linuxPath)) {
                key = Files.readAllLines(linuxPath).get(0);
            } else {
                Path winPath = Paths.get("C:\\secure\\jasypt.key");
                key = Files.readAllLines(winPath).get(0);
            }

            encryptor.setPassword(key.trim());
            encryptor.setAlgorithm("PBEWithMD5AndDES");
            encryptor.setIvGenerator(new NoIvGenerator());

        } catch (Exception e) {
            throw new RuntimeException("读取密钥文件失败", e);
        }

        String password = "321qwerty";
        String encRedisPwd = encryptor.encrypt(password);

        System.out.println("密文：ENC(" + encRedisPwd + ")");
    }
}
