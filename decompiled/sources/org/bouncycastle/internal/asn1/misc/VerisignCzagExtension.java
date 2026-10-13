package org.bouncycastle.internal.asn1.misc;

import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.DERIA5String;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/internal/asn1/misc/VerisignCzagExtension.class */
public class VerisignCzagExtension extends DERIA5String {
    public VerisignCzagExtension(ASN1IA5String aSN1IA5String) {
        super(aSN1IA5String.getString());
    }

    @Override // org.bouncycastle.asn1.ASN1IA5String
    public String toString() {
        return "VerisignCzagExtension: " + getString();
    }
}
