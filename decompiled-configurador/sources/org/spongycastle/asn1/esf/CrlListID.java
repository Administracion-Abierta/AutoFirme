package org.spongycastle.asn1.esf;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/esf/CrlListID.class */
public class CrlListID extends ASN1Object {
    private ASN1Sequence crls;

    public static CrlListID getInstance(Object obj) {
        if (obj instanceof CrlListID) {
            return (CrlListID) obj;
        }
        if (obj != null) {
            return new CrlListID(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private CrlListID(ASN1Sequence seq) {
        this.crls = (ASN1Sequence) seq.getObjectAt(0);
        Enumeration e = this.crls.getObjects();
        while (e.hasMoreElements()) {
            CrlValidatedID.getInstance(e.nextElement());
        }
    }

    public CrlListID(CrlValidatedID[] crls) {
        this.crls = new DERSequence(crls);
    }

    public CrlValidatedID[] getCrls() {
        CrlValidatedID[] result = new CrlValidatedID[this.crls.size()];
        for (int idx = 0; idx < result.length; idx++) {
            result[idx] = CrlValidatedID.getInstance(this.crls.getObjectAt(idx));
        }
        return result;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.crls);
    }
}
