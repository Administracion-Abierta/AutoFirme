package org.spongycastle.asn1.cms;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.BERSequence;
import org.spongycastle.asn1.BERTaggedObject;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cms/EncryptedData.class */
public class EncryptedData extends ASN1Object {
    private ASN1Integer version;
    private EncryptedContentInfo encryptedContentInfo;
    private ASN1Set unprotectedAttrs;

    public static EncryptedData getInstance(Object o) {
        if (o instanceof EncryptedData) {
            return (EncryptedData) o;
        }
        if (o != null) {
            return new EncryptedData(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public EncryptedData(EncryptedContentInfo encInfo) {
        this(encInfo, null);
    }

    public EncryptedData(EncryptedContentInfo encInfo, ASN1Set unprotectedAttrs) {
        this.version = new ASN1Integer(unprotectedAttrs == null ? 0L : 2L);
        this.encryptedContentInfo = encInfo;
        this.unprotectedAttrs = unprotectedAttrs;
    }

    private EncryptedData(ASN1Sequence seq) {
        this.version = ASN1Integer.getInstance(seq.getObjectAt(0));
        this.encryptedContentInfo = EncryptedContentInfo.getInstance(seq.getObjectAt(1));
        if (seq.size() == 3) {
            this.unprotectedAttrs = ASN1Set.getInstance((ASN1TaggedObject) seq.getObjectAt(2), false);
        }
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public EncryptedContentInfo getEncryptedContentInfo() {
        return this.encryptedContentInfo;
    }

    public ASN1Set getUnprotectedAttrs() {
        return this.unprotectedAttrs;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.version);
        v.add(this.encryptedContentInfo);
        if (this.unprotectedAttrs != null) {
            v.add(new BERTaggedObject(false, 1, this.unprotectedAttrs));
        }
        return new BERSequence(v);
    }
}
