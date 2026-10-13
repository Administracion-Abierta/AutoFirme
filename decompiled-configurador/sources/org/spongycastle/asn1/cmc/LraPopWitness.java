package org.spongycastle.asn1.cmc;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cmc/LraPopWitness.class */
public class LraPopWitness extends ASN1Object {
    private final BodyPartID pkiDataBodyid;
    private final ASN1Sequence bodyIds;

    public LraPopWitness(BodyPartID pkiDataBodyid, ASN1Sequence bodyIds) {
        this.pkiDataBodyid = pkiDataBodyid;
        this.bodyIds = bodyIds;
    }

    private LraPopWitness(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.pkiDataBodyid = BodyPartID.getInstance(seq.getObjectAt(0));
        this.bodyIds = ASN1Sequence.getInstance(seq.getObjectAt(1));
    }

    public static LraPopWitness getInstance(Object o) {
        if (o instanceof LraPopWitness) {
            return (LraPopWitness) o;
        }
        if (o != null) {
            return new LraPopWitness(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public BodyPartID getPkiDataBodyid() {
        return this.pkiDataBodyid;
    }

    public BodyPartID[] getBodyIds() {
        BodyPartID[] rv = new BodyPartID[this.bodyIds.size()];
        for (int i = 0; i != this.bodyIds.size(); i++) {
            rv[i] = BodyPartID.getInstance(this.bodyIds.getObjectAt(i));
        }
        return rv;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.pkiDataBodyid);
        v.add(this.bodyIds);
        return new DERSequence(v);
    }
}
