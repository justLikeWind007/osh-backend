package com.backstage.common.utils.JasyptEncrypt;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.NoIvGenerator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/26
 * Time: 18:57
 */
public class JasyptEncryptUtil {
    private static final String ALGORITHM = "PBEWithMD5AndDES";
    private static final Path LINUX_KEY = Paths.get("/opt/secure/jasypt.key");
    private static final Path WINDOWS_KEY = Paths.get("C:\\secure\\jasypt.key");

    public static void main(String[] args) {
        try {
            StandardPBEStringEncryptor encryptor = getEncryptor();
            Scanner scanner = new Scanner(System.in);
            System.out.println("===== Jasypt 加解密工具（输入 exit 退出）=====");
            System.out.println("1. 直接输入明文 → 加密");
            System.out.println("2. 输入 ENC(xxx) → 解密\n");
            while (true) {
                System.out.print("请输入内容：");
                String text = scanner.nextLine().trim();

                if ("exit".equalsIgnoreCase(text)) break;
                if (text.isEmpty()) continue;

                if (text.startsWith("ENC(") && text.endsWith(")")) {
                    String cipher = text.substring(4, text.length() - 1);
                    String plain = encryptor.decrypt(cipher);
                    System.out.println("解密结果：" + plain + "\n");
                } else {
                    String cipher = encryptor.encrypt(text);
                    System.out.println("加密结果：ENC(" + cipher + ")\n");
                }
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static StandardPBEStringEncryptor getEncryptor() throws Exception {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm(ALGORITHM);
        encryptor.setIvGenerator(new NoIvGenerator());
        String key = Files.exists(LINUX_KEY) ? readFileContent(LINUX_KEY) : readFileContent(WINDOWS_KEY);
        encryptor.setPassword(key);
        return encryptor;
    }

    /** Java 8 兼容的文件读取，替代 Java 11 的 Files.readString */
    private static String readFileContent(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes, StandardCharsets.UTF_8).trim();
    }
}