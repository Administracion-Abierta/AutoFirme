package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERIA5String;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/Url.class */
public class Url extends ASN1Object {
    private final String url;

    public Url(String str) {
        this.url = str;
    }

    private Url(ASN1IA5String aSN1IA5String) {
        this.url = aSN1IA5String.getString();
    }

    public static Url getInstance(Object obj) {
        if (obj instanceof Url) {
            return (Url) obj;
        }
        if (obj != null) {
            return new Url(ASN1IA5String.getInstance(obj));
        }
        return null;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERIA5String(this.url);
    }

    public String getUrl() {
        return this.url;
    }
}
