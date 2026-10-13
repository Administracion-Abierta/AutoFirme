package org.bouncycastle.asn1;

import java.util.Date;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/DERUTCTime.class */
public class DERUTCTime extends ASN1UTCTime {
    DERUTCTime(byte[] bArr) {
        super(bArr);
    }

    public DERUTCTime(Date date) {
        super(date);
    }

    public DERUTCTime(String str) {
        super(str);
    }
}
