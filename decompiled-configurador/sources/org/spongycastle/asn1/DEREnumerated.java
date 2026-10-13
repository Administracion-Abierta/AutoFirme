package org.spongycastle.asn1;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/DEREnumerated.class */
public class DEREnumerated extends ASN1Enumerated {
    DEREnumerated(byte[] bytes) {
        super(bytes);
    }

    public DEREnumerated(BigInteger value) {
        super(value);
    }

    public DEREnumerated(int value) {
        super(value);
    }
}
