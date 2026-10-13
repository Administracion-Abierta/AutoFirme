package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.GregorianCalendar;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/datetime/BigDateTimeValueType.class */
public class BigDateTimeValueType implements IDateTimeValueType {
    private BigInteger year;
    private Integer month;
    private Integer day;
    private Integer hour;
    private Integer minute;
    private BigDecimal second;
    private java.util.TimeZone zone;
    private IDateTimeValueType normalizedValue;
    private static final long serialVersionUID = 1;

    public BigInteger getYear() {
        return this.year;
    }

    public Integer getMonth() {
        return this.month;
    }

    public Integer getDay() {
        return this.day;
    }

    public Integer getHour() {
        return this.hour;
    }

    public Integer getMinute() {
        return this.minute;
    }

    public BigDecimal getSecond() {
        return this.second;
    }

    public java.util.TimeZone getTimeZone() {
        return this.zone;
    }

    public BigDateTimeValueType(BigDateTimeValueType base, java.util.TimeZone newTimeZone) {
        this(base.year, base.month, base.day, base.hour, base.minute, base.second, newTimeZone);
    }

    public BigDateTimeValueType(BigInteger year, int month, int day, int hour, int minute, BigDecimal second, java.util.TimeZone timeZone) {
        this(year, new Integer(month), new Integer(day), new Integer(hour), new Integer(minute), second, timeZone);
    }

    public BigDateTimeValueType(BigInteger year, Integer month, Integer day, Integer hour, Integer minute, BigDecimal second, java.util.TimeZone timeZone) {
        this.normalizedValue = null;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.zone = timeZone;
    }

    public BigDateTimeValueType() {
        this.normalizedValue = null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.IDateTimeValueType
    public BigDateTimeValueType getBigValue() {
        return this;
    }

    public boolean equals(Object o) {
        return equals((IDateTimeValueType) o);
    }

    public boolean equals(IDateTimeValueType rhs) {
        if (!(rhs instanceof BigDateTimeValueType)) {
            rhs = rhs.getBigValue();
        }
        return equals(this, (BigDateTimeValueType) rhs);
    }

    public boolean equals(BigDateTimeValueType lhs, BigDateTimeValueType rhs) {
        return compare(lhs, rhs) == 0;
    }

    public String toString() {
        return PreciseCalendarFormatter.format("%Y-%M-%DT%h:%m:%s%z", this);
    }

    public int hashCode() {
        BigDateTimeValueType n = (BigDateTimeValueType) normalize();
        return Util.objHashCode(n.year) + Util.objHashCode(n.month) + Util.objHashCode(n.day) + Util.objHashCode(n.hour) + Util.objHashCode(n.minute) + Util.objHashCode(n.second) + Util.objHashCode(n.zone);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.IDateTimeValueType
    public int compare(IDateTimeValueType o) {
        if (!(o instanceof BigDateTimeValueType)) {
            o = o.getBigValue();
        }
        return compare(this, (BigDateTimeValueType) o);
    }

    protected static int compare(BigDateTimeValueType lhs, BigDateTimeValueType rhs) {
        BigDateTimeValueType lhs2 = (BigDateTimeValueType) lhs.normalize();
        BigDateTimeValueType rhs2 = (BigDateTimeValueType) rhs.normalize();
        if ((lhs2.zone != null && rhs2.zone != null) || (lhs2.zone == null && rhs2.zone == null)) {
            if (!Util.objEqual(lhs2.year, rhs2.year)) {
                return Util.objCompare(lhs2.year, rhs2.year);
            }
            if (!Util.objEqual(lhs2.month, rhs2.month)) {
                return Util.objCompare(lhs2.month, rhs2.month);
            }
            if (!Util.objEqual(lhs2.day, rhs2.day)) {
                return Util.objCompare(lhs2.day, rhs2.day);
            }
            if (!Util.objEqual(lhs2.hour, rhs2.hour)) {
                return Util.objCompare(lhs2.hour, rhs2.hour);
            }
            if (!Util.objEqual(lhs2.minute, rhs2.minute)) {
                return Util.objCompare(lhs2.minute, rhs2.minute);
            }
            if (!Util.objEqual(lhs2.second, rhs2.second)) {
                return Util.objCompare(lhs2.second, rhs2.second);
            }
            return 0;
        }
        if (lhs2.zone == null) {
            int r = compare((BigDateTimeValueType) new BigDateTimeValueType(lhs2, Util.timeZoneNeg14).normalize(), rhs2);
            if (r == 0 || r == -1) {
                return -1;
            }
            int r2 = compare((BigDateTimeValueType) new BigDateTimeValueType(lhs2, Util.timeZonePos14).normalize(), rhs2);
            if (r2 == 0 || r2 == 1) {
                return 1;
            }
            return 999;
        }
        int r3 = compare(lhs2, new BigDateTimeValueType(rhs2, Util.timeZonePos14));
        if (r3 == 0 || r3 == -1) {
            return -1;
        }
        int r4 = compare(lhs2, new BigDateTimeValueType(rhs2, Util.timeZoneNeg14));
        if (r4 == 0 || r4 == 1) {
            return 1;
        }
        return 999;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.IDateTimeValueType
    public IDateTimeValueType normalize() {
        if (this.zone == TimeZone.ZERO || this.zone == null) {
            return this;
        }
        if (this.normalizedValue != null) {
            return this.normalizedValue;
        }
        this.normalizedValue = add(BigTimeDurationValueType.fromMinutes((-this.zone.getRawOffset()) / 60000));
        ((BigDateTimeValueType) this.normalizedValue).zone = TimeZone.ZERO;
        return this.normalizedValue;
    }

    private static BigInteger nullAs0(BigInteger o) {
        if (o != null) {
            return o;
        }
        return BigInteger.ZERO;
    }

    private static BigDecimal nullAs0(BigDecimal o) {
        if (o != null) {
            return o;
        }
        return Util.decimal0;
    }

    private static BigInteger[] divideAndRemainder(BigInteger x1, BigInteger x2) {
        BigInteger[] r = x1.divideAndRemainder(x2);
        if (r[1].signum() < 0) {
            r[1] = r[1].add(x2);
            r[0] = r[0].subtract(BigInteger.ONE);
        }
        return r;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.IDateTimeValueType
    public IDateTimeValueType add(ITimeDurationValueType _rhs) {
        int tempDays;
        int carry;
        if (_rhs instanceof BigTimeDurationValueType) {
            BigTimeDurationValueType rhs = (BigTimeDurationValueType) _rhs;
            BigInteger[] quoAndMod = divideAndRemainder(Util.int2bi(this.month).add(signed(rhs, rhs.month)), Util.the12);
            int omonth = quoAndMod[1].intValue();
            BigInteger oyear = quoAndMod[0].add(nullAs0(this.year)).add(signed(rhs, rhs.year));
            BigDecimal sec = nullAs0(this.second).add(signed(rhs, rhs.second));
            BigInteger[] quoAndMod2 = divideAndRemainder(sec.unscaledValue(), Util.the60.multiply(Util.the10.pow(sec.scale())));
            BigDecimal osecond = new BigDecimal(quoAndMod2[1], sec.scale());
            BigInteger[] quoAndMod3 = divideAndRemainder(quoAndMod2[0].add(Util.int2bi(this.minute)).add(signed(rhs, rhs.minute)), Util.the60);
            int ominute = quoAndMod3[1].intValue();
            BigInteger[] quoAndMod4 = divideAndRemainder(quoAndMod3[0].add(Util.int2bi(this.hour)).add(signed(rhs, rhs.hour)), Util.the24);
            int ohour = quoAndMod4[1].intValue();
            int md = Util.maximumDayInMonthFor(oyear, omonth);
            int dayValue = this.day != null ? this.day.intValue() : 0;
            if (dayValue < 0) {
                tempDays = 0;
            } else if (dayValue >= md) {
                tempDays = md - 1;
            } else {
                tempDays = dayValue;
            }
            BigInteger oday = signed(rhs, rhs.day).add(quoAndMod4[0]).add(Util.int2bi(tempDays));
            while (true) {
                if (oday.signum() == -1) {
                    oday = oday.add(Util.int2bi(Util.maximumDayInMonthFor(oyear, (omonth + 11) % 12)));
                    carry = -1;
                } else {
                    BigInteger bmd = Util.int2bi(Util.maximumDayInMonthFor(oyear, omonth));
                    if (oday.compareTo(bmd) < 0) {
                        break;
                    }
                    oday = oday.subtract(bmd);
                    carry = 1;
                }
                int omonth2 = omonth + carry;
                if (omonth2 < 0) {
                    omonth2 += 12;
                    oyear = oyear.subtract(BigInteger.ONE);
                }
                oyear = oyear.add(Util.int2bi(omonth2 / 12));
                omonth = omonth2 % 12;
            }
            return new BigDateTimeValueType(this.year != null ? oyear : null, this.month != null ? new Integer(omonth) : null, this.day != null ? new Integer(oday.intValue()) : null, this.hour != null ? new Integer(ohour) : null, this.minute != null ? new Integer(ominute) : null, this.second != null ? osecond : null, this.zone);
        }
        return add(_rhs.getBigValue());
    }

    private BigInteger signed(BigTimeDurationValueType dur, BigInteger i) {
        return dur.signum < 0 ? i.negate() : i;
    }

    private BigDecimal signed(BigTimeDurationValueType dur, BigDecimal i) {
        return dur.signum < 0 ? i.negate() : i;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.IDateTimeValueType
    public Calendar toCalendar() {
        Calendar cal = new GregorianCalendar(0, 0, 0);
        cal.setTimeZone(createJavaTimeZone());
        cal.clear(1);
        cal.clear(2);
        cal.clear(5);
        if (getYear() != null) {
            cal.set(1, getYear().intValue());
        }
        if (getMonth() != null) {
            cal.set(2, getMonth().intValue());
        }
        if (getDay() != null) {
            cal.set(5, getDay().intValue() + 1);
        }
        if (getHour() != null) {
            cal.set(11, getHour().intValue());
        }
        if (getMinute() != null) {
            cal.set(12, getMinute().intValue());
        }
        if (getSecond() != null) {
            cal.set(13, getSecond().intValue());
            cal.set(14, getSecond().movePointRight(3).intValue() % 1000);
        }
        return cal;
    }

    protected java.util.TimeZone createJavaTimeZone() {
        java.util.TimeZone tz = getTimeZone();
        return tz == null ? TimeZone.MISSING : tz;
    }
}
