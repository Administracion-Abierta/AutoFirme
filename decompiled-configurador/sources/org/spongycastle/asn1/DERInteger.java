package org.spongycastle.asn1;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/DERInteger.class */
public class DERInteger extends ASN1Integer {
    public DERInteger(byte[] bytes) {
        super(bytes, true);
    }

    public DERInteger(BigInteger value) {
        super(value);
    }

    public DERInteger(long value) {
        super(value);
    }
}
