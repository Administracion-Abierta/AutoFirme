package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/AttCertValidityPeriod.class */
public class AttCertValidityPeriod extends ASN1Object {
    ASN1GeneralizedTime notBeforeTime;
    ASN1GeneralizedTime notAfterTime;

    public static AttCertValidityPeriod getInstance(Object obj) {
        if (obj instanceof AttCertValidityPeriod) {
            return (AttCertValidityPeriod) obj;
        }
        if (obj != null) {
            return new AttCertValidityPeriod(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private AttCertValidityPeriod(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + aSN1Sequence.size());
        }
        this.notBeforeTime = ASN1GeneralizedTime.getInstance(aSN1Sequence.getObjectAt(0));
        this.notAfterTime = ASN1GeneralizedTime.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public AttCertValidityPeriod(ASN1GeneralizedTime aSN1GeneralizedTime, ASN1GeneralizedTime aSN1GeneralizedTime2) {
        this.notBeforeTime = aSN1GeneralizedTime;
        this.notAfterTime = aSN1GeneralizedTime2;
    }

    public ASN1GeneralizedTime getNotBeforeTime() {
        return this.notBeforeTime;
    }

    public ASN1GeneralizedTime getNotAfterTime() {
        return this.notAfterTime;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.notBeforeTime, this.notAfterTime);
    }
}
