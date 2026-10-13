package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/datetime/PreciseCalendarFormatter.class */
public class PreciseCalendarFormatter extends AbstractCalendarFormatter {
    private static final PreciseCalendarFormatter theInstance = new PreciseCalendarFormatter();

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarFormatter
    public /* bridge */ /* synthetic */ String doFormat(String x0, Object x1) throws IllegalArgumentException {
        return super.doFormat(x0, x1);
    }

    private PreciseCalendarFormatter() {
    }

    public static String format(String format, IDateTimeValueType cal) {
        return theInstance.doFormat(format, cal.getBigValue());
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarFormatter
    protected Calendar toCalendar(Object cal) {
        return ((BigDateTimeValueType) cal).toCalendar();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarFormatter
    protected void formatYear(Object cal, StringBuffer buf) {
        String string;
        BigDateTimeValueType bv = ((IDateTimeValueType) cal).getBigValue();
        BigInteger year = bv.getYear();
        if (year == null) {
            buf.append("0000");
            return;
        }
        if (year.signum() <= 0) {
            buf.append('-');
            string = year.negate().add(BigInteger.ONE).toString();
        } else {
            string = year.toString();
        }
        while (true) {
            String s = string;
            if (s.length() < 4) {
                string = "0" + s;
            } else {
                buf.append(s);
                return;
            }
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarFormatter
    protected void formatMonth(Object cal, StringBuffer buf) {
        BigDateTimeValueType bv = ((IDateTimeValueType) cal).getBigValue();
        formatTwoDigits(bv.getMonth(), 1, buf);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarFormatter
    protected void formatDays(Object cal, StringBuffer buf) {
        BigDateTimeValueType bv = ((IDateTimeValueType) cal).getBigValue();
        formatTwoDigits(bv.getDay(), 1, buf);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarFormatter
    protected void formatHours(Object cal, StringBuffer buf) {
        BigDateTimeValueType bv = ((IDateTimeValueType) cal).getBigValue();
        formatTwoDigits(bv.getHour(), buf);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarFormatter
    protected void formatMinutes(Object cal, StringBuffer buf) {
        BigDateTimeValueType bv = ((IDateTimeValueType) cal).getBigValue();
        formatTwoDigits(bv.getMinute(), buf);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarFormatter
    protected void formatSeconds(Object cal, StringBuffer buf) {
        BigDateTimeValueType bv = ((IDateTimeValueType) cal).getBigValue();
        BigDecimal sec = bv.getSecond();
        if (sec == null) {
            buf.append("00");
            return;
        }
        while (sec.scale() > 0 && sec.toString().endsWith("0")) {
            sec = sec.movePointLeft(1);
        }
        String s = sec.toString();
        if (sec.compareTo(new BigDecimal("10")) < 0) {
            s = "0" + s;
        }
        buf.append(s);
    }

    private void formatTwoDigits(Integer v, StringBuffer buf) {
        formatTwoDigits(v, 0, buf);
    }

    private void formatTwoDigits(Integer v, int offset, StringBuffer buf) {
        if (v == null) {
            buf.append("00");
        } else {
            formatTwoDigits(v.intValue() + offset, buf);
        }
    }
}
