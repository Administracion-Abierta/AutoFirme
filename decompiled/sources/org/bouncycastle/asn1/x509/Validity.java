package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/Validity.class */
public class Validity extends ASN1Object {
    private final Time notBefore;
    private final Time notAfter;

    public static Validity getInstance(Object obj) {
        if (obj instanceof Validity) {
            return (Validity) obj;
        }
        if (obj != null) {
            return new Validity(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static Validity getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return new Validity(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    private Validity(ASN1Sequence aSN1Sequence) {
        int size = aSN1Sequence.size();
        if (size != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + size);
        }
        this.notBefore = Time.getInstance(aSN1Sequence.getObjectAt(0));
        this.notAfter = Time.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public Validity(Time time, Time time2) {
        if (time == null) {
            throw new NullPointerException("'notBefore' cannot be null");
        }
        if (time2 == null) {
            throw new NullPointerException("'notAfter' cannot be null");
        }
        this.notBefore = time;
        this.notAfter = time2;
    }

    public Time getNotBefore() {
        return this.notBefore;
    }

    public Time getNotAfter() {
        return this.notAfter;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.notBefore, this.notAfter);
    }
}
