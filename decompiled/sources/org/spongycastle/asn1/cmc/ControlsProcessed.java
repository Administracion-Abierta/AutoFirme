package org.spongycastle.asn1.cmc;

import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmc/ControlsProcessed.class */
public class ControlsProcessed extends ASN1Object {
    private final ASN1Sequence bodyPartReferences;

    public ControlsProcessed(BodyPartReference bodyPartRef) {
        this.bodyPartReferences = new DERSequence(bodyPartRef);
    }

    public ControlsProcessed(BodyPartReference[] bodyList) {
        this.bodyPartReferences = new DERSequence(bodyList);
    }

    public static ControlsProcessed getInstance(Object src) {
        if (src instanceof ControlsProcessed) {
            return (ControlsProcessed) src;
        }
        if (src != null) {
            return new ControlsProcessed(ASN1Sequence.getInstance(src));
        }
        return null;
    }

    private ControlsProcessed(ASN1Sequence seq) {
        if (seq.size() != 1) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.bodyPartReferences = ASN1Sequence.getInstance(seq.getObjectAt(0));
    }

    public BodyPartReference[] getBodyList() {
        BodyPartReference[] tmp = new BodyPartReference[this.bodyPartReferences.size()];
        for (int i = 0; i != this.bodyPartReferences.size(); i++) {
            tmp[i] = BodyPartReference.getInstance(this.bodyPartReferences.getObjectAt(i));
        }
        return tmp;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.bodyPartReferences);
    }
}
