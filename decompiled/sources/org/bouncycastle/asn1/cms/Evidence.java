package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.tsp.EvidenceRecord;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cms/Evidence.class */
public class Evidence extends ASN1Object implements ASN1Choice {
    private TimeStampTokenEvidence tstEvidence;
    private EvidenceRecord ersEvidence;
    private ASN1Sequence otherEvidence;

    public Evidence(TimeStampTokenEvidence timeStampTokenEvidence) {
        this.tstEvidence = timeStampTokenEvidence;
    }

    public Evidence(EvidenceRecord evidenceRecord) {
        this.ersEvidence = evidenceRecord;
    }

    private Evidence(ASN1TaggedObject aSN1TaggedObject) {
        if (aSN1TaggedObject.getTagNo() == 0) {
            this.tstEvidence = TimeStampTokenEvidence.getInstance(aSN1TaggedObject, false);
        } else if (aSN1TaggedObject.getTagNo() == 1) {
            this.ersEvidence = EvidenceRecord.getInstance(aSN1TaggedObject, false);
        } else {
            if (aSN1TaggedObject.getTagNo() != 2) {
                throw new IllegalArgumentException("unknown tag in Evidence");
            }
            this.otherEvidence = ASN1Sequence.getInstance(aSN1TaggedObject, false);
        }
    }

    public static Evidence getInstance(Object obj) {
        if (obj == null || (obj instanceof Evidence)) {
            return (Evidence) obj;
        }
        if (obj instanceof ASN1TaggedObject) {
            return new Evidence(ASN1TaggedObject.getInstance(obj, 128));
        }
        throw new IllegalArgumentException("unknown object in getInstance");
    }

    public static Evidence getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        if (z) {
            return getInstance(aSN1TaggedObject.getExplicitBaseObject());
        }
        throw new IllegalArgumentException("choice item must be explicitly tagged");
    }

    public TimeStampTokenEvidence getTstEvidence() {
        return this.tstEvidence;
    }

    public EvidenceRecord getErsEvidence() {
        return this.ersEvidence;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        if (this.tstEvidence != null) {
            return new DERTaggedObject(false, 0, (ASN1Encodable) this.tstEvidence);
        }
        return this.ersEvidence != null ? new DERTaggedObject(false, 1, (ASN1Encodable) this.ersEvidence) : new DERTaggedObject(false, 2, (ASN1Encodable) this.otherEvidence);
    }
}
