package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/Targets.class */
public class Targets extends ASN1Object {
    private ASN1Sequence targets;

    public static Targets getInstance(Object obj) {
        if (obj instanceof Targets) {
            return (Targets) obj;
        }
        if (obj != null) {
            return new Targets(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private Targets(ASN1Sequence aSN1Sequence) {
        this.targets = aSN1Sequence;
    }

    public Targets(Target[] targetArr) {
        this.targets = new DERSequence(targetArr);
    }

    public Target[] getTargets() {
        Target[] targetArr = new Target[this.targets.size()];
        int i = 0;
        Enumeration objects = this.targets.getObjects();
        while (objects.hasMoreElements()) {
            int i2 = i;
            i++;
            targetArr[i2] = Target.getInstance(objects.nextElement());
        }
        return targetArr;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.targets;
    }
}
