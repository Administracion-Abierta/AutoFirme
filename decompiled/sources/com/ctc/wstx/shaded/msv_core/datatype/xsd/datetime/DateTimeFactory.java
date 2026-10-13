package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import java.math.BigDecimal;
import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/datetime/DateTimeFactory.class */
public class DateTimeFactory {
    public static IDateTimeValueType createFromDateTime(Number year, Integer month, Integer day, Integer hour, Integer minute, Number mSecond, java.util.TimeZone zone) {
        BigDecimal second = null;
        if (year instanceof Integer) {
            year = new BigInteger(year.toString());
        }
        if (mSecond != null) {
            if (mSecond instanceof Integer) {
                second = new BigDecimal(mSecond.toString()).movePointLeft(3);
            } else if (mSecond instanceof BigDecimal) {
                second = ((BigDecimal) mSecond).movePointLeft(3);
            } else {
                throw new UnsupportedOperationException();
            }
        }
        return new BigDateTimeValueType((BigInteger) year, month, day, hour, minute, second, zone);
    }

    public static IDateTimeValueType createFromDate(Number year, Integer month, Integer day, java.util.TimeZone zone) {
        return createFromDateTime(year, month, day, null, null, null, zone);
    }

    public static IDateTimeValueType createFromTime(Integer hour, Integer minute, Number mSecond, java.util.TimeZone zone) {
        return createFromDateTime(null, null, null, hour, minute, mSecond, zone);
    }
}
