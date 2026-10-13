package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.ReasonFlags;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cmc/GetCRL.class */
public class GetCRL extends ASN1Object {
    private final X500Name issuerName;
    private GeneralName cRLName;
    private ASN1GeneralizedTime time;
    private ReasonFlags reasons;

    public GetCRL(X500Name x500Name, GeneralName generalName, ASN1GeneralizedTime aSN1GeneralizedTime, ReasonFlags reasonFlags) {
        this.issuerName = x500Name;
        this.cRLName = generalName;
        this.time = aSN1GeneralizedTime;
        this.reasons = reasonFlags;
    }

    private GetCRL(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() < 1 || aSN1Sequence.size() > 4) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.issuerName = X500Name.getInstance(aSN1Sequence.getObjectAt(0));
        int i = 1;
        if (aSN1Sequence.size() > 1 && (aSN1Sequence.getObjectAt(1).toASN1Primitive() instanceof ASN1TaggedObject)) {
            i = 1 + 1;
            this.cRLName = GeneralName.getInstance(aSN1Sequence.getObjectAt(1));
        }
        if (aSN1Sequence.size() > i && (aSN1Sequence.getObjectAt(i).toASN1Primitive() instanceof ASN1GeneralizedTime)) {
            int i2 = i;
            i++;
            this.time = ASN1GeneralizedTime.getInstance(aSN1Sequence.getObjectAt(i2));
        }
        if (aSN1Sequence.size() <= i || !(aSN1Sequence.getObjectAt(i).toASN1Primitive() instanceof ASN1BitString)) {
            return;
        }
        this.reasons = new ReasonFlags(ASN1BitString.getInstance(aSN1Sequence.getObjectAt(i)));
    }

    public static GetCRL getInstance(Object obj) {
        if (obj instanceof GetCRL) {
            return (GetCRL) obj;
        }
        if (obj != null) {
            return new GetCRL(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public X500Name getIssuerName() {
        return this.issuerName;
    }

    public GeneralName getcRLName() {
        return this.cRLName;
    }

    public ASN1GeneralizedTime getTime() {
        return this.time;
    }

    public ReasonFlags getReasons() {
        return this.reasons;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(4);
        aSN1EncodableVector.add(this.issuerName);
        if (this.cRLName != null) {
            aSN1EncodableVector.add(this.cRLName);
        }
        if (this.time != null) {
            aSN1EncodableVector.add(this.time);
        }
        if (this.reasons != null) {
            aSN1EncodableVector.add(this.reasons);
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
