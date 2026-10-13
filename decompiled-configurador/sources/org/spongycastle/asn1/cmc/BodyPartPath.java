package org.spongycastle.asn1.cmc;

import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cmc/BodyPartPath.class */
public class BodyPartPath extends ASN1Object {
    private final BodyPartID[] bodyPartIDs;

    public static BodyPartPath getInstance(Object obj) {
        if (obj instanceof BodyPartPath) {
            return (BodyPartPath) obj;
        }
        if (obj != null) {
            return new BodyPartPath(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static BodyPartPath getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public BodyPartPath(BodyPartID bodyPartID) {
        this.bodyPartIDs = new BodyPartID[]{bodyPartID};
    }

    public BodyPartPath(BodyPartID[] bodyPartIDs) {
        this.bodyPartIDs = Utils.clone(bodyPartIDs);
    }

    private BodyPartPath(ASN1Sequence seq) {
        this.bodyPartIDs = Utils.toBodyPartIDArray(seq);
    }

    public BodyPartID[] getBodyPartIDs() {
        return Utils.clone(this.bodyPartIDs);
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.bodyPartIDs);
    }
}
