package com.twelvemonkeys.lang;

import java.util.Date;
import java.util.TimeZone;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/lang/DateUtil.class */
public final class DateUtil {
    public static final long SECOND = 1000;
    public static final long MINUTE = 60000;
    public static final long HOUR = 3600000;
    public static final long DAY = 86400000;
    public static final long CALENDAR_YEAR = 31556952000L;

    private DateUtil() {
    }

    public static long delta(long j) {
        return System.currentTimeMillis() - j;
    }

    public static long delta(Date date) {
        return System.currentTimeMillis() - date.getTime();
    }

    public static long currentTimeSecond() {
        return roundToSecond(System.currentTimeMillis());
    }

    public static long currentTimeMinute() {
        return roundToMinute(System.currentTimeMillis());
    }

    public static long currentTimeHour() {
        return roundToHour(System.currentTimeMillis());
    }

    public static long currentTimeDay() {
        return roundToDay(System.currentTimeMillis());
    }

    public static long roundToSecond(long j) {
        return (j / 1000) * 1000;
    }

    public static long roundToMinute(long j) {
        return (j / MINUTE) * MINUTE;
    }

    public static long roundToHour(long j) {
        return roundToHour(j, TimeZone.getDefault());
    }

    public static long roundToHour(long j, TimeZone timeZone) {
        return ((j / HOUR) * HOUR) - ((long) timeZone.getOffset(j));
    }

    public static long roundToDay(long j) {
        return roundToDay(j, TimeZone.getDefault());
    }

    public static long roundToDay(long j, TimeZone timeZone) {
        int offset = timeZone.getOffset(j);
        return (((j + ((long) offset)) / DAY) * DAY) - ((long) offset);
    }
}
