package org.spongycastle.asn1;

import java.util.Date;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/DERUTCTime.class */
public class DERUTCTime extends ASN1UTCTime {
    DERUTCTime(byte[] bytes) {
        super(bytes);
    }

    public DERUTCTime(Date time) {
        super(time);
    }

    public DERUTCTime(String time) {
        super(time);
    }
}
