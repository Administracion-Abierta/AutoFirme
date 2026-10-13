package org.spongycastle.asn1.isismtt.x509;

import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.x500.DirectoryString;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/isismtt/x509/Restriction.class */
public class Restriction extends ASN1Object {
    private DirectoryString restriction;

    public static Restriction getInstance(Object obj) {
        if (obj instanceof Restriction) {
            return (Restriction) obj;
        }
        if (obj != null) {
            return new Restriction(DirectoryString.getInstance(obj));
        }
        return null;
    }

    private Restriction(DirectoryString restriction) {
        this.restriction = restriction;
    }

    public Restriction(String restriction) {
        this.restriction = new DirectoryString(restriction);
    }

    public DirectoryString getRestriction() {
        return this.restriction;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.restriction.toASN1Primitive();
    }
}
