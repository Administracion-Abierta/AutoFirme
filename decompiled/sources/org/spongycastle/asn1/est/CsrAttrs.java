package org.spongycastle.asn1.est;

import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/est/CsrAttrs.class */
public class CsrAttrs extends ASN1Object {
    private final AttrOrOID[] attrOrOIDs;

    public static CsrAttrs getInstance(Object obj) {
        if (obj instanceof CsrAttrs) {
            return (CsrAttrs) obj;
        }
        if (obj != null) {
            return new CsrAttrs(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static CsrAttrs getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public CsrAttrs(AttrOrOID attrOrOID) {
        this.attrOrOIDs = new AttrOrOID[]{attrOrOID};
    }

    public CsrAttrs(AttrOrOID[] attrOrOIDs) {
        this.attrOrOIDs = Utils.clone(attrOrOIDs);
    }

    private CsrAttrs(ASN1Sequence seq) {
        this.attrOrOIDs = new AttrOrOID[seq.size()];
        for (int i = 0; i != seq.size(); i++) {
            this.attrOrOIDs[i] = AttrOrOID.getInstance(seq.getObjectAt(i));
        }
    }

    public AttrOrOID[] getAttrOrOIDs() {
        return Utils.clone(this.attrOrOIDs);
    }

    public int size() {
        return this.attrOrOIDs.length;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.attrOrOIDs);
    }
}
