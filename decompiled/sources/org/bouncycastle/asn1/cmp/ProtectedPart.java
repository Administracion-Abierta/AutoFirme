package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cmp/ProtectedPart.class */
public class ProtectedPart extends ASN1Object {
    private final PKIHeader header;
    private final PKIBody body;

    private ProtectedPart(ASN1Sequence aSN1Sequence) {
        this.header = PKIHeader.getInstance(aSN1Sequence.getObjectAt(0));
        this.body = PKIBody.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public ProtectedPart(PKIHeader pKIHeader, PKIBody pKIBody) {
        this.header = pKIHeader;
        this.body = pKIBody;
    }

    public static ProtectedPart getInstance(Object obj) {
        if (obj instanceof ProtectedPart) {
            return (ProtectedPart) obj;
        }
        if (obj != null) {
            return new ProtectedPart(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public PKIHeader getHeader() {
        return this.header;
    }

    public PKIBody getBody() {
        return this.body;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.header, this.body);
    }
}
