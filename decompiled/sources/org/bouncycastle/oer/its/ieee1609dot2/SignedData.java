package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashAlgorithm;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/SignedData.class */
public class SignedData extends ASN1Object {
    private final HashAlgorithm hashId;
    private final ToBeSignedData tbsData;
    private final SignerIdentifier signer;
    private final Signature signature;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/SignedData$Builder.class */
    public static class Builder {
        private HashAlgorithm hashId;
        private ToBeSignedData tbsData;
        private SignerIdentifier signer;
        private Signature signature;

        public Builder setHashId(HashAlgorithm hashAlgorithm) {
            this.hashId = hashAlgorithm;
            return this;
        }

        public Builder setTbsData(ToBeSignedData toBeSignedData) {
            this.tbsData = toBeSignedData;
            return this;
        }

        public Builder setSigner(SignerIdentifier signerIdentifier) {
            this.signer = signerIdentifier;
            return this;
        }

        public Builder setSignature(Signature signature) {
            this.signature = signature;
            return this;
        }

        public SignedData createSignedData() {
            return new SignedData(this.hashId, this.tbsData, this.signer, this.signature);
        }
    }

    public SignedData(HashAlgorithm hashAlgorithm, ToBeSignedData toBeSignedData, SignerIdentifier signerIdentifier, Signature signature) {
        this.hashId = hashAlgorithm;
        this.tbsData = toBeSignedData;
        this.signer = signerIdentifier;
        this.signature = signature;
    }

    private SignedData(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 4) {
            throw new IllegalArgumentException("expected sequence size of 4");
        }
        this.hashId = HashAlgorithm.getInstance((Object) aSN1Sequence.getObjectAt(0));
        this.tbsData = ToBeSignedData.getInstance(aSN1Sequence.getObjectAt(1));
        this.signer = SignerIdentifier.getInstance(aSN1Sequence.getObjectAt(2));
        this.signature = Signature.getInstance(aSN1Sequence.getObjectAt(3));
    }

    public static SignedData getInstance(Object obj) {
        if (obj instanceof SignedData) {
            return (SignedData) obj;
        }
        if (obj != null) {
            return new SignedData(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.hashId, this.tbsData, this.signer, this.signature);
    }

    public HashAlgorithm getHashId() {
        return this.hashId;
    }

    public ToBeSignedData getTbsData() {
        return this.tbsData;
    }

    public SignerIdentifier getSigner() {
        return this.signer;
    }

    public Signature getSignature() {
        return this.signature;
    }

    public static Builder builder() {
        return new Builder();
    }
}
