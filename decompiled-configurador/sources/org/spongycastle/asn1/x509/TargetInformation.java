package org.spongycastle.asn1.x509;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/x509/TargetInformation.class */
public class TargetInformation extends ASN1Object {
    private ASN1Sequence targets;

    public static TargetInformation getInstance(Object obj) {
        if (obj instanceof TargetInformation) {
            return (TargetInformation) obj;
        }
        if (obj != null) {
            return new TargetInformation(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private TargetInformation(ASN1Sequence seq) {
        this.targets = seq;
    }

    public Targets[] getTargetsObjects() {
        Targets[] copy = new Targets[this.targets.size()];
        int count = 0;
        Enumeration e = this.targets.getObjects();
        while (e.hasMoreElements()) {
            int i = count;
            count++;
            copy[i] = Targets.getInstance(e.nextElement());
        }
        return copy;
    }

    public TargetInformation(Targets targets) {
        this.targets = new DERSequence(targets);
    }

    public TargetInformation(Target[] targets) {
        this(new Targets(targets));
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.targets;
    }
}
