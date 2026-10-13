package org.spongycastle.asn1.x509;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/x509/Targets.class */
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

    private Targets(ASN1Sequence targets) {
        this.targets = targets;
    }

    public Targets(Target[] targets) {
        this.targets = new DERSequence(targets);
    }

    public Target[] getTargets() {
        Target[] targs = new Target[this.targets.size()];
        int count = 0;
        Enumeration e = this.targets.getObjects();
        while (e.hasMoreElements()) {
            int i = count;
            count++;
            targs[i] = Target.getInstance(e.nextElement());
        }
        return targs;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.targets;
    }
}
