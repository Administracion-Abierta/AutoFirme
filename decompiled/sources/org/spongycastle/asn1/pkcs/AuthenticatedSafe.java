package org.spongycastle.asn1.pkcs;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.BERSequence;
import org.spongycastle.asn1.DLSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/pkcs/AuthenticatedSafe.class */
public class AuthenticatedSafe extends ASN1Object {
    private ContentInfo[] info;
    private boolean isBer;

    private AuthenticatedSafe(ASN1Sequence seq) {
        this.isBer = true;
        this.info = new ContentInfo[seq.size()];
        for (int i = 0; i != this.info.length; i++) {
            this.info[i] = ContentInfo.getInstance(seq.getObjectAt(i));
        }
        this.isBer = seq instanceof BERSequence;
    }

    public static AuthenticatedSafe getInstance(Object o) {
        if (o instanceof AuthenticatedSafe) {
            return (AuthenticatedSafe) o;
        }
        if (o != null) {
            return new AuthenticatedSafe(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public AuthenticatedSafe(ContentInfo[] info) {
        this.isBer = true;
        this.info = info;
    }

    public ContentInfo[] getContentInfo() {
        return this.info;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        for (int i = 0; i != this.info.length; i++) {
            v.add(this.info[i]);
        }
        if (this.isBer) {
            return new BERSequence(v);
        }
        return new DLSequence(v);
    }
}
