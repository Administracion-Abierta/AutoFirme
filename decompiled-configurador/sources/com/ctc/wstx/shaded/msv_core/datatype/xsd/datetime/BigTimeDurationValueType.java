package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.Comparator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.xml.security.utils.Constants;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/datetime/BigTimeDurationValueType.class */
public class BigTimeDurationValueType implements ITimeDurationValueType {
    protected int signum;
    protected BigInteger year;
    protected BigInteger month;
    protected BigInteger day;
    protected BigInteger hour;
    protected BigInteger minute;
    protected BigDecimal second;
    private static final BigDateTimeValueType[] testInstance = {new BigDateTimeValueType(new BigInteger("1696"), 8, 0, 0, 0, new BigDecimal(0), TimeZone.ZERO), new BigDateTimeValueType(new BigInteger("1697"), 1, 0, 0, 0, new BigDecimal(0), TimeZone.ZERO), new BigDateTimeValueType(new BigInteger("1903"), 2, 0, 0, 0, new BigDecimal(0), TimeZone.ZERO), new BigDateTimeValueType(new BigInteger("1903"), 6, 0, 0, 0, new BigDecimal(0), TimeZone.ZERO)};
    private static final long serialVersionUID = 1;

    public boolean equals(Object o) {
        return equals((ITimeDurationValueType) o);
    }

    public boolean equals(ITimeDurationValueType o) {
        return compare(o) == 0;
    }

    public String toString() {
        return (this.signum < 0 ? "-" : "") + Constants._TAG_P + nullAsZero(this.year).abs() + Constants._TAG_Y + nullAsZero(this.month) + "M" + nullAsZero(this.day) + "DT" + nullAsZero(this.hour) + "H" + nullAsZero(this.minute) + "M" + (this.second == null ? "" : this.second.toString()) + "S";
    }

    private BigInteger nullAsZero(BigInteger o) {
        if (o == null) {
            return BigInteger.ZERO;
        }
        return o;
    }

    public int hashCode() {
        return nullAsZero(this.day).multiply(Util.the24).add(nullAsZero(this.hour)).multiply(Util.the60).add(nullAsZero(this.minute)).mod(Util.the210379680).hashCode();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.ITimeDurationValueType
    public int compare(ITimeDurationValueType o) {
        if (!(o instanceof BigTimeDurationValueType)) {
            o = o.getBigValue();
        }
        return compare(this, (BigTimeDurationValueType) o);
    }

    private static int compare(BigTimeDurationValueType lhs, BigTimeDurationValueType rhs) {
        boolean less = false;
        boolean greater = false;
        boolean noDeterminate = false;
        for (int i = 0; i < testInstance.length; i++) {
            BigDateTimeValueType l = (BigDateTimeValueType) testInstance[i].add(lhs);
            BigDateTimeValueType r = (BigDateTimeValueType) testInstance[i].add(rhs);
            int v = BigDateTimeValueType.compare(l, r);
            if (v < 0) {
                less = true;
            }
            if (v > 0) {
                greater = true;
            }
            if (v == 0 && !l.equals((IDateTimeValueType) r)) {
                noDeterminate = true;
            }
        }
        if (noDeterminate) {
            return Comparator.UNDECIDABLE;
        }
        if (less && greater) {
            return Comparator.UNDECIDABLE;
        }
        if (less) {
            return -1;
        }
        if (greater) {
            return 1;
        }
        return 0;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.ITimeDurationValueType
    public BigTimeDurationValueType getBigValue() {
        return this;
    }

    public BigTimeDurationValueType(int signum, BigInteger year, BigInteger month, BigInteger day, BigInteger hour, BigInteger minute, BigDecimal second) {
        this.signum = signum;
        this.year = year != null ? year : BigInteger.ZERO;
        this.month = month != null ? month : BigInteger.ZERO;
        this.day = day != null ? day : BigInteger.ZERO;
        this.hour = hour != null ? hour : BigInteger.ZERO;
        this.minute = minute != null ? minute : BigInteger.ZERO;
        this.second = second != null ? second : Util.decimal0;
    }

    public static BigTimeDurationValueType fromMinutes(int minutes) {
        return fromMinutes(Util.int2bi(minutes));
    }

    public static BigTimeDurationValueType fromMinutes(BigInteger minutes) {
        return new BigTimeDurationValueType(minutes.signum(), null, null, null, null, minutes.abs(), null);
    }

    public BigInteger getDay() {
        return this.day;
    }

    public BigInteger getHour() {
        return this.hour;
    }

    public BigInteger getMinute() {
        return this.minute;
    }

    public BigInteger getMonth() {
        return this.month;
    }

    public BigDecimal getSecond() {
        return this.second;
    }

    public BigInteger getYear() {
        return this.year;
    }

    public BigTimeDurationValueType(String lexicalRepresentation) throws IllegalArgumentException {
        boolean positive;
        int[] idx = new int[1];
        if (lexicalRepresentation.charAt(idx[0]) == '-') {
            idx[0] = idx[0] + 1;
            positive = false;
        } else {
            positive = true;
        }
        int i = idx[0];
        idx[0] = i + 1;
        if (lexicalRepresentation.charAt(i) != 'P') {
            throw new IllegalArgumentException(lexicalRepresentation);
        }
        int dateLen = 0;
        String[] dateParts = new String[3];
        int[] datePartsIndex = new int[3];
        while (lexicalRepresentation.length() != idx[0] && isDigit(lexicalRepresentation.charAt(idx[0])) && dateLen < 3) {
            datePartsIndex[dateLen] = idx[0];
            int i2 = dateLen;
            dateLen++;
            dateParts[i2] = parsePiece(lexicalRepresentation, idx);
        }
        if (lexicalRepresentation.length() != idx[0]) {
            int i3 = idx[0];
            idx[0] = i3 + 1;
            if (lexicalRepresentation.charAt(i3) != 'T') {
                throw new IllegalArgumentException(lexicalRepresentation);
            }
        }
        int timeLen = 0;
        String[] timeParts = new String[3];
        int[] timePartsIndex = new int[3];
        while (lexicalRepresentation.length() != idx[0] && isDigitOrPeriod(lexicalRepresentation.charAt(idx[0])) && timeLen < 3) {
            timePartsIndex[timeLen] = idx[0];
            int i4 = timeLen;
            timeLen++;
            timeParts[i4] = parsePiece(lexicalRepresentation, idx);
        }
        if (lexicalRepresentation.length() != idx[0]) {
            throw new IllegalArgumentException(lexicalRepresentation);
        }
        if (dateLen == 0 && timeLen == 0) {
            throw new IllegalArgumentException(lexicalRepresentation);
        }
        organizeParts(lexicalRepresentation, dateParts, datePartsIndex, dateLen, "YMD");
        organizeParts(lexicalRepresentation, timeParts, timePartsIndex, timeLen, "HMS");
        this.year = parseBigInteger(lexicalRepresentation, dateParts[0], datePartsIndex[0]);
        this.month = parseBigInteger(lexicalRepresentation, dateParts[1], datePartsIndex[1]);
        this.day = parseBigInteger(lexicalRepresentation, dateParts[2], datePartsIndex[2]);
        this.hour = parseBigInteger(lexicalRepresentation, timeParts[0], timePartsIndex[0]);
        this.minute = parseBigInteger(lexicalRepresentation, timeParts[1], timePartsIndex[1]);
        this.second = parseBigDecimal(lexicalRepresentation, timeParts[2], timePartsIndex[2]);
        this.year = this.year != null ? this.year : BigInteger.ZERO;
        this.month = this.month != null ? this.month : BigInteger.ZERO;
        this.day = this.day != null ? this.day : BigInteger.ZERO;
        this.hour = this.hour != null ? this.hour : BigInteger.ZERO;
        this.minute = this.minute != null ? this.minute : BigInteger.ZERO;
        this.second = this.second != null ? this.second : Util.decimal0;
        if (getSignum(this.year) == 0 && getSignum(this.month) == 0 && getSignum(this.day) == 0 && getSignum(this.hour) == 0 && getSignum(this.minute) == 0 && getSignum(this.second) == 0) {
            this.signum = 0;
        } else if (positive) {
            this.signum = 1;
        } else {
            this.signum = -1;
        }
    }

    private int getSignum(BigInteger i) {
        if (i == null) {
            return 0;
        }
        return i.signum();
    }

    private int getSignum(BigDecimal i) {
        if (i == null) {
            return 0;
        }
        return i.signum();
    }

    private static boolean isDigit(char ch) {
        return '0' <= ch && ch <= '9';
    }

    private static boolean isDigitOrPeriod(char ch) {
        return isDigit(ch) || ch == '.';
    }

    private static String parsePiece(String whole, int[] idx) throws IllegalArgumentException {
        int start = idx[0];
        while (idx[0] < whole.length() && isDigitOrPeriod(whole.charAt(idx[0]))) {
            idx[0] = idx[0] + 1;
        }
        if (idx[0] == whole.length()) {
            throw new IllegalArgumentException(whole);
        }
        idx[0] = idx[0] + 1;
        return whole.substring(start, idx[0]);
    }

    private static void organizeParts(String whole, String[] parts, int[] partsIndex, int len, String tokens) throws IllegalArgumentException {
        int idx = tokens.length();
        for (int i = len - 1; i >= 0; i--) {
            int nidx = tokens.lastIndexOf(parts[i].charAt(parts[i].length() - 1), idx - 1);
            if (nidx == -1) {
                throw new IllegalArgumentException(whole);
            }
            for (int j = nidx + 1; j < idx; j++) {
                parts[j] = null;
            }
            idx = nidx;
            parts[idx] = parts[i];
            partsIndex[idx] = partsIndex[i];
        }
        while (true) {
            idx--;
            if (idx >= 0) {
                parts[idx] = null;
            } else {
                return;
            }
        }
    }

    private static BigInteger parseBigInteger(String whole, String part, int index) throws IllegalArgumentException {
        if (part == null) {
            return null;
        }
        return new BigInteger(part.substring(0, part.length() - 1));
    }

    private static BigDecimal parseBigDecimal(String whole, String part, int index) throws IllegalArgumentException {
        if (part == null) {
            return null;
        }
        return new BigDecimal(part.substring(0, part.length() - 1));
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        switch (this.year.signum()) {
            case -1:
                this.signum = -1;
                this.year = this.year.negate();
                break;
            case 1:
                this.signum = 1;
                break;
            default:
                switch (this.month.signum()) {
                    case -1:
                        this.signum = -1;
                        this.month = this.month.negate();
                        break;
                    case 1:
                        this.signum = 1;
                        break;
                    default:
                        switch (this.day.signum()) {
                            case -1:
                                this.signum = -1;
                                this.day = this.day.negate();
                                break;
                            case 1:
                                this.signum = 1;
                                break;
                            default:
                                switch (this.hour.signum()) {
                                    case -1:
                                        this.signum = -1;
                                        this.hour = this.hour.negate();
                                        break;
                                    case 1:
                                        this.signum = 1;
                                        break;
                                    default:
                                        switch (this.minute.signum()) {
                                            case -1:
                                                this.signum = -1;
                                                this.minute = this.minute.negate();
                                                break;
                                            case 1:
                                                this.signum = 1;
                                                break;
                                            default:
                                                switch (this.second.signum()) {
                                                    case -1:
                                                        this.signum = -1;
                                                        this.second = this.second.negate();
                                                        break;
                                                    case 1:
                                                        this.signum = 1;
                                                        break;
                                                    default:
                                                        this.signum = 0;
                                                        break;
                                                }
                                                break;
                                        }
                                        break;
                                }
                                break;
                        }
                        break;
                }
                break;
        }
    }
}
