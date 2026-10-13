package org.spongycastle.asn1.eac;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/eac/PackedDate.class */
public class PackedDate {
    private byte[] time;

    public PackedDate(String time) {
        this.time = convert(time);
    }

    public PackedDate(Date time) {
        SimpleDateFormat dateF = new SimpleDateFormat("yyMMdd'Z'");
        dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.time = convert(dateF.format(time));
    }

    public PackedDate(Date time, Locale locale) {
        SimpleDateFormat dateF = new SimpleDateFormat("yyMMdd'Z'", locale);
        dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.time = convert(dateF.format(time));
    }

    private byte[] convert(String sTime) {
        char[] digs = sTime.toCharArray();
        byte[] date = new byte[6];
        for (int i = 0; i != 6; i++) {
            date[i] = (byte) (digs[i] - '0');
        }
        return date;
    }

    PackedDate(byte[] bytes) {
        this.time = bytes;
    }

    public Date getDate() throws ParseException {
        SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMdd");
        return dateF.parse("20" + toString());
    }

    public int hashCode() {
        return Arrays.hashCode(this.time);
    }

    public boolean equals(Object o) {
        if (!(o instanceof PackedDate)) {
            return false;
        }
        PackedDate other = (PackedDate) o;
        return Arrays.areEqual(this.time, other.time);
    }

    public String toString() {
        char[] dateC = new char[this.time.length];
        for (int i = 0; i != dateC.length; i++) {
            dateC[i] = (char) ((this.time[i] & 255) + 48);
        }
        return new String(dateC);
    }

    public byte[] getEncoding() {
        return Arrays.clone(this.time);
    }
}
