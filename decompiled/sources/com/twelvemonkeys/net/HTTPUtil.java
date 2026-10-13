package com.twelvemonkeys.net;

import com.twelvemonkeys.lang.DateUtil;
import com.twelvemonkeys.lang.StringUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/net/HTTPUtil.class */
public class HTTPUtil {
    private static final SimpleDateFormat HTTP_RFC1123_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
    private static final SimpleDateFormat HTTP_RFC850_FORMAT;
    private static final SimpleDateFormat HTTP_ASCTIME_FORMAT;
    private static long sNext50YearWindowChange;

    static {
        HTTP_RFC1123_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        HTTP_RFC850_FORMAT = new SimpleDateFormat("EEE, dd-MMM-yy HH:mm:ss z", Locale.US);
        HTTP_ASCTIME_FORMAT = new SimpleDateFormat("EEE MMM d HH:mm:ss yy", Locale.US);
        sNext50YearWindowChange = DateUtil.currentTimeDay();
        HTTP_RFC850_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        HTTP_ASCTIME_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        update50YearWindowIfNeeded();
    }

    private static void update50YearWindowIfNeeded() {
        long j = sNext50YearWindowChange;
        if (j < System.currentTimeMillis()) {
            long j2 = j + DateUtil.DAY;
            sNext50YearWindowChange = j2;
            Date date = new Date(j2 - 1577847600000L);
            synchronized (HTTP_RFC850_FORMAT) {
                HTTP_RFC850_FORMAT.set2DigitYearStart(date);
            }
            synchronized (HTTP_ASCTIME_FORMAT) {
                HTTP_ASCTIME_FORMAT.set2DigitYearStart(date);
            }
        }
    }

    private HTTPUtil() {
    }

    public static String formatHTTPDate(long j) {
        return formatHTTPDate(new Date(j));
    }

    public static String formatHTTPDate(Date date) {
        String str;
        synchronized (HTTP_RFC1123_FORMAT) {
            str = HTTP_RFC1123_FORMAT.format(date);
        }
        return str;
    }

    public static long parseHTTPDate(String str) throws NumberFormatException {
        return parseHTTPDateImpl(str).getTime();
    }

    private static Date parseHTTPDateImpl(String str) throws NumberFormatException {
        SimpleDateFormat simpleDateFormat;
        Date date;
        if (str == null) {
            throw new IllegalArgumentException("date == null");
        }
        if (StringUtil.isEmpty(str)) {
            throw new NumberFormatException("Invalid HTTP date: \"" + str + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
        }
        if (str.indexOf(45) >= 0) {
            simpleDateFormat = HTTP_RFC850_FORMAT;
            update50YearWindowIfNeeded();
        } else if (str.indexOf(44) < 0) {
            simpleDateFormat = HTTP_ASCTIME_FORMAT;
            update50YearWindowIfNeeded();
        } else {
            simpleDateFormat = HTTP_RFC1123_FORMAT;
        }
        try {
            synchronized (simpleDateFormat) {
                date = simpleDateFormat.parse(str);
            }
            if (date == null) {
                throw new NumberFormatException("Invalid HTTP date: \"" + str + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
            }
            return date;
        } catch (ParseException e) {
            NumberFormatException numberFormatException = new NumberFormatException("Invalid HTTP date: \"" + str + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
            numberFormatException.initCause(e);
            throw numberFormatException;
        }
    }
}
