package org.spongycastle.asn1.misc;

import org.spongycastle.asn1.DERIA5String;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/misc/VerisignCzagExtension.class */
public class VerisignCzagExtension extends DERIA5String {
    public VerisignCzagExtension(DERIA5String str) {
        super(str.getString());
    }

    @Override // org.spongycastle.asn1.DERIA5String
    public String toString() {
        return "VerisignCzagExtension: " + getString();
    }
}
