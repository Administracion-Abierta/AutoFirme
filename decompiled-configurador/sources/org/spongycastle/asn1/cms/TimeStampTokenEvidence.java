package org.spongycastle.asn1.cms;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cms/TimeStampTokenEvidence.class */
public class TimeStampTokenEvidence extends ASN1Object {
    private TimeStampAndCRL[] timeStampAndCRLs;

    public TimeStampTokenEvidence(TimeStampAndCRL[] timeStampAndCRLs) {
        this.timeStampAndCRLs = timeStampAndCRLs;
    }

    public TimeStampTokenEvidence(TimeStampAndCRL timeStampAndCRL) {
        this.timeStampAndCRLs = new TimeStampAndCRL[1];
        this.timeStampAndCRLs[0] = timeStampAndCRL;
    }

    private TimeStampTokenEvidence(ASN1Sequence seq) {
        this.timeStampAndCRLs = new TimeStampAndCRL[seq.size()];
        int count = 0;
        Enumeration en = seq.getObjects();
        while (en.hasMoreElements()) {
            int i = count;
            count++;
            this.timeStampAndCRLs[i] = TimeStampAndCRL.getInstance(en.nextElement());
        }
    }

    public static TimeStampTokenEvidence getInstance(ASN1TaggedObject tagged, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(tagged, explicit));
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
        return this.timeStampAndCRLs;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        for (int i = 0; i != this.timeStampAndCRLs.length; i++) {
            v.add(this.timeStampAndCRLs[i]);
        }
        return new DERSequence(v);
    }
}
