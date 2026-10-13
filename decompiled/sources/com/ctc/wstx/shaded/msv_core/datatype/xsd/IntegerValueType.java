package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import java.io.Serializable;
import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/IntegerValueType.class */
public class IntegerValueType extends Number implements Comparable, Serializable {
    private final String value;
    private static final long serialVersionUID = 1;

    private IntegerValueType(String canonicalizedValue) {
        this.value = canonicalizedValue;
    }

    private IntegerValueType(long v) {
        this.value = Long.toString(v);
    }

    public static IntegerValueType create(BigInteger bi) {
        return create(bi.toString());
    }

    public static IntegerValueType create(String nonCanonicalizedValue) {
        int idx = 0;
        String v = "";
        int len = nonCanonicalizedValue.length();
        if (len == 0) {
            return null;
        }
        switch (nonCanonicalizedValue.charAt(0)) {
            case '+':
                idx = 0 + 1;
                break;
            case ',':
            case '.':
            case '/':
            default:
                return null;
            case '-':
                v = v + '-';
                idx = 0 + 1;
                break;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                break;
        }
        if (idx == len) {
            return null;
        }
        while (idx < len && nonCanonicalizedValue.charAt(idx) == '0') {
            idx++;
        }
        if (idx == len) {
            return new IntegerValueType("0");
        }
        while (idx < len) {
            int i = idx;
            idx++;
            char ch = nonCanonicalizedValue.charAt(i);
            if ('0' <= ch && ch <= '9') {
                v = v + ch;
            } else {
                return null;
            }
        }
        return new IntegerValueType(v);
    }

    @Override // java.lang.Comparable
    public int compareTo(Object o) {
        IntegerValueType rhs;
        int rp;
        int lp;
        if (o instanceof IntegerValueType) {
            rhs = (IntegerValueType) o;
        } else {
            rhs = new IntegerValueType(((Number) o).longValue());
        }
        boolean lhsIsNegative = this.value.charAt(0) == '-';
        boolean rhsIsNegative = rhs.value.charAt(0) == '-';
        if (lhsIsNegative && !rhsIsNegative) {
            return -1;
        }
        if (rhsIsNegative && !lhsIsNegative) {
            return 1;
        }
        if (lhsIsNegative && rhsIsNegative) {
            rp = 1;
            lp = 1;
        } else {
            rp = 0;
            lp = 0;
        }
        int llen = this.value.length() - lp;
        int rlen = rhs.value.length() - rp;
        if (llen > rlen) {
            return lhsIsNegative ? -1 : 1;
        }
        if (llen < rlen) {
            return lhsIsNegative ? 1 : -1;
        }
        while (llen > 0) {
            int i = lp;
            lp++;
            char lch = this.value.charAt(i);
            int i2 = rp;
            rp++;
            char rch = rhs.value.charAt(i2);
            if (lch > rch) {
                return lhsIsNegative ? -1 : 1;
            }
            if (lch < rch) {
                return lhsIsNegative ? 1 : -1;
            }
            llen--;
        }
        return 0;
    }

    public boolean equals(Object o) {
        if (o instanceof IntegerValueType) {
            return this.value.equals(((IntegerValueType) o).value);
        }
        return false;
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public String toString() {
        return this.value;
    }

    public int precision() {
        int len = this.value.length();
        if (this.value.charAt(0) == '-') {
            return len - 1;
        }
        return len;
    }

    public boolean isNonPositive() {
        char ch = this.value.charAt(0);
        if (ch == '-' || ch == '0') {
            return true;
        }
        return false;
    }

    public boolean isPositive() {
        char ch = this.value.charAt(0);
        if (ch == '-' || ch == '0') {
            return false;
        }
        return true;
    }

    public boolean isNegative() {
        return this.value.charAt(0) == '-';
    }

    public boolean isNonNegative() {
        return this.value.charAt(0) != '-';
    }

    public BigInteger toBigInteger() {
        return new BigInteger(this.value);
    }

    @Override // java.lang.Number
    public double doubleValue() {
        return toBigInteger().doubleValue();
    }

    @Override // java.lang.Number
    public float floatValue() {
        return (float) doubleValue();
    }

    @Override // java.lang.Number
    public int intValue() {
        return toBigInteger().intValue();
    }

    @Override // java.lang.Number
    public long longValue() {
        return toBigInteger().longValue();
    }
}
