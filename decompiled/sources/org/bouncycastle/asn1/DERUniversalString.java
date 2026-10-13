package org.bouncycastle.asn1;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/DERUniversalString.class */
public class DERUniversalString extends ASN1UniversalString {
    public DERUniversalString(byte[] bArr) {
        this(bArr, true);
    }

    DERUniversalString(byte[] bArr, boolean z) {
        super(bArr, z);
    }
}
