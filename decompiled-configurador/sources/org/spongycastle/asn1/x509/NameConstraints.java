package org.spongycastle.asn1.x509;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/x509/NameConstraints.class */
public class NameConstraints extends ASN1Object {
    private GeneralSubtree[] permitted;
    private GeneralSubtree[] excluded;

    public static NameConstraints getInstance(Object obj) {
        if (obj instanceof NameConstraints) {
            return (NameConstraints) obj;
        }
        if (obj != null) {
            return new NameConstraints(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private NameConstraints(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        while (e.hasMoreElements()) {
            ASN1TaggedObject o = ASN1TaggedObject.getInstance(e.nextElement());
            switch (o.getTagNo()) {
                case 0:
                    this.permitted = createArray(ASN1Sequence.getInstance(o, false));
                    break;
                case 1:
                    this.excluded = createArray(ASN1Sequence.getInstance(o, false));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown tag encountered: " + o.getTagNo());
            }
        }
    }

    public NameConstraints(GeneralSubtree[] permitted, GeneralSubtree[] excluded) {
        this.permitted = cloneSubtree(permitted);
        this.excluded = cloneSubtree(excluded);
    }

    private GeneralSubtree[] createArray(ASN1Sequence subtree) {
        GeneralSubtree[] ar = new GeneralSubtree[subtree.size()];
        for (int i = 0; i != ar.length; i++) {
            ar[i] = GeneralSubtree.getInstance(subtree.getObjectAt(i));
        }
        return ar;
    }

    public GeneralSubtree[] getPermittedSubtrees() {
        return cloneSubtree(this.permitted);
    }

    public GeneralSubtree[] getExcludedSubtrees() {
        return cloneSubtree(this.excluded);
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        if (this.permitted != null) {
            v.add(new DERTaggedObject(false, 0, new DERSequence(this.permitted)));
        }
        if (this.excluded != null) {
            v.add(new DERTaggedObject(false, 1, new DERSequence(this.excluded)));
        }
        return new DERSequence(v);
    }

    private static GeneralSubtree[] cloneSubtree(GeneralSubtree[] subtrees) {
        if (subtrees != null) {
            GeneralSubtree[] rv = new GeneralSubtree[subtrees.length];
            System.arraycopy(subtrees, 0, rv, 0, rv.length);
            return rv;
        }
        return null;
    }
}
