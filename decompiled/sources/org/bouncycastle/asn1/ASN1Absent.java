package org.bouncycastle.asn1;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ASN1Absent.class */
public class ASN1Absent extends ASN1Primitive {
    public static final ASN1Absent INSTANCE = new ASN1Absent();

    private ASN1Absent() {
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive, org.bouncycastle.asn1.ASN1Object
    public int hashCode() {
        return 0;
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    boolean encodeConstructed() {
        return false;
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    int encodedLength(boolean z) throws IOException {
        return 0;
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    void encode(ASN1OutputStream aSN1OutputStream, boolean z) throws IOException {
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        return aSN1Primitive == this;
    }
}
