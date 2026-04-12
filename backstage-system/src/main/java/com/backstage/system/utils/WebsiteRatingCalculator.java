package com.backstage.system.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @author xuanqing
 * @create 2026-04-10 23:46
 */
public class WebsiteRatingCalculator {
    private static final double GOOD_WEIGHT = 10.0;
    private static final double MID_WEIGHT = 3.0;
    private static final double BAD_WEIGHT = 8.0;
    private static final double CLICK_WEIGHT = 0.01;
    private static final double TIME_DECAY_BASE = 100.0;

    public static BigDecimal calculateRatingScore(Integer goodCount, Integer midCount,
                                                  Integer badCount, Integer clickCount,
                                                  Date createTime) {
        if (createTime == null) {
            return BigDecimal.ZERO;
        }

        double goodScore = safeInt(goodCount) * GOOD_WEIGHT;
        double midScore = safeInt(midCount) * MID_WEIGHT;
        double badScore = safeInt(badCount) * BAD_WEIGHT;
        double clickScore = safeInt(clickCount) * CLICK_WEIGHT;
        double timeDecayScore = calculateTimeDecay(createTime);

        double totalScore = goodScore + midScore - badScore + clickScore + timeDecayScore;

        return new BigDecimal(totalScore).setScale(2, RoundingMode.HALF_UP);
    }

    private static int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private static double calculateTimeDecay(Date createTime) {
        LocalDate createLocalDate = createTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalDate now = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(createLocalDate, now);

        return TIME_DECAY_BASE / (daysBetween + 1);
    }
}
