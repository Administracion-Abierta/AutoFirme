package org.spongycastle.asn1.cmc;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1GeneralizedTime;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.GeneralName;
import org.spongycastle.asn1.x509.ReasonFlags;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmc/GetCRL.class */
public class GetCRL extends ASN1Object {
    private final X500Name issuerName;
    private GeneralName cRLName;
    private ASN1GeneralizedTime time;
    private ReasonFlags reasons;

    public GetCRL(X500Name issuerName, GeneralName cRLName, ASN1GeneralizedTime time, ReasonFlags reasons) {
        this.issuerName = issuerName;
        this.cRLName = cRLName;
        this.time = time;
        this.reasons = reasons;
    }

    private GetCRL(ASN1Sequence seq) {
        if (seq.size() < 1 || seq.size() > 4) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.issuerName = X500Name.getInstance(seq.getObjectAt(0));
        int index = 1;
        if (seq.size() > 1 && (seq.getObjectAt(1).toASN1Primitive() instanceof ASN1TaggedObject)) {
            index = 1 + 1;
            this.cRLName = GeneralName.getInstance(seq.getObjectAt(1));
        }
        if (seq.size() > index && (seq.getObjectAt(index).toASN1Primitive() instanceof ASN1GeneralizedTime)) {
            int i = index;
            index++;
            this.time = ASN1GeneralizedTime.getInstance(seq.getObjectAt(i));
        }
        if (seq.size() > index && (seq.getObjectAt(index).toASN1Primitive() instanceof DERBitString)) {
            this.reasons = new ReasonFlags(DERBitString.getInstance(seq.getObjectAt(index)));
        }
    }

    public static GetCRL getInstance(Object o) {
        if (o instanceof GetCRL) {
            return (GetCRL) o;
        }
        if (o != null) {
            return new GetCRL(ASN1Sequence.getInstance(o));
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

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.issuerName);
        if (this.cRLName != null) {
            v.add(this.cRLName);
        }
        if (this.time != null) {
            v.add(this.time);
        }
        if (this.reasons != null) {
            v.add(this.reasons);
        }
        return new DERSequence(v);
    }
}
