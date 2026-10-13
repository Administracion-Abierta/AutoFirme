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
import org.bouncycastle.oer.its.etsi102941.basetypes.PublicKeys;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/InnerEcRequest.class */
public class InnerEcRequest extends ASN1Object {
    private final ASN1OctetString itsId;
    private final CertificateFormat certificateFormat;
    private final PublicKeys publicKeys;
    private final CertificateSubjectAttributes requestedSubjectAttributes;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/InnerEcRequest$Builder.class */
    public static class Builder {
        private ASN1OctetString itsId;
        private CertificateFormat certificateFormat;
        private PublicKeys publicKeys;
        private CertificateSubjectAttributes requestedSubjectAttributes;

        public Builder setItsId(ASN1OctetString aSN1OctetString) {
            this.itsId = aSN1OctetString;
            return this;
        }

        public Builder setItsId(byte[] bArr) {
            this.itsId = new DEROctetString(Arrays.clone(bArr));
            return this;
        }

        public Builder setCertificateFormat(CertificateFormat certificateFormat) {
            this.certificateFormat = certificateFormat;
            return this;
        }

        public Builder setPublicKeys(PublicKeys publicKeys) {
            this.publicKeys = publicKeys;
            return this;
        }

        public Builder setRequestedSubjectAttributes(CertificateSubjectAttributes certificateSubjectAttributes) {
            this.requestedSubjectAttributes = certificateSubjectAttributes;
            return this;
        }

        public InnerEcRequest createInnerEcRequest() {
            return new InnerEcRequest(this.itsId, this.certificateFormat, this.publicKeys, this.requestedSubjectAttributes);
        }
    }

    public InnerEcRequest(ASN1OctetString aSN1OctetString, CertificateFormat certificateFormat, PublicKeys publicKeys, CertificateSubjectAttributes certificateSubjectAttributes) {
        this.itsId = aSN1OctetString;
        this.certificateFormat = certificateFormat;
        this.publicKeys = publicKeys;
        this.requestedSubjectAttributes = certificateSubjectAttributes;
    }

    private InnerEcRequest(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 4) {
            throw new IllegalArgumentException("expected sequence size of 4");
        }
        this.itsId = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0));
        this.certificateFormat = CertificateFormat.getInstance(aSN1Sequence.getObjectAt(1));
        this.publicKeys = PublicKeys.getInstance(aSN1Sequence.getObjectAt(2));
        this.requestedSubjectAttributes = CertificateSubjectAttributes.getInstance(aSN1Sequence.getObjectAt(3));
    }

    public static InnerEcRequest getInstance(Object obj) {
        if (obj instanceof InnerEcRequest) {
            return (InnerEcRequest) obj;
        }
        if (obj != null) {
            return new InnerEcRequest(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1OctetString getItsId() {
        return this.itsId;
    }

    public CertificateFormat getCertificateFormat() {
        return this.certificateFormat;
    }

    public PublicKeys getPublicKeys() {
        return this.publicKeys;
    }

    public CertificateSubjectAttributes getRequestedSubjectAttributes() {
        return this.requestedSubjectAttributes;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.itsId, this.certificateFormat, this.publicKeys, this.requestedSubjectAttributes});
    }

    public static Builder builder() {
        return new Builder();
    }
}
