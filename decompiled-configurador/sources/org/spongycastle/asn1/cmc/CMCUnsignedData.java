package org.spongycastle.asn1.cmc;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cmc/CMCUnsignedData.class */
public class CMCUnsignedData extends ASN1Object {
    private final BodyPartPath bodyPartPath;
    private final ASN1ObjectIdentifier identifier;
    private final ASN1Encodable content;

    public CMCUnsignedData(BodyPartPath bodyPartPath, ASN1ObjectIdentifier identifier, ASN1Encodable content) {
        this.bodyPartPath = bodyPartPath;
        this.identifier = identifier;
        this.content = content;
    }

    private CMCUnsignedData(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.bodyPartPath = BodyPartPath.getInstance(seq.getObjectAt(0));
        this.identifier = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(1));
        this.content = seq.getObjectAt(2);
    }

    public static CMCUnsignedData getInstance(Object o) {
        if (o instanceof CMCUnsignedData) {
            return (CMCUnsignedData) o;
        }
        if (o != null) {
            return new CMCUnsignedData(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.bodyPartPath);
        v.add(this.identifier);
        v.add(this.content);
        return new DERSequence(v);
    }

    public BodyPartPath getBodyPartPath() {
        return this.bodyPartPath;
    }

    public ASN1ObjectIdentifier getIdentifier() {
        return this.identifier;
    }

    public ASN1Encodable getContent() {
        return this.content;
    }
}
