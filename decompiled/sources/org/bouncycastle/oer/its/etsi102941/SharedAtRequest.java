package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi102941.basetypes.CertificateFormat;
import org.bouncycastle.oer.its.etsi102941.basetypes.CertificateSubjectAttributes;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/SharedAtRequest.class */
public class SharedAtRequest extends ASN1Object {
    private final HashedId8 eaId;
    private final ASN1OctetString keyTag;
    private final CertificateFormat certificateFormat;
    private final CertificateSubjectAttributes requestedSubjectAttributes;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/SharedAtRequest$Builder.class */
    public static class Builder {
        private HashedId8 eaId;
        private ASN1OctetString keyTag;
        private CertificateFormat certificateFormat;
        private CertificateSubjectAttributes requestedSubjectAttributes;

        public Builder setEaId(HashedId8 hashedId8) {
            this.eaId = hashedId8;
            return this;
        }

        public Builder setKeyTag(ASN1OctetString aSN1OctetString) {
            this.keyTag = aSN1OctetString;
            return this;
        }

        public Builder setKeyTag(byte[] bArr) {
            this.keyTag = new DEROctetString(bArr);
            return this;
        }

        public Builder setCertificateFormat(CertificateFormat certificateFormat) {
            this.certificateFormat = certificateFormat;
            return this;
        }

        public Builder setRequestedSubjectAttributes(CertificateSubjectAttributes certificateSubjectAttributes) {
            this.requestedSubjectAttributes = certificateSubjectAttributes;
            return this;
        }

        public SharedAtRequest createSharedAtRequest() {
            return new SharedAtRequest(this.eaId, this.keyTag, this.certificateFormat, this.requestedSubjectAttributes);
        }
    }

    public SharedAtRequest(HashedId8 hashedId8, ASN1OctetString aSN1OctetString, CertificateFormat certificateFormat, CertificateSubjectAttributes certificateSubjectAttributes) {
        this.eaId = hashedId8;
        this.keyTag = aSN1OctetString;
        this.certificateFormat = certificateFormat;
        this.requestedSubjectAttributes = certificateSubjectAttributes;
    }

    private SharedAtRequest(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 4) {
            throw new IllegalArgumentException("expected sequence size of 4");
        }
        this.eaId = HashedId8.getInstance(aSN1Sequence.getObjectAt(0));
        this.keyTag = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1));
        this.certificateFormat = CertificateFormat.getInstance(aSN1Sequence.getObjectAt(2));
        this.requestedSubjectAttributes = CertificateSubjectAttributes.getInstance(aSN1Sequence.getObjectAt(3));
    }

    public static SharedAtRequest getInstance(Object obj) {
        if (obj instanceof SharedAtRequest) {
            return (SharedAtRequest) obj;
        }
        if (obj != null) {
            return new SharedAtRequest(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public HashedId8 getEaId() {
        return this.eaId;
    }

    public ASN1OctetString getKeyTag() {
        return this.keyTag;
    }

    public CertificateFormat getCertificateFormat() {
        return this.certificateFormat;
    }

    public CertificateSubjectAttributes getRequestedSubjectAttributes() {
        return this.requestedSubjectAttributes;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.eaId, this.keyTag, this.certificateFormat, this.requestedSubjectAttributes});
    }

    public static Builder builder() {
        return new Builder();
    }
}
