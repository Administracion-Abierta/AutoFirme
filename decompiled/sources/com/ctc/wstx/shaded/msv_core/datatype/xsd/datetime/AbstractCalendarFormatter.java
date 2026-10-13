package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import java.util.Calendar;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/datetime/AbstractCalendarFormatter.class */
abstract class AbstractCalendarFormatter {
    protected abstract Calendar toCalendar(Object obj);

    protected abstract void formatYear(Object obj, StringBuffer stringBuffer);

    protected abstract void formatMonth(Object obj, StringBuffer stringBuffer);

    protected abstract void formatDays(Object obj, StringBuffer stringBuffer);

    protected abstract void formatHours(Object obj, StringBuffer stringBuffer);

    protected abstract void formatMinutes(Object obj, StringBuffer stringBuffer);

    protected abstract void formatSeconds(Object obj, StringBuffer stringBuffer);

    AbstractCalendarFormatter() {
    }

    public String doFormat(String format, Object cal) throws IllegalArgumentException {
        int fidx = 0;
        int flen = format.length();
        StringBuffer buf = new StringBuffer();
        while (fidx < flen) {
            int i = fidx;
            fidx++;
            char fch = format.charAt(i);
            if (fch != '%') {
                buf.append(fch);
            } else {
                fidx++;
                switch (format.charAt(fidx)) {
                    case 'D':
                        formatDays(cal, buf);
                        break;
                    case 'M':
                        formatMonth(cal, buf);
                        break;
                    case 'Y':
                        formatYear(cal, buf);
                        break;
                    case 'h':
                        formatHours(cal, buf);
                        break;
                    case 'm':
                        formatMinutes(cal, buf);
                        break;
                    case 's':
                        formatSeconds(cal, buf);
                        break;
                    case 'z':
                        formatTimeZone(cal, buf);
                        break;
                    default:
                        throw new InternalError();
                }
            }
        }
        return buf.toString();
    }

    private void formatTimeZone(Object _cal, StringBuffer buf) {
        int offset;
        Calendar cal = toCalendar(_cal);
        java.util.TimeZone tz = cal.getTimeZone();
        if (tz == null || tz == TimeZone.MISSING) {
            return;
        }
        if (tz == TimeZone.ZERO) {
            buf.append('Z');
            return;
        }
        if (tz.inDaylightTime(cal.getTime())) {
            offset = tz.getRawOffset() + (tz.useDaylightTime() ? 3600000 : 0);
        } else {
            offset = tz.getRawOffset();
        }
        if (offset >= 0) {
            buf.append('+');
        } else {
            buf.append('-');
            offset *= -1;
        }
        int offset2 = offset / 60000;
        formatTwoDigits(offset2 / 60, buf);
        buf.append(':');
        formatTwoDigits(offset2 % 60, buf);
    }

    protected final void formatTwoDigits(int n, StringBuffer buf) {
        if (n < 10) {
            buf.append('0');
        }
        buf.append(n);
    }
}
