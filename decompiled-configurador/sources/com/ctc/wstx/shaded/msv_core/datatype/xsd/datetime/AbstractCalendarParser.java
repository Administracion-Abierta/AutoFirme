package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import java.math.BigInteger;
import java.util.SimpleTimeZone;
import org.spongycastle.asn1.eac.EACTags;
import org.spongycastle.crypto.tls.AlertDescription;
import org.spongycastle.crypto.tls.CipherSuite;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/datetime/AbstractCalendarParser.class */
abstract class AbstractCalendarParser {
    private final String format;
    private final String value;
    private final int flen;
    private final int vlen;
    private int fidx;
    protected int vidx;

    protected abstract void parseFractionSeconds();

    protected abstract void setTimeZone(java.util.TimeZone timeZone);

    protected abstract void setSeconds(int i);

    protected abstract void setMinutes(int i);

    protected abstract void setHours(int i);

    protected abstract void setDay(int i);

    protected abstract void setMonth(int i);

    protected abstract void setYear(int i);

    protected AbstractCalendarParser(String format, String value) {
        this.format = format;
        this.value = value;
        this.flen = format.length();
        this.vlen = value.length();
    }

    public void parse() throws IllegalArgumentException {
        while (this.fidx < this.flen) {
            String str = this.format;
            int i = this.fidx;
            this.fidx = i + 1;
            char fch = str.charAt(i);
            if (fch != '%') {
                skip(fch);
            } else {
                String str2 = this.format;
                int i2 = this.fidx;
                this.fidx = i2 + 1;
                switch (str2.charAt(i2)) {
                    case 'D':
                        setDay(parseInt(2, 2));
                        break;
                    case EACTags.INTEGRATED_CIRCUIT_MANUFACTURER_ID /* 77 */:
                        setMonth(parseInt(2, 2));
                        break;
                    case 'Y':
                        int sign = 1;
                        if (peek() == '-') {
                            this.vidx++;
                            sign = -1;
                        }
                        setYear(sign * parseInt(4, Integer.MAX_VALUE));
                        break;
                    case 'h':
                        setHours(parseInt(2, 2));
                        break;
                    case CipherSuite.TLS_DH_anon_WITH_AES_256_CBC_SHA256 /* 109 */:
                        setMinutes(parseInt(2, 2));
                        break;
                    case AlertDescription.unknown_psk_identity /* 115 */:
                        setSeconds(parseInt(2, 2));
                        if (peek() == '.') {
                            this.vidx++;
                            parseFractionSeconds();
                        }
                        break;
                    case 'z':
                        char vch = peek();
                        if (vch == 'Z') {
                            this.vidx++;
                            setTimeZone(TimeZone.ZERO);
                        } else if (vch == '+' || vch == '-') {
                            this.vidx++;
                            int h = parseInt(2, 2);
                            skip(':');
                            int m = parseInt(2, 2);
                            setTimeZone(new SimpleTimeZone(((h * 60) + m) * (vch == '+' ? 1 : -1) * 60 * 1000, ""));
                        } else {
                            setTimeZone(TimeZone.MISSING);
                        }
                        break;
                    default:
                        throw new InternalError();
                }
            }
        }
        if (this.vidx != this.vlen) {
            throw new IllegalArgumentException(this.value);
        }
    }

    private char peek() throws IllegalArgumentException {
        if (this.vidx == this.vlen) {
            return (char) 65535;
        }
        return this.value.charAt(this.vidx);
    }

    private char read() throws IllegalArgumentException {
        if (this.vidx == this.vlen) {
            throw new IllegalArgumentException(this.value);
        }
        String str = this.value;
        int i = this.vidx;
        this.vidx = i + 1;
        return str.charAt(i);
    }

    private void skip(char ch) throws IllegalArgumentException {
        if (read() != ch) {
            throw new IllegalArgumentException(this.value);
        }
    }

    protected final void skipDigits() {
        while (isDigit(peek())) {
            this.vidx++;
        }
    }

    protected final int parseInt(int minDigits, int maxDigits) throws IllegalArgumentException {
        int vstart = this.vidx;
        while (isDigit(peek()) && this.vidx - vstart < maxDigits) {
            this.vidx++;
        }
        if (this.vidx - vstart < minDigits) {
            throw new IllegalArgumentException(this.value);
        }
        return Integer.parseInt(this.value.substring(vstart, this.vidx));
    }

    protected final BigInteger parseBigInteger(int minDigits, int maxDigits) throws IllegalArgumentException {
        int vstart = this.vidx;
        while (isDigit(peek()) && this.vidx - vstart <= maxDigits) {
            this.vidx++;
        }
        if (this.vidx - vstart < minDigits) {
            throw new IllegalArgumentException(this.value);
        }
        return new BigInteger(this.value.substring(vstart, this.vidx));
    }

    private static boolean isDigit(char ch) {
        return '0' <= ch && ch <= '9';
    }
}
