package org.spongycastle.asn1.misc;

import org.spongycastle.asn1.DERIA5String;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/misc/NetscapeRevocationURL.class */
public class NetscapeRevocationURL extends DERIA5String {
    public NetscapeRevocationURL(DERIA5String str) {
        super(str.getString());
    }

    @Override // org.spongycastle.asn1.DERIA5String
    public String toString() {
        return "NetscapeRevocationURL: " + getString();
    }
}
