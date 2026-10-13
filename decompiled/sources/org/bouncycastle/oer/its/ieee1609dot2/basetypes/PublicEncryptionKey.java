package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/PublicEncryptionKey.class */
public class PublicEncryptionKey extends ASN1Object {
    private final SymmAlgorithm supportedSymmAlg;
    private final BasePublicEncryptionKey publicKey;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/PublicEncryptionKey$Builder.class */
    public static class Builder {
        private SymmAlgorithm supportedSymmAlg;
        private BasePublicEncryptionKey publicKey;

        public Builder setSupportedSymmAlg(SymmAlgorithm symmAlgorithm) {
            this.supportedSymmAlg = symmAlgorithm;
            return this;
        }

        public Builder setPublicKey(BasePublicEncryptionKey basePublicEncryptionKey) {
            this.publicKey = basePublicEncryptionKey;
            return this;
        }

        public PublicEncryptionKey createPublicEncryptionKey() {
            return new PublicEncryptionKey(this.supportedSymmAlg, this.publicKey);
        }
    }

    public PublicEncryptionKey(SymmAlgorithm symmAlgorithm, BasePublicEncryptionKey basePublicEncryptionKey) {
        this.supportedSymmAlg = symmAlgorithm;
        this.publicKey = basePublicEncryptionKey;
    }

    private PublicEncryptionKey(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.supportedSymmAlg = SymmAlgorithm.getInstance((Object) aSN1Sequence.getObjectAt(0));
        this.publicKey = BasePublicEncryptionKey.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static PublicEncryptionKey getInstance(Object obj) {
        if (obj instanceof PublicEncryptionKey) {
            return (PublicEncryptionKey) obj;
        }
        if (obj != null) {
            return new PublicEncryptionKey(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public SymmAlgorithm getSupportedSymmAlg() {
        return this.supportedSymmAlg;
    }

    public BasePublicEncryptionKey getPublicKey() {
        return this.publicKey;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.supportedSymmAlg, this.publicKey);
    }

    public static Builder builder() {
        return new Builder();
    }
}
