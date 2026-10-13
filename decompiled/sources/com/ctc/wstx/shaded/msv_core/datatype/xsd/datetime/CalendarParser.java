package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import java.util.GregorianCalendar;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/datetime/CalendarParser.class */
public final class CalendarParser extends AbstractCalendarParser {
    private final GregorianCalendar cal;

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarParser
    public /* bridge */ /* synthetic */ void parse() throws IllegalArgumentException {
        super.parse();
    }

    public static GregorianCalendar parse(String format, String value) throws IllegalArgumentException {
        CalendarParser parser = new CalendarParser(format, value);
        parser.parse();
        return parser.cal;
    }

    private CalendarParser(String format, String value) {
        super(format, value);
        this.cal = new GregorianCalendar(0, 0, 0);
        this.cal.clear(1);
        this.cal.clear(2);
        this.cal.clear(5);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarParser
    protected void parseFractionSeconds() {
        this.cal.set(14, parseInt(1, 3));
        skipDigits();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarParser
    protected void setTimeZone(java.util.TimeZone tz) {
        this.cal.setTimeZone(tz);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarParser
    protected void setSeconds(int i) {
        this.cal.set(13, i);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarParser
    protected void setMinutes(int i) {
        this.cal.set(12, i);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarParser
    protected void setHours(int i) {
        this.cal.set(11, i);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarParser
    protected void setDay(int i) {
        this.cal.set(5, i);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarParser
    protected void setMonth(int i) {
        this.cal.set(2, i - 1);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarParser
    protected void setYear(int i) {
        this.cal.set(1, i);
    }
}
