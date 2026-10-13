package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/SignedDataPayload.class */
public class SignedDataPayload extends ASN1Object {
    private final Ieee1609Dot2Data data;
    private final HashedData extDataHash;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/SignedDataPayload$Builder.class */
    public static class Builder {
        private Ieee1609Dot2Data data;
        private HashedData extDataHash;

        public Builder setData(Ieee1609Dot2Data ieee1609Dot2Data) {
            this.data = ieee1609Dot2Data;
            return this;
        }

        public Builder setExtDataHash(HashedData hashedData) {
            this.extDataHash = hashedData;
            return this;
        }

        public SignedDataPayload createSignedDataPayload() {
            return new SignedDataPayload(this.data, this.extDataHash);
        }
    }

    public SignedDataPayload(Ieee1609Dot2Data ieee1609Dot2Data, HashedData hashedData) {
        this.data = ieee1609Dot2Data;
        this.extDataHash = hashedData;
    }

    private SignedDataPayload(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.data = (Ieee1609Dot2Data) OEROptional.getValue(Ieee1609Dot2Data.class, aSN1Sequence.getObjectAt(0));
        this.extDataHash = (HashedData) OEROptional.getValue(HashedData.class, aSN1Sequence.getObjectAt(1));
    }

    public static SignedDataPayload getInstance(Object obj) {
        if (obj instanceof SignedDataPayload) {
            return (SignedDataPayload) obj;
        }
        if (obj != null) {
            return new SignedDataPayload(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{OEROptional.getInstance(this.data), OEROptional.getInstance(this.extDataHash)});
    }

    public Ieee1609Dot2Data getData() {
        return this.data;
    }

    public HashedData getExtDataHash() {
        return this.extDataHash;
    }
}
