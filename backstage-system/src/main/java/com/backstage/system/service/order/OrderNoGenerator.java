package com.backstage.system.service.order;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class OrderNoGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    public String nextOrderNo() {
        return "O" + timestamp() + randomSuffix();
    }

    public String nextPaymentNo() {
        return "P" + timestamp() + randomSuffix();
    }

    private String timestamp() {
        return LocalDateTime.now().format(FORMATTER);
    }

    private String randomSuffix() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1000000));
    }
}
