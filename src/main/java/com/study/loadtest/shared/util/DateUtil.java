package com.study.loadtest.shared.util;

import lombok.NonNull;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateUtil {

    // TODO: 프로필의 zoneId를 사용, fallback으로 systemDefault
    private static ZoneId getZoneId() {
        return ZoneId.systemDefault();
    }

    @NonNull
    public static String toIsoZonedString(@NonNull OffsetDateTime time) {
        return time.atZoneSameInstant(getZoneId())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
