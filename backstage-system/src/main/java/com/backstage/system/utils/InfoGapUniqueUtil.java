package com.backstage.system.utils;

import java.util.concurrent.ThreadLocalRandom;

public class InfoGapUniqueUtil {

    private static final String PREFIX = "info_";
    private static final int SUFFIX_LENGTH = 6;

    private static final char[] CHARACTERS = {
            '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'j', 'k', 'm', 'n', 'p', 'q', 'r', 's',
            't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    public static String generate() {
        StringBuilder sb = new StringBuilder(PREFIX.length() + SUFFIX_LENGTH);
        sb.append(PREFIX);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < SUFFIX_LENGTH; i++) {
            sb.append(CHARACTERS[random.nextInt(CHARACTERS.length)]);
        }
        return sb.toString();
    }
}
