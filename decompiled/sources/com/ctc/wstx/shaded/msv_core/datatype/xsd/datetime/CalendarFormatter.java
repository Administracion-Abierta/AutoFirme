package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import java.util.Calendar;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/datetime/CalendarFormatter.class */
public final class CalendarFormatter extends AbstractCalendarFormatter {
    private static final CalendarFormatter theInstance = new CalendarFormatter();

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarFormatter
    public /* bridge */ /* synthetic */ String doFormat(String x0, Object x1) throws IllegalArgumentException {
        return super.doFormat(x0, x1);
    }

    private CalendarFormatter() {
    }

    public static String format(String format, Calendar cal) {
        return theInstance.doFormat(format, cal);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarFormatter
    protected Calendar toCalendar(Object cal) {
        return (Calendar) cal;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarFormatter
    protected void formatYear(Object cal, StringBuffer buf) {
        String string;
        String s;
        int year = ((Calendar) cal).get(1);
        if (year <= 0) {
            string = Integer.toString(1 - year);
        } else {
            string = Integer.toString(year);
        }
        while (true) {
            s = string;
            if (s.length() >= 4) {
                break;
            } else {
                string = "0" + s;
            }
        }
        if (year <= 0) {
            s = "-" + s;
        }
        buf.append(s);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarFormatter
    protected void formatMonth(Object cal, StringBuffer buf) {
        formatTwoDigits(((Calendar) cal).get(2) + 1, buf);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarFormatter
    protected void formatDays(Object cal, StringBuffer buf) {
        formatTwoDigits(((Calendar) cal).get(5), buf);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarFormatter
    protected void formatHours(Object cal, StringBuffer buf) {
        formatTwoDigits(((Calendar) cal).get(11), buf);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarFormatter
    protected void formatMinutes(Object cal, StringBuffer buf) {
        formatTwoDigits(((Calendar) cal).get(12), buf);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarFormatter
    protected void formatSeconds(Object _cal, StringBuffer buf) {
        int n;
        Calendar cal = (Calendar) _cal;
        formatTwoDigits(cal.get(13), buf);
        if (cal.isSet(14) && (n = cal.get(14)) != 0) {
            String string = Integer.toString(n);
            while (true) {
                String ms = string;
                if (ms.length() < 3) {
                    string = "0" + ms;
                } else {
                    buf.append('.');
                    buf.append(ms);
                    return;
                }
            }
        }
    }
}
