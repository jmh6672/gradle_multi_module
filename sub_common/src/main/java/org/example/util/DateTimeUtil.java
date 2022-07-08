package org.example.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtil {
    public static LocalDateTime nowAtKoreaZone() {
        return LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }

    public static LocalDateTime nowFromKoreaZone() {
        return ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }

    public static LocalDateTime nowKorea() {
        return LocalDateTime.now().plusHours(9);
    }

    public static TimeZone getKoreaTimezone() {
        return TimeZone.getTimeZone("Asia/Seoul");
    }

    public static Locale getKoreaLocale() {
        return Locale.KOREA;
    }
}
