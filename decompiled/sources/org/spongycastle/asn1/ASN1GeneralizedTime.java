package org.spongycastle.asn1;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import org.slf4j.Marker;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/ASN1GeneralizedTime.class */
public class ASN1GeneralizedTime extends ASN1Primitive {
    private byte[] time;

    public static ASN1GeneralizedTime getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1GeneralizedTime)) {
            return (ASN1GeneralizedTime) obj;
        }
        if (obj instanceof byte[]) {
            try {
                return (ASN1GeneralizedTime) fromByteArray((byte[]) obj);
            } catch (Exception e) {
                throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1GeneralizedTime getInstance(ASN1TaggedObject obj, boolean explicit) {
        ASN1Primitive o = obj.getObject();
        if (explicit || (o instanceof ASN1GeneralizedTime)) {
            return getInstance(o);
        }
        return new ASN1GeneralizedTime(((ASN1OctetString) o).getOctets());
    }

    public ASN1GeneralizedTime(String time) {
        this.time = Strings.toByteArray(time);
        try {
            getDate();
        } catch (ParseException e) {
            throw new IllegalArgumentException("invalid date string: " + e.getMessage());
        }
    }

    public ASN1GeneralizedTime(Date time) {
        SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
        dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.time = Strings.toByteArray(dateF.format(time));
    }

    public ASN1GeneralizedTime(Date time, Locale locale) {
        SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMddHHmmss'Z'", locale);
        dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.time = Strings.toByteArray(dateF.format(time));
    }

    ASN1GeneralizedTime(byte[] bytes) {
        this.time = bytes;
    }

    public String getTimeString() {
        return Strings.fromByteArray(this.time);
    }

    public String getTime() {
        String stime = Strings.fromByteArray(this.time);
        if (stime.charAt(stime.length() - 1) == 'Z') {
            return stime.substring(0, stime.length() - 1) + "GMT+00:00";
        }
        int signPos = stime.length() - 5;
        char sign = stime.charAt(signPos);
        if (sign == '-' || sign == '+') {
            return stime.substring(0, signPos) + "GMT" + stime.substring(signPos, signPos + 3) + ":" + stime.substring(signPos + 3);
        }
        int signPos2 = stime.length() - 3;
        char sign2 = stime.charAt(signPos2);
        if (sign2 == '-' || sign2 == '+') {
            return stime.substring(0, signPos2) + "GMT" + stime.substring(signPos2) + ":00";
        }
        return stime + calculateGMTOffset();
    }

    private String calculateGMTOffset() {
        String sign = Marker.ANY_NON_NULL_MARKER;
        TimeZone timeZone = TimeZone.getDefault();
        int offset = timeZone.getRawOffset();
        if (offset < 0) {
            sign = "-";
            offset = -offset;
        }
        int hours = offset / 3600000;
        int minutes = (offset - (((hours * 60) * 60) * 1000)) / 60000;
        try {
            if (timeZone.useDaylightTime() && timeZone.inDaylightTime(getDate())) {
                hours += sign.equals(Marker.ANY_NON_NULL_MARKER) ? 1 : -1;
            }
        } catch (ParseException e) {
        }
        return "GMT" + sign + convert(hours) + ":" + convert(minutes);
    }

    private String convert(int time) {
        if (time < 10) {
            return "0" + time;
        }
        return Integer.toString(time);
    }

    public Date getDate() throws ParseException {
        SimpleDateFormat dateF;
        char ch;
        String stime = Strings.fromByteArray(this.time);
        String d = stime;
        if (stime.endsWith("Z")) {
            if (hasFractionalSeconds()) {
                dateF = new SimpleDateFormat("yyyyMMddHHmmss.SSS'Z'");
            } else {
                dateF = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
            }
            dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
        } else if (stime.indexOf(45) > 0 || stime.indexOf(43) > 0) {
            d = getTime();
            if (hasFractionalSeconds()) {
                dateF = new SimpleDateFormat("yyyyMMddHHmmss.SSSz");
            } else {
                dateF = new SimpleDateFormat("yyyyMMddHHmmssz");
            }
            dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
        } else {
            if (hasFractionalSeconds()) {
                dateF = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
            } else {
                dateF = new SimpleDateFormat("yyyyMMddHHmmss");
            }
            dateF.setTimeZone(new SimpleTimeZone(0, TimeZone.getDefault().getID()));
        }
        if (hasFractionalSeconds()) {
            String frac = d.substring(14);
            int index = 1;
            while (index < frac.length() && '0' <= (ch = frac.charAt(index)) && ch <= '9') {
                index++;
            }
            if (index - 1 > 3) {
                d = d.substring(0, 14) + (frac.substring(0, 4) + frac.substring(index));
            } else if (index - 1 == 1) {
                d = d.substring(0, 14) + (frac.substring(0, index) + "00" + frac.substring(index));
            } else if (index - 1 == 2) {
                d = d.substring(0, 14) + (frac.substring(0, index) + "0" + frac.substring(index));
            }
        }
        return dateF.parse(d);
    }

    private boolean hasFractionalSeconds() {
        for (int i = 0; i != this.time.length; i++) {
            if (this.time[i] == 46 && i == 14) {
                return true;
            }
        }
        return false;
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    boolean isConstructed() {
        return false;
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    int encodedLength() {
        int length = this.time.length;
        return 1 + StreamUtil.calculateBodyLength(length) + length;
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    void encode(ASN1OutputStream out) throws IOException {
        out.writeEncoded(24, this.time);
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    boolean asn1Equals(ASN1Primitive o) {
        if (!(o instanceof ASN1GeneralizedTime)) {
            return false;
        }
        return Arrays.areEqual(this.time, ((ASN1GeneralizedTime) o).time);
    }

    @Override // org.spongycastle.asn1.ASN1Primitive, org.spongycastle.asn1.ASN1Object
    public int hashCode() {
        return Arrays.hashCode(this.time);
    }
}
