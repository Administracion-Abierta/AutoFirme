package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import java.math.BigDecimal;
import java.math.BigInteger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/datetime/PreciseCalendarParser.class */
public class PreciseCalendarParser extends AbstractCalendarParser {
    private BigInteger year;
    private Integer month;
    private Integer day;
    private Integer hour;
    private Integer minute;
    private BigDecimal second;
    private java.util.TimeZone timeZone;

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarParser
    public /* bridge */ /* synthetic */ void parse() throws IllegalArgumentException {
        super.parse();
    }

    public static BigDateTimeValueType parse(String format, String value) throws IllegalArgumentException {
        PreciseCalendarParser parser = new PreciseCalendarParser(format, value);
        parser.parse();
        return parser.createCalendar();
    }

    private PreciseCalendarParser(String format, String value) {
        super(format, value);
    }

    private BigDateTimeValueType createCalendar() {
        return new BigDateTimeValueType(this.year, this.month, this.day, this.hour, this.minute, this.second, this.timeZone);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarParser
    protected void parseFractionSeconds() {
        int s = this.vidx;
        BigInteger bi = parseBigInteger(1, Integer.MAX_VALUE);
        BigDecimal d = new BigDecimal(bi, this.vidx - s);
        if (this.second != null) {
            this.second = this.second.add(d);
        } else {
            this.second = d;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarParser
    protected void setTimeZone(java.util.TimeZone tz) {
        if (tz == TimeZone.MISSING) {
            tz = null;
        }
        this.timeZone = tz;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarParser
    protected void setSeconds(int i) {
        BigDecimal d = new BigDecimal(BigInteger.valueOf(i));
        if (this.second != null) {
            this.second = this.second.add(d);
        } else {
            this.second = d;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarParser
    protected void setMinutes(int i) {
        this.minute = new Integer(i);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarParser
    protected void setHours(int i) {
        this.hour = new Integer(i);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarParser
    protected void setDay(int i) {
        this.day = new Integer(i - 1);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarParser
    protected void setMonth(int i) {
        this.month = new Integer(i - 1);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarParser
    protected void setYear(int i) {
        this.year = BigInteger.valueOf(i);
    }
}
