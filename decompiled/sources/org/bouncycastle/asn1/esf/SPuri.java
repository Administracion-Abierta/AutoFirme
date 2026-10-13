package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Primitive;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/esf/SPuri.class */
public class SPuri {
    private ASN1IA5String uri;

    public static SPuri getInstance(Object obj) {
        if (obj instanceof SPuri) {
            return (SPuri) obj;
        }
        if (obj instanceof ASN1IA5String) {
            return new SPuri(ASN1IA5String.getInstance(obj));
        }
        return null;
    }

    public SPuri(ASN1IA5String aSN1IA5String) {
        this.uri = aSN1IA5String;
    }

    public ASN1IA5String getUriIA5() {
        return this.uri;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.uri.toASN1Primitive();
    }
}
