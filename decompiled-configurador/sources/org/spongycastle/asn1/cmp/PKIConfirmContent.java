package org.spongycastle.asn1.cmp;

import org.spongycastle.asn1.ASN1Null;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DERNull;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cmp/PKIConfirmContent.class */
public class PKIConfirmContent extends ASN1Object {
    private ASN1Null val;

    private PKIConfirmContent(ASN1Null val) {
        this.val = val;
    }

    public static PKIConfirmContent getInstance(Object o) {
        if (o == null || (o instanceof PKIConfirmContent)) {
            return (PKIConfirmContent) o;
        }
        if (o instanceof ASN1Null) {
            return new PKIConfirmContent((ASN1Null) o);
        }
        throw new IllegalArgumentException("Invalid object: " + o.getClass().getName());
    }

    public PKIConfirmContent() {
        this.val = DERNull.INSTANCE;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.val;
    }
}
