package org.spongycastle.asn1.crmf;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.x509.Time;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/crmf/OptionalValidity.class */
public class OptionalValidity extends ASN1Object {
    private Time notBefore;
    private Time notAfter;

    private OptionalValidity(ASN1Sequence seq) {
        Enumeration en = seq.getObjects();
        while (en.hasMoreElements()) {
            ASN1TaggedObject tObj = (ASN1TaggedObject) en.nextElement();
            if (tObj.getTagNo() == 0) {
                this.notBefore = Time.getInstance(tObj, true);
            } else {
                this.notAfter = Time.getInstance(tObj, true);
            }
        }
    }

    public static OptionalValidity getInstance(Object o) {
        if (o instanceof OptionalValidity) {
            return (OptionalValidity) o;
        }
        if (o != null) {
            return new OptionalValidity(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public OptionalValidity(Time notBefore, Time notAfter) {
        if (notBefore == null && notAfter == null) {
            throw new IllegalArgumentException("at least one of notBefore/notAfter must not be null.");
        }
        this.notBefore = notBefore;
        this.notAfter = notAfter;
    }

    public Time getNotBefore() {
        return this.notBefore;
    }

    public Time getNotAfter() {
        return this.notAfter;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        if (this.notBefore != null) {
            v.add(new DERTaggedObject(true, 0, this.notBefore));
        }
        if (this.notAfter != null) {
            v.add(new DERTaggedObject(true, 1, this.notAfter));
        }
        return new DERSequence(v);
    }
}
