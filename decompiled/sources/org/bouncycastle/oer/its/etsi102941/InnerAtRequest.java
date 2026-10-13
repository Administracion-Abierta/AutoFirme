package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi102941.basetypes.EcSignature;
import org.bouncycastle.oer.its.etsi102941.basetypes.PublicKeys;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/InnerAtRequest.class */
public class InnerAtRequest extends ASN1Object {
    private final PublicKeys publicKeys;
    private final ASN1OctetString hmacKey;
    private final SharedAtRequest sharedAtRequest;
    private final EcSignature ecSignature;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/InnerAtRequest$Builder.class */
    public static class Builder {
        private PublicKeys publicKeys;
        private ASN1OctetString hmacKey;
        private SharedAtRequest sharedAtRequest;
        private EcSignature ecSignature;

        public Builder setPublicKeys(PublicKeys publicKeys) {
            this.publicKeys = publicKeys;
            return this;
        }

        public Builder setHmacKey(ASN1OctetString aSN1OctetString) {
            this.hmacKey = aSN1OctetString;
            return this;
        }

        public Builder setHmacKey(byte[] bArr) {
            this.hmacKey = new DEROctetString(Arrays.clone(bArr));
            return this;
        }

        public Builder setSharedAtRequest(SharedAtRequest sharedAtRequest) {
            this.sharedAtRequest = sharedAtRequest;
            return this;
        }

        public Builder setEcSignature(EcSignature ecSignature) {
            this.ecSignature = ecSignature;
            return this;
        }

        public InnerAtRequest createInnerAtRequest() {
            return new InnerAtRequest(this.publicKeys, this.hmacKey, this.sharedAtRequest, this.ecSignature);
        }
    }

    public InnerAtRequest(PublicKeys publicKeys, ASN1OctetString aSN1OctetString, SharedAtRequest sharedAtRequest, EcSignature ecSignature) {
        this.publicKeys = publicKeys;
        this.hmacKey = aSN1OctetString;
        this.sharedAtRequest = sharedAtRequest;
        this.ecSignature = ecSignature;
    }

    private InnerAtRequest(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 4) {
            throw new IllegalArgumentException("expected sequence size of 4");
        }
        this.publicKeys = PublicKeys.getInstance(aSN1Sequence.getObjectAt(0));
        this.hmacKey = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1));
        this.sharedAtRequest = SharedAtRequest.getInstance(aSN1Sequence.getObjectAt(2));
        this.ecSignature = EcSignature.getInstance(aSN1Sequence.getObjectAt(3));
    }

    public static InnerAtRequest getInstance(Object obj) {
        if (obj instanceof InnerAtRequest) {
            return (InnerAtRequest) obj;
        }
        if (obj != null) {
            return new InnerAtRequest(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public PublicKeys getPublicKeys() {
        return this.publicKeys;
    }

    public ASN1OctetString getHmacKey() {
        return this.hmacKey;
    }

    public SharedAtRequest getSharedAtRequest() {
        return this.sharedAtRequest;
    }

    public EcSignature getEcSignature() {
        return this.ecSignature;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.publicKeys, this.hmacKey, this.sharedAtRequest, this.ecSignature});
    }

    public static Builder builder() {
        return new Builder();
    }
}
