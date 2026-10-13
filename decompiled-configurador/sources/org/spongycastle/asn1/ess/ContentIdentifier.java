package org.spongycastle.asn1.ess;

import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DEROctetString;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/ess/ContentIdentifier.class */
public class ContentIdentifier extends ASN1Object {
    ASN1OctetString value;

    public static ContentIdentifier getInstance(Object o) {
        if (o instanceof ContentIdentifier) {
            return (ContentIdentifier) o;
        }
        if (o != null) {
            return new ContentIdentifier(ASN1OctetString.getInstance(o));
        }
        return null;
    }

    private ContentIdentifier(ASN1OctetString value) {
        this.value = value;
    }

    public ContentIdentifier(byte[] value) {
        this(new DEROctetString(value));
    }

    public ASN1OctetString getValue() {
        return this.value;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.value;
    }
}
