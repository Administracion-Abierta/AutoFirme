package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi102941.basetypes.EcSignature;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/AuthorizationValidationRequest.class */
public class AuthorizationValidationRequest extends ASN1Object {
    private final SharedAtRequest sharedAtRequest;
    private final EcSignature ecSignature;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/AuthorizationValidationRequest$Builder.class */
    public static class Builder {
        private SharedAtRequest sharedAtRequest;
        private EcSignature ecSignature;

        public Builder setSharedAtRequest(SharedAtRequest sharedAtRequest) {
            this.sharedAtRequest = sharedAtRequest;
            return this;
        }

        public Builder setEcSignature(EcSignature ecSignature) {
            this.ecSignature = ecSignature;
            return this;
        }

        public AuthorizationValidationRequest createAuthorizationValidationRequest() {
            return new AuthorizationValidationRequest(this.sharedAtRequest, this.ecSignature);
        }
    }

    public AuthorizationValidationRequest(SharedAtRequest sharedAtRequest, EcSignature ecSignature) {
        this.sharedAtRequest = sharedAtRequest;
        this.ecSignature = ecSignature;
    }

    private AuthorizationValidationRequest(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.sharedAtRequest = SharedAtRequest.getInstance(aSN1Sequence.getObjectAt(0));
        this.ecSignature = EcSignature.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static AuthorizationValidationRequest getInstance(Object obj) {
        if (obj instanceof AuthorizationValidationRequest) {
            return (AuthorizationValidationRequest) obj;
        }
        if (obj != null) {
            return new AuthorizationValidationRequest(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public SharedAtRequest getSharedAtRequest() {
        return this.sharedAtRequest;
    }

    public EcSignature getEcSignature() {
        return this.ecSignature;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.sharedAtRequest, this.ecSignature});
    }

    public static Builder builder() {
        return new Builder();
    }
}
