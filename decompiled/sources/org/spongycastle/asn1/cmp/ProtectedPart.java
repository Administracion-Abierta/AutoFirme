package org.spongycastle.asn1.cmp;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmp/ProtectedPart.class */
public class ProtectedPart extends ASN1Object {
    private PKIHeader header;
    private PKIBody body;

    private ProtectedPart(ASN1Sequence seq) {
        this.header = PKIHeader.getInstance(seq.getObjectAt(0));
        this.body = PKIBody.getInstance(seq.getObjectAt(1));
    }

    public static ProtectedPart getInstance(Object o) {
        if (o instanceof ProtectedPart) {
            return (ProtectedPart) o;
        }
        if (o != null) {
            return new ProtectedPart(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public ProtectedPart(PKIHeader header, PKIBody body) {
        this.header = header;
        this.body = body;
    }

    public PKIHeader getHeader() {
        return this.header;
    }

    public PKIBody getBody() {
        return this.body;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.header);
        v.add(this.body);
        return new DERSequence(v);
    }
}
