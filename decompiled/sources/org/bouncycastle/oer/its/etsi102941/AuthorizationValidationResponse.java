package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.etsi102941.basetypes.CertificateSubjectAttributes;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/AuthorizationValidationResponse.class */
public class AuthorizationValidationResponse extends ASN1Object {
    private final ASN1OctetString requestHash;
    private final AuthorizationValidationResponseCode responseCode;
    private final CertificateSubjectAttributes confirmedSubjectAttributes;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/AuthorizationValidationResponse$Builder.class */
    public static class Builder {
        private ASN1OctetString requestHash;
        private AuthorizationValidationResponseCode responseCode;
        private CertificateSubjectAttributes confirmedSubjectAttributes;

        public Builder setRequestHash(ASN1OctetString aSN1OctetString) {
            this.requestHash = aSN1OctetString;
            return this;
        }

        public Builder setRequestHash(byte[] bArr) {
            this.requestHash = new DEROctetString(Arrays.clone(bArr));
            return this;
        }

        public Builder setResponseCode(AuthorizationValidationResponseCode authorizationValidationResponseCode) {
            this.responseCode = authorizationValidationResponseCode;
            return this;
        }

        public Builder setConfirmedSubjectAttributes(CertificateSubjectAttributes certificateSubjectAttributes) {
            this.confirmedSubjectAttributes = certificateSubjectAttributes;
            return this;
        }

        public AuthorizationValidationResponse createAuthorizationValidationResponse() {
            return new AuthorizationValidationResponse(this.requestHash, this.responseCode, this.confirmedSubjectAttributes);
        }
    }

    public AuthorizationValidationResponse(ASN1OctetString aSN1OctetString, AuthorizationValidationResponseCode authorizationValidationResponseCode, CertificateSubjectAttributes certificateSubjectAttributes) {
        this.requestHash = aSN1OctetString;
        this.responseCode = authorizationValidationResponseCode;
        this.confirmedSubjectAttributes = certificateSubjectAttributes;
    }

    private AuthorizationValidationResponse(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 3) {
            throw new IllegalArgumentException("expected sequence size of 3");
        }
        this.requestHash = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0));
        this.responseCode = AuthorizationValidationResponseCode.getInstance((Object) aSN1Sequence.getObjectAt(1));
        this.confirmedSubjectAttributes = (CertificateSubjectAttributes) OEROptional.getValue(CertificateSubjectAttributes.class, aSN1Sequence.getObjectAt(2));
    }

    public static AuthorizationValidationResponse getInstance(Object obj) {
        if (obj instanceof AuthorizationValidationResponse) {
            return (AuthorizationValidationResponse) obj;
        }
        if (obj != null) {
            return new AuthorizationValidationResponse(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1OctetString getRequestHash() {
        return this.requestHash;
    }

    public AuthorizationValidationResponseCode getResponseCode() {
        return this.responseCode;
    }

    public CertificateSubjectAttributes getConfirmedSubjectAttributes() {
        return this.confirmedSubjectAttributes;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.requestHash, this.responseCode, OEROptional.getInstance(this.confirmedSubjectAttributes)});
    }

    public static Builder builder() {
        return new Builder();
    }
}
