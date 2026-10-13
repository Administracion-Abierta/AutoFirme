package org.bouncycastle.asn1;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;
import org.slf4j.Marker;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ASN1GeneralizedTime.class */
public class ASN1GeneralizedTime extends ASN1Primitive {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1GeneralizedTime.class, 24) { // from class: org.bouncycastle.asn1.ASN1GeneralizedTime.1
        @Override // org.bouncycastle.asn1.ASN1UniversalType
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1GeneralizedTime.createPrimitive(dEROctetString.getOctets());
        }
    };
    final byte[] contents;

    public static ASN1GeneralizedTime getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1GeneralizedTime)) {
            return (ASN1GeneralizedTime) obj;
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive aSN1Primitive = ((ASN1Encodable) obj).toASN1Primitive();
            if (aSN1Primitive instanceof ASN1GeneralizedTime) {
                return (ASN1GeneralizedTime) aSN1Primitive;
            }
        }
        if (!(obj instanceof byte[])) {
            throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
        }
        try {
            return (ASN1GeneralizedTime) TYPE.fromByteArray((byte[]) obj);
        } catch (Exception e) {
            throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
        }
    }

    public static ASN1GeneralizedTime getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return (ASN1GeneralizedTime) TYPE.getContextInstance(aSN1TaggedObject, z);
    }

    public ASN1GeneralizedTime(String str) {
        this.contents = Strings.toByteArray(str);
        try {
            getDate();
        } catch (ParseException e) {
            throw new IllegalArgumentException("invalid date string: " + e.getMessage());
        }
    }

    public ASN1GeneralizedTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss'Z'", LocaleUtil.EN_Locale);
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.contents = Strings.toByteArray(simpleDateFormat.format(date));
    }

    public ASN1GeneralizedTime(Date date, Locale locale) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss'Z'", locale);
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.contents = Strings.toByteArray(simpleDateFormat.format(date));
    }

    ASN1GeneralizedTime(byte[] bArr) {
        if (bArr.length < 4) {
            throw new IllegalArgumentException("GeneralizedTime string too short");
        }
        this.contents = bArr;
        if (!isDigit(0) || !isDigit(1) || !isDigit(2) || !isDigit(3)) {
            throw new IllegalArgumentException("illegal characters in GeneralizedTime string");
        }
    }

    public String getTimeString() {
        return Strings.fromByteArray(this.contents);
    }

    public String getTime() {
        String strFromByteArray = Strings.fromByteArray(this.contents);
        if (strFromByteArray.charAt(strFromByteArray.length() - 1) == 'Z') {
            return strFromByteArray.substring(0, strFromByteArray.length() - 1) + "GMT+00:00";
        }
        int length = strFromByteArray.length() - 6;
        char cCharAt = strFromByteArray.charAt(length);
        if ((cCharAt == '-' || cCharAt == '+') && strFromByteArray.indexOf("GMT") == length - 3) {
            return strFromByteArray;
        }
        int length2 = strFromByteArray.length() - 5;
        char cCharAt2 = strFromByteArray.charAt(length2);
        if (cCharAt2 == '-' || cCharAt2 == '+') {
            return strFromByteArray.substring(0, length2) + "GMT" + strFromByteArray.substring(length2, length2 + 3) + ":" + strFromByteArray.substring(length2 + 3);
        }
        int length3 = strFromByteArray.length() - 3;
        char cCharAt3 = strFromByteArray.charAt(length3);
        return (cCharAt3 == '-' || cCharAt3 == '+') ? strFromByteArray.substring(0, length3) + "GMT" + strFromByteArray.substring(length3) + ":00" : strFromByteArray + calculateGMTOffset(strFromByteArray);
    }

    private String calculateGMTOffset(String str) {
        String str2 = Marker.ANY_NON_NULL_MARKER;
        TimeZone timeZone = TimeZone.getDefault();
        int rawOffset = timeZone.getRawOffset();
        if (rawOffset < 0) {
            str2 = "-";
            rawOffset = -rawOffset;
        }
        int i = rawOffset / 3600000;
        int i2 = (rawOffset - (((i * 60) * 60) * 1000)) / 60000;
        try {
            if (timeZone.useDaylightTime()) {
                if (hasFractionalSeconds()) {
                    str = pruneFractionalSeconds(str);
                }
                if (timeZone.inDaylightTime(calculateGMTDateFormat().parse(str + "GMT" + str2 + convert(i) + ":" + convert(i2)))) {
                    i += str2.equals(Marker.ANY_NON_NULL_MARKER) ? 1 : -1;
                }
            }
        } catch (ParseException e) {
        }
        return "GMT" + str2 + convert(i) + ":" + convert(i2);
    }

    private SimpleDateFormat calculateGMTDateFormat() {
        SimpleDateFormat simpleDateFormat;
        if (hasFractionalSeconds()) {
            simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss.SSSz");
        } else if (hasSeconds()) {
            simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssz");
        } else {
            simpleDateFormat = hasMinutes() ? new SimpleDateFormat("yyyyMMddHHmmz") : new SimpleDateFormat("yyyyMMddHHz");
        }
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        return simpleDateFormat;
    }

    private String pruneFractionalSeconds(String str) {
        char cCharAt;
        String strSubstring = str.substring(14);
        int i = 1;
        while (i < strSubstring.length() && '0' <= (cCharAt = strSubstring.charAt(i)) && cCharAt <= '9') {
            i++;
        }
        if (i - 1 > 3) {
            str = str.substring(0, 14) + (strSubstring.substring(0, 4) + strSubstring.substring(i));
        } else if (i - 1 == 1) {
            str = str.substring(0, 14) + (strSubstring.substring(0, i) + "00" + strSubstring.substring(i));
        } else if (i - 1 == 2) {
            str = str.substring(0, 14) + (strSubstring.substring(0, i) + "0" + strSubstring.substring(i));
        }
        return str;
    }

    private String convert(int i) {
        return i < 10 ? "0" + i : Integer.toString(i);
    }

    public Date getDate() throws ParseException {
        SimpleDateFormat simpleDateFormatCalculateGMTDateFormat;
        String strFromByteArray = Strings.fromByteArray(this.contents);
        String time = strFromByteArray;
        if (strFromByteArray.endsWith("Z")) {
            if (hasFractionalSeconds()) {
                simpleDateFormatCalculateGMTDateFormat = new SimpleDateFormat("yyyyMMddHHmmss.SSS'Z'", LocaleUtil.EN_Locale);
            } else if (hasSeconds()) {
                simpleDateFormatCalculateGMTDateFormat = new SimpleDateFormat("yyyyMMddHHmmss'Z'", LocaleUtil.EN_Locale);
            } else {
                simpleDateFormatCalculateGMTDateFormat = hasMinutes() ? new SimpleDateFormat("yyyyMMddHHmm'Z'", LocaleUtil.EN_Locale) : new SimpleDateFormat("yyyyMMddHH'Z'", LocaleUtil.EN_Locale);
            }
            simpleDateFormatCalculateGMTDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        } else if (strFromByteArray.indexOf(45) > 0 || strFromByteArray.indexOf(43) > 0) {
            time = getTime();
            simpleDateFormatCalculateGMTDateFormat = calculateGMTDateFormat();
        } else {
            if (hasFractionalSeconds()) {
                simpleDateFormatCalculateGMTDateFormat = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
            } else if (hasSeconds()) {
                simpleDateFormatCalculateGMTDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            } else {
                simpleDateFormatCalculateGMTDateFormat = hasMinutes() ? new SimpleDateFormat("yyyyMMddHHmm") : new SimpleDateFormat("yyyyMMddHH");
            }
            simpleDateFormatCalculateGMTDateFormat.setTimeZone(new SimpleTimeZone(0, TimeZone.getDefault().getID()));
        }
        if (hasFractionalSeconds()) {
            time = pruneFractionalSeconds(time);
        }
        return simpleDateFormatCalculateGMTDateFormat.parse(time);
    }

    protected boolean hasFractionalSeconds() {
        for (int i = 0; i != this.contents.length; i++) {
            if (this.contents[i] == 46 && i == 14) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasSeconds() {
        return isDigit(12) && isDigit(13);
    }

    protected boolean hasMinutes() {
        return isDigit(10) && isDigit(11);
    }

    private boolean isDigit(int i) {
        return this.contents.length > i && this.contents[i] >= 48 && this.contents[i] <= 57;
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    final boolean encodeConstructed() {
        return false;
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    int encodedLength(boolean z) {
        return ASN1OutputStream.getLengthOfEncodingDL(z, this.contents.length);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    void encode(ASN1OutputStream aSN1OutputStream, boolean z) throws IOException {
        aSN1OutputStream.writeEncodingDL(z, 24, this.contents);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    ASN1Primitive toDERObject() {
        return new DERGeneralizedTime(this.contents);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (aSN1Primitive instanceof ASN1GeneralizedTime) {
            return Arrays.areEqual(this.contents, ((ASN1GeneralizedTime) aSN1Primitive).contents);
        }
        return false;
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive, org.bouncycastle.asn1.ASN1Object
    public int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    static ASN1GeneralizedTime createPrimitive(byte[] bArr) {
        return new ASN1GeneralizedTime(bArr);
    }
}
