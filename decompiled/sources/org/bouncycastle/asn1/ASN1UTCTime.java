package org.bouncycastle.asn1;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ASN1UTCTime.class */
public class ASN1UTCTime extends ASN1Primitive {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1UTCTime.class, 23) { // from class: org.bouncycastle.asn1.ASN1UTCTime.1
        @Override // org.bouncycastle.asn1.ASN1UniversalType
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1UTCTime.createPrimitive(dEROctetString.getOctets());
        }
    };
    final byte[] contents;

    public static ASN1UTCTime getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1UTCTime)) {
            return (ASN1UTCTime) obj;
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive aSN1Primitive = ((ASN1Encodable) obj).toASN1Primitive();
            if (aSN1Primitive instanceof ASN1UTCTime) {
                return (ASN1UTCTime) aSN1Primitive;
            }
        }
        if (!(obj instanceof byte[])) {
            throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
        }
        try {
            return (ASN1UTCTime) TYPE.fromByteArray((byte[]) obj);
        } catch (Exception e) {
            throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
        }
    }

    public static ASN1UTCTime getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return (ASN1UTCTime) TYPE.getContextInstance(aSN1TaggedObject, z);
    }

    public ASN1UTCTime(String str) {
        this.contents = Strings.toByteArray(str);
        try {
            getDate();
        } catch (ParseException e) {
            throw new IllegalArgumentException("invalid date string: " + e.getMessage());
        }
    }

    public ASN1UTCTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss'Z'", LocaleUtil.EN_Locale);
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.contents = Strings.toByteArray(simpleDateFormat.format(date));
    }

    public ASN1UTCTime(Date date, Locale locale) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss'Z'", locale);
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.contents = Strings.toByteArray(simpleDateFormat.format(date));
    }

    ASN1UTCTime(byte[] bArr) {
        if (bArr.length < 2) {
            throw new IllegalArgumentException("UTCTime string too short");
        }
        this.contents = bArr;
        if (!isDigit(0) || !isDigit(1)) {
            throw new IllegalArgumentException("illegal characters in UTCTime string");
        }
    }

    public Date getDate() throws ParseException {
        return new SimpleDateFormat("yyMMddHHmmssz", LocaleUtil.EN_Locale).parse(getTime());
    }

    public Date getAdjustedDate() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssz", LocaleUtil.EN_Locale);
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        return simpleDateFormat.parse(getAdjustedTime());
    }

    public String getTime() {
        String strFromByteArray = Strings.fromByteArray(this.contents);
        if (strFromByteArray.indexOf(45) < 0 && strFromByteArray.indexOf(43) < 0) {
            return strFromByteArray.length() == 11 ? strFromByteArray.substring(0, 10) + "00GMT+00:00" : strFromByteArray.substring(0, 12) + "GMT+00:00";
        }
        int iIndexOf = strFromByteArray.indexOf(45);
        if (iIndexOf < 0) {
            iIndexOf = strFromByteArray.indexOf(43);
        }
        String str = strFromByteArray;
        if (iIndexOf == strFromByteArray.length() - 3) {
            str = str + "00";
        }
        return iIndexOf == 10 ? str.substring(0, 10) + "00GMT" + str.substring(10, 13) + ":" + str.substring(13, 15) : str.substring(0, 12) + "GMT" + str.substring(12, 15) + ":" + str.substring(15, 17);
    }

    public String getAdjustedTime() {
        String time = getTime();
        return time.charAt(0) < '5' ? "20" + time : "19" + time;
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
        aSN1OutputStream.writeEncodingDL(z, 23, this.contents);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (aSN1Primitive instanceof ASN1UTCTime) {
            return Arrays.areEqual(this.contents, ((ASN1UTCTime) aSN1Primitive).contents);
        }
        return false;
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive, org.bouncycastle.asn1.ASN1Object
    public int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    public String toString() {
        return Strings.fromByteArray(this.contents);
    }

    static ASN1UTCTime createPrimitive(byte[] bArr) {
        return new ASN1UTCTime(bArr);
    }
}
