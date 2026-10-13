package org.spongycastle.asn1.cmc;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmc/TaggedAttribute.class */
public class TaggedAttribute extends ASN1Object {
    private final BodyPartID bodyPartID;
    private final ASN1ObjectIdentifier attrType;
    private final ASN1Set attrValues;

    public static TaggedAttribute getInstance(Object o) {
        if (o instanceof TaggedAttribute) {
            return (TaggedAttribute) o;
        }
        if (o != null) {
            return new TaggedAttribute(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    private TaggedAttribute(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.bodyPartID = BodyPartID.getInstance(seq.getObjectAt(0));
        this.attrType = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(1));
        this.attrValues = ASN1Set.getInstance(seq.getObjectAt(2));
    }

    public TaggedAttribute(BodyPartID bodyPartID, ASN1ObjectIdentifier attrType, ASN1Set attrValues) {
        this.bodyPartID = bodyPartID;
        this.attrType = attrType;
        this.attrValues = attrValues;
    }

    public BodyPartID getBodyPartID() {
        return this.bodyPartID;
    }

    public ASN1ObjectIdentifier getAttrType() {
        return this.attrType;
    }

    public ASN1Set getAttrValues() {
        return this.attrValues;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.bodyPartID, this.attrType, this.attrValues});
    }
}
