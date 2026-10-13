package org.apache.pdfbox.util;

import com.twelvemonkeys.lang.DateUtil;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSString;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/util/DateConverter.class */
public final class DateConverter {
    private static final int MINUTES_PER_HOUR = 60;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MILLIS_PER_MINUTE = 60000;
    private static final int MILLIS_PER_HOUR = 3600000;
    private static final int HALF_DAY = 43200000;
    private static final int DAY = 86400000;
    private static final String[] ALPHA_START_FORMATS = {"EEEE, dd MMM yy hh:mm:ss a", "EEEE, MMM dd, yy hh:mm:ss a", "EEEE, MMM dd, yy 'at' hh:mma", "EEEE, MMM dd, yy", "EEEE MMM dd, yy HH:mm:ss", "EEEE MMM dd HH:mm:ss z yy", "EEEE MMM dd HH:mm:ss yy"};
    private static final String[] DIGIT_START_FORMATS = {"dd MMM yy HH:mm:ss", "dd MMM yy HH:mm", "yyyy MMM d", "yyyymmddhh:mm:ss", "H:m M/d/yy", "M/d/yy HH:mm:ss", "M/d/yy HH:mm", "M/d/yy"};

    private DateConverter() {
    }

    public static String toString(Calendar cal) {
        if (cal == null) {
            return null;
        }
        String offset = formatTZoffset(cal.get(15) + cal.get(16), OperatorName.SHOW_TEXT_LINE);
        return String.format(Locale.US, "D:%1$4tY%1$2tm%1$2td%1$2tH%1$2tM%1$2tS%2$s'", cal, offset);
    }

    public static String toISO8601(Calendar cal) {
        String offset = formatTZoffset(cal.get(15) + cal.get(16), ":");
        return String.format(Locale.US, "%1$4tY-%1$2tm-%1$2tdT%1$2tH:%1$2tM:%1$2tS%2$s", cal, offset);
    }

    private static int restrainTZoffset(long proposedOffset) {
        if (proposedOffset <= 50400000 && proposedOffset >= -50400000) {
            return (int) proposedOffset;
        }
        long proposedOffset2 = (((proposedOffset + 43200000) % DateUtil.DAY) + DateUtil.DAY) % DateUtil.DAY;
        if (proposedOffset2 == 0) {
            return HALF_DAY;
        }
        return (int) ((proposedOffset2 - 43200000) % 43200000);
    }

    static String formatTZoffset(long millis, String sep) {
        SimpleDateFormat sdf = new SimpleDateFormat("Z");
        sdf.setTimeZone(new SimpleTimeZone(restrainTZoffset(millis), "unknown"));
        String tz = sdf.format(new Date());
        return tz.substring(0, 3) + sep + tz.substring(3);
    }

    private static int parseTimeField(String text, ParsePosition where, int maxlen, int remedy) {
        int cval;
        if (text == null) {
            return remedy;
        }
        int retval = 0;
        int index = where.getIndex();
        int limit = index + Math.min(maxlen, text.length() - index);
        while (index < limit && (cval = text.charAt(index) - '0') >= 0 && cval <= 9) {
            retval = (retval * 10) + cval;
            index++;
        }
        if (index == where.getIndex()) {
            return remedy;
        }
        where.setIndex(index);
        return retval;
    }

    private static char skipOptionals(String text, ParsePosition where, String optionals) {
        char retval = ' ';
        while (where.getIndex() < text.length()) {
            char currch = text.charAt(where.getIndex());
            if (optionals.indexOf(currch) < 0) {
                break;
            }
            retval = currch != ' ' ? currch : retval;
            where.setIndex(where.getIndex() + 1);
        }
        return retval;
    }

    private static boolean skipString(String text, String victim, ParsePosition where) {
        if (text.startsWith(victim, where.getIndex())) {
            where.setIndex(where.getIndex() + victim.length());
            return true;
        }
        return false;
    }

    static GregorianCalendar newGreg() {
        GregorianCalendar retCal = new GregorianCalendar(Locale.ENGLISH);
        retCal.setTimeZone(new SimpleTimeZone(0, "UTC"));
        retCal.setLenient(false);
        retCal.set(14, 0);
        return retCal;
    }

    private static void adjustTimeZoneNicely(GregorianCalendar cal, TimeZone tz) {
        cal.setTimeZone(tz);
        int offset = (cal.get(15) + cal.get(16)) / MILLIS_PER_MINUTE;
        cal.add(12, -offset);
    }

    static boolean parseTZoffset(String text, GregorianCalendar cal, ParsePosition initialWhere) {
        ParsePosition where = new ParsePosition(initialWhere.getIndex());
        TimeZone tz = new SimpleTimeZone(0, "GMT");
        char sign = skipOptionals(text, where, "Z+- ");
        boolean hadGMT = sign == 'Z' || skipString(text, "GMT", where) || skipString(text, "UTC", where);
        char sign2 = !hadGMT ? sign : skipOptionals(text, where, "+- ");
        int tzHours = parseTimeField(text, where, 2, -999);
        skipOptionals(text, where, "': ");
        int tzMin = parseTimeField(text, where, 2, 0);
        skipOptionals(text, where, "' ");
        if (tzHours != -999) {
            int hrSign = sign2 == '-' ? -1 : 1;
            tz.setRawOffset(restrainTZoffset(((long) hrSign) * (((long) (tzHours * MILLIS_PER_HOUR)) + (((long) tzMin) * DateUtil.MINUTE))));
            updateZoneId(tz);
        } else if (!hadGMT) {
            String tzText = text.substring(initialWhere.getIndex()).trim();
            tz = TimeZone.getTimeZone(tzText);
            if ("GMT".equals(tz.getID())) {
                return false;
            }
            where.setIndex(text.length());
        }
        adjustTimeZoneNicely(cal, tz);
        initialWhere.setIndex(where.getIndex());
        return true;
    }

    private static void updateZoneId(TimeZone tz) {
        int offset = tz.getRawOffset();
        char pm = '+';
        if (offset < 0) {
            pm = '-';
            offset = -offset;
        }
        int hh = offset / MILLIS_PER_HOUR;
        int mm = (offset % MILLIS_PER_HOUR) / MILLIS_PER_MINUTE;
        if (offset == 0) {
            tz.setID("GMT");
            return;
        }
        if (pm == '+' && hh <= 12) {
            tz.setID(String.format(Locale.US, "GMT+%02d:%02d", Integer.valueOf(hh), Integer.valueOf(mm)));
        } else if (pm == '-' && hh <= 14) {
            tz.setID(String.format(Locale.US, "GMT-%02d:%02d", Integer.valueOf(hh), Integer.valueOf(mm)));
        } else {
            tz.setID("unknown");
        }
    }

    private static GregorianCalendar parseBigEndianDate(String text, ParsePosition initialWhere) {
        ParsePosition where = new ParsePosition(initialWhere.getIndex());
        int year = parseTimeField(text, where, 4, 0);
        if (where.getIndex() != 4 + initialWhere.getIndex()) {
            return null;
        }
        skipOptionals(text, where, "/- ");
        int month = parseTimeField(text, where, 2, 1) - 1;
        skipOptionals(text, where, "/- ");
        int day = parseTimeField(text, where, 2, 1);
        skipOptionals(text, where, " T");
        int hour = parseTimeField(text, where, 2, 0);
        skipOptionals(text, where, ": ");
        int minute = parseTimeField(text, where, 2, 0);
        skipOptionals(text, where, ": ");
        int second = parseTimeField(text, where, 2, 0);
        char nextC = skipOptionals(text, where, ".");
        if (nextC == '.') {
            parseTimeField(text, where, 19, 0);
        }
        GregorianCalendar dest = newGreg();
        try {
            dest.set(year, month, day, hour, minute, second);
            dest.getTimeInMillis();
            initialWhere.setIndex(where.getIndex());
            skipOptionals(text, initialWhere, " ");
            return dest;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static GregorianCalendar parseSimpleDate(String text, String[] fmts, ParsePosition initialWhere) {
        for (String fmt : fmts) {
            ParsePosition where = new ParsePosition(initialWhere.getIndex());
            SimpleDateFormat sdf = new SimpleDateFormat(fmt, Locale.ENGLISH);
            GregorianCalendar retCal = newGreg();
            sdf.setCalendar(retCal);
            if (sdf.parse(text, where) != null) {
                initialWhere.setIndex(where.getIndex());
                skipOptionals(text, initialWhere, " ");
                return retCal;
            }
        }
        return null;
    }

    private static Calendar parseDate(String text, ParsePosition initialWhere) {
        if (text == null || text.isEmpty() || "D:".equals(text.trim())) {
            return null;
        }
        int longestLen = -999999;
        GregorianCalendar longestDate = null;
        ParsePosition where = new ParsePosition(initialWhere.getIndex());
        skipOptionals(text, where, " ");
        int startPosition = where.getIndex();
        GregorianCalendar retCal = parseBigEndianDate(text, where);
        if (retCal != null && (where.getIndex() == text.length() || parseTZoffset(text, retCal, where))) {
            int whereLen = where.getIndex();
            if (whereLen == text.length()) {
                initialWhere.setIndex(whereLen);
                return retCal;
            }
            longestLen = whereLen;
            longestDate = retCal;
        }
        where.setIndex(startPosition);
        String[] formats = Character.isDigit(text.charAt(startPosition)) ? DIGIT_START_FORMATS : ALPHA_START_FORMATS;
        GregorianCalendar retCal2 = parseSimpleDate(text, formats, where);
        if (retCal2 != null && (where.getIndex() == text.length() || parseTZoffset(text, retCal2, where))) {
            int whereLen2 = where.getIndex();
            if (whereLen2 == text.length()) {
                initialWhere.setIndex(whereLen2);
                return retCal2;
            }
            if (whereLen2 > longestLen) {
                longestLen = whereLen2;
                longestDate = retCal2;
            }
        }
        if (longestDate != null) {
            initialWhere.setIndex(longestLen);
            return longestDate;
        }
        return retCal2;
    }

    public static Calendar toCalendar(COSString text) {
        if (text == null) {
            return null;
        }
        return toCalendar(text.getString());
    }

    public static Calendar toCalendar(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        ParsePosition where = new ParsePosition(0);
        skipOptionals(text, where, " ");
        skipString(text, "D:", where);
        Calendar calendar = parseDate(text, where);
        if (calendar == null || where.getIndex() != text.length()) {
            return null;
        }
        return calendar;
    }
}
