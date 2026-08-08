package com.kh.codereview.common.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class KstDateTime {

    public static final ZoneOffset OFFSET = ZoneOffset.of("+09:00");

    private KstDateTime() {
    }

    public static OffsetDateTime from(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.atOffset(OFFSET);
    }
}
