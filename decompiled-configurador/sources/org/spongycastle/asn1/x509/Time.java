package org.spongycastle.asn1.x509;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import org.spongycastle.asn1.ASN1Choice;
import org.spongycastle.asn1.ASN1GeneralizedTime;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.ASN1UTCTime;
import org.spongycastle.asn1.DERGeneralizedTime;
import org.spongycastle.asn1.DERUTCTime;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/x509/Time.class */
public class Time extends ASN1Object implements ASN1Choice {
    ASN1Primitive time;

    public static Time getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(obj.getObject());
    }

    public Time(ASN1Primitive time) {
        if (!(time instanceof ASN1UTCTime) && !(time instanceof ASN1GeneralizedTime)) {
            throw new IllegalArgumentException("unknown object passed to Time");
        }
        this.time = time;
    }

    public Time(Date time) {
        SimpleTimeZone tz = new SimpleTimeZone(0, "Z");
        SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMddHHmmss");
        dateF.setTimeZone(tz);
        String d = dateF.format(time) + "Z";
        int year = Integer.parseInt(d.substring(0, 4));
        if (year < 1950 || year > 2049) {
            this.time = new DERGeneralizedTime(d);
        } else {
            this.time = new DERUTCTime(d.substring(2));
        }
    }

    public Time(Date time, Locale locale) {
        SimpleTimeZone tz = new SimpleTimeZone(0, "Z");
        SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMddHHmmss", locale);
        dateF.setTimeZone(tz);
        String d = dateF.format(time) + "Z";
        int year = Integer.parseInt(d.substring(0, 4));
        if (year < 1950 || year > 2049) {
            this.time = new DERGeneralizedTime(d);
        } else {
            this.time = new DERUTCTime(d.substring(2));
        }
    }

    public static Time getInstance(Object obj) {
        if (obj == null || (obj instanceof Time)) {
            return (Time) obj;
        }
        if (obj instanceof ASN1UTCTime) {
            return new Time((ASN1UTCTime) obj);
        }
        if (obj instanceof ASN1GeneralizedTime) {
            return new Time((ASN1GeneralizedTime) obj);
        }
        throw new IllegalArgumentException("unknown object in factory: " + obj.getClass().getName());
    }

    public String getTime() {
        if (this.time instanceof ASN1UTCTime) {
            return ((ASN1UTCTime) this.time).getAdjustedTime();
        }
        return ((ASN1GeneralizedTime) this.time).getTime();
    }

    public Date getDate() {
        try {
            if (this.time instanceof ASN1UTCTime) {
                return ((ASN1UTCTime) this.time).getAdjustedDate();
            }
            return ((ASN1GeneralizedTime) this.time).getDate();
        } catch (ParseException e) {
            throw new IllegalStateException("invalid date string: " + e.getMessage());
        }
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.time;
    }

    public String toString() {
        return getTime();
    }
}
