package org.spongycastle.asn1.cmp;

import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cmp/CertConfirmContent.class */
public class CertConfirmContent extends ASN1Object {
    private ASN1Sequence content;

    private CertConfirmContent(ASN1Sequence seq) {
        this.content = seq;
    }

    public static CertConfirmContent getInstance(Object o) {
        if (o instanceof CertConfirmContent) {
            return (CertConfirmContent) o;
        }
        if (o != null) {
            return new CertConfirmContent(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public CertStatus[] toCertStatusArray() {
        CertStatus[] result = new CertStatus[this.content.size()];
        for (int i = 0; i != result.length; i++) {
            result[i] = CertStatus.getInstance(this.content.getObjectAt(i));
        }
        return result;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}
