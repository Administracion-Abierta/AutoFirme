package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097Certificate;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/TlmEntry.class */
public class TlmEntry extends ASN1Object {
    private final EtsiTs103097Certificate selfSignedTLMCertificate;
    private final EtsiTs103097Certificate successorTo;
    private final Url accessPoint;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/TlmEntry$Builder.class */
    public static class Builder {
        private EtsiTs103097Certificate selfSignedTLMCertificate;
        private EtsiTs103097Certificate successorTo;
        private Url accessPoint;

        public Builder setSelfSignedTLMCertificate(EtsiTs103097Certificate etsiTs103097Certificate) {
            this.selfSignedTLMCertificate = etsiTs103097Certificate;
            return this;
        }

        public Builder setSuccessorTo(EtsiTs103097Certificate etsiTs103097Certificate) {
            this.successorTo = etsiTs103097Certificate;
            return this;
        }

        public Builder setAccessPoint(Url url) {
            this.accessPoint = url;
            return this;
        }

        public TlmEntry createTlmEntry() {
            return new TlmEntry(this.selfSignedTLMCertificate, this.successorTo, this.accessPoint);
        }
    }

    public TlmEntry(EtsiTs103097Certificate etsiTs103097Certificate, EtsiTs103097Certificate etsiTs103097Certificate2, Url url) {
        this.selfSignedTLMCertificate = etsiTs103097Certificate;
        this.successorTo = etsiTs103097Certificate2;
        this.accessPoint = url;
    }

    private TlmEntry(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 3) {
            throw new IllegalArgumentException("expected sequence size of 3");
        }
        this.selfSignedTLMCertificate = EtsiTs103097Certificate.getInstance((Object) aSN1Sequence.getObjectAt(0));
        this.successorTo = (EtsiTs103097Certificate) OEROptional.getValue(EtsiTs103097Certificate.class, aSN1Sequence.getObjectAt(1));
        this.accessPoint = Url.getInstance(aSN1Sequence.getObjectAt(2));
    }

    public static TlmEntry getInstance(Object obj) {
        if (obj instanceof TlmEntry) {
            return (TlmEntry) obj;
        }
        if (obj != null) {
            return new TlmEntry(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public EtsiTs103097Certificate getSelfSignedTLMCertificate() {
        return this.selfSignedTLMCertificate;
    }

    public EtsiTs103097Certificate getSuccessorTo() {
        return this.successorTo;
    }

    public Url getAccessPoint() {
        return this.accessPoint;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.selfSignedTLMCertificate, OEROptional.getInstance(this.successorTo), this.accessPoint});
    }

    public static Builder builder() {
        return new Builder();
    }
}
