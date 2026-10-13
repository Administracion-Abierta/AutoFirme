package org.bouncycastle.asn1.cms;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cms/TimeStampTokenEvidence.class */
public class TimeStampTokenEvidence extends ASN1Object {
    private TimeStampAndCRL[] timeStampAndCRLs;

    public TimeStampTokenEvidence(TimeStampAndCRL[] timeStampAndCRLArr) {
        this.timeStampAndCRLs = copy(timeStampAndCRLArr);
    }

    public TimeStampTokenEvidence(TimeStampAndCRL timeStampAndCRL) {
        this.timeStampAndCRLs = new TimeStampAndCRL[1];
        this.timeStampAndCRLs[0] = timeStampAndCRL;
    }

    private TimeStampTokenEvidence(ASN1Sequence aSN1Sequence) {
        this.timeStampAndCRLs = new TimeStampAndCRL[aSN1Sequence.size()];
        int i = 0;
        Enumeration objects = aSN1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            int i2 = i;
            i++;
            this.timeStampAndCRLs[i2] = TimeStampAndCRL.getInstance(objects.nextElement());
        }
    }

    public static TimeStampTokenEvidence getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    public static TimeStampTokenEvidence getInstance(Object obj) {
        if (obj instanceof TimeStampTokenEvidence) {
            return (TimeStampTokenEvidence) obj;
        }
        if (obj != null) {
            return new TimeStampTokenEvidence(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public TimeStampAndCRL[] toTimeStampAndCRLArray() {
        return copy(this.timeStampAndCRLs);
    }

    private TimeStampAndCRL[] copy(TimeStampAndCRL[] timeStampAndCRLArr) {
        TimeStampAndCRL[] timeStampAndCRLArr2 = new TimeStampAndCRL[timeStampAndCRLArr.length];
        System.arraycopy(timeStampAndCRLArr, 0, timeStampAndCRLArr2, 0, timeStampAndCRLArr2.length);
        return timeStampAndCRLArr2;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(this.timeStampAndCRLs.length);
        for (int i = 0; i != this.timeStampAndCRLs.length; i++) {
            aSN1EncodableVector.add(this.timeStampAndCRLs[i]);
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
