package org.bouncycastle.asn1.misc;

import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.DERIA5String;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/misc/NetscapeRevocationURL.class */
public class NetscapeRevocationURL extends DERIA5String {
    public NetscapeRevocationURL(ASN1IA5String aSN1IA5String) {
        super(aSN1IA5String.getString());
    }

    @Override // org.bouncycastle.asn1.ASN1IA5String
    public String toString() {
        return "NetscapeRevocationURL: " + getString();
    }
}
