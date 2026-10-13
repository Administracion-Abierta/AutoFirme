package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/SymmRecipientInfo.class */
public class SymmRecipientInfo extends ASN1Object {
    private final HashedId8 recipientId;
    private final SymmetricCiphertext encKey;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/SymmRecipientInfo$Builder.class */
    public static class Builder {
        private HashedId8 recipientId;
        private SymmetricCiphertext encKey;

        public Builder setRecipientId(HashedId8 hashedId8) {
            this.recipientId = hashedId8;
            return this;
        }

        public Builder setEncKey(SymmetricCiphertext symmetricCiphertext) {
            this.encKey = symmetricCiphertext;
            return this;
        }

        public SymmRecipientInfo createSymmRecipientInfo() {
            return new SymmRecipientInfo(this.recipientId, this.encKey);
        }
    }

    public SymmRecipientInfo(HashedId8 hashedId8, SymmetricCiphertext symmetricCiphertext) {
        this.recipientId = hashedId8;
        this.encKey = symmetricCiphertext;
    }

    private SymmRecipientInfo(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.recipientId = HashedId8.getInstance(aSN1Sequence.getObjectAt(0));
        this.encKey = SymmetricCiphertext.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static SymmRecipientInfo getInstance(Object obj) {
        if (obj instanceof SymmRecipientInfo) {
            return (SymmRecipientInfo) obj;
        }
        if (obj != null) {
            return new SymmRecipientInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public HashedId getRecipientId() {
        return this.recipientId;
    }

    public SymmetricCiphertext getEncKey() {
        return this.encKey;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.recipientId, this.encKey);
    }

    public static Builder builder() {
        return new Builder();
    }
}
