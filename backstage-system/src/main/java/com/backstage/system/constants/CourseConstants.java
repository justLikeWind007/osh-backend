package com.backstage.system.constants;

import java.math.BigDecimal;

public final class CourseConstants {

    public static final int STATUS_DRAFT = 0;
    public static final int STATUS_PENDING_AUDIT = 1;
    public static final int STATUS_PUBLISHED = 2;
    public static final int DEFAULT_COUNT = 0;
    public static final long DEFAULT_LONG_COUNT = 0L;
    public static final BigDecimal DEFAULT_RATING_SCORE = BigDecimal.ZERO;

    private CourseConstants() {
    }
}
