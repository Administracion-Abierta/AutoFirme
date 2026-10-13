package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DLSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/pkcs/AuthenticatedSafe.class */
public class AuthenticatedSafe extends ASN1Object {
    private ContentInfo[] info;
    private boolean isBer;

    private AuthenticatedSafe(ASN1Sequence aSN1Sequence) {
        this.isBer = true;
        this.info = new ContentInfo[aSN1Sequence.size()];
        for (int i = 0; i != this.info.length; i++) {
            this.info[i] = ContentInfo.getInstance(aSN1Sequence.getObjectAt(i));
        }
        this.isBer = aSN1Sequence instanceof BERSequence;
    }

    public static AuthenticatedSafe getInstance(Object obj) {
        if (obj instanceof AuthenticatedSafe) {
            return (AuthenticatedSafe) obj;
        }
        if (obj != null) {
            return new AuthenticatedSafe(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public AuthenticatedSafe(ContentInfo[] contentInfoArr) {
        this.isBer = true;
        this.info = copy(contentInfoArr);
    }

    public ContentInfo[] getContentInfo() {
        return copy(this.info);
    }

    private ContentInfo[] copy(ContentInfo[] contentInfoArr) {
        ContentInfo[] contentInfoArr2 = new ContentInfo[contentInfoArr.length];
        System.arraycopy(contentInfoArr, 0, contentInfoArr2, 0, contentInfoArr2.length);
        return contentInfoArr2;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.isBer ? new BERSequence(this.info) : new DLSequence(this.info);
    }
}
