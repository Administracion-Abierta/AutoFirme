package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/PKRecipientInfo.class */
public class PKRecipientInfo extends ASN1Object {
    private final HashedId8 recipientId;
    private final EncryptedDataEncryptionKey encKey;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/PKRecipientInfo$Builder.class */
    public static class Builder {
        private HashedId8 recipientId;
        private EncryptedDataEncryptionKey encKey;

        public Builder setRecipientId(HashedId8 hashedId8) {
            this.recipientId = hashedId8;
            return this;
        }

        public Builder setEncKey(EncryptedDataEncryptionKey encryptedDataEncryptionKey) {
            this.encKey = encryptedDataEncryptionKey;
            return this;
        }

        public PKRecipientInfo createPKRecipientInfo() {
            return new PKRecipientInfo(this.recipientId, this.encKey);
        }
    }

    public PKRecipientInfo(HashedId8 hashedId8, EncryptedDataEncryptionKey encryptedDataEncryptionKey) {
        this.recipientId = hashedId8;
        this.encKey = encryptedDataEncryptionKey;
    }

    private PKRecipientInfo(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.recipientId = HashedId8.getInstance(aSN1Sequence.getObjectAt(0));
        this.encKey = EncryptedDataEncryptionKey.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static PKRecipientInfo getInstance(Object obj) {
        if (obj instanceof PKRecipientInfo) {
            return (PKRecipientInfo) obj;
        }
        if (obj != null) {
            return new PKRecipientInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public HashedId getRecipientId() {
        return this.recipientId;
    }

    public EncryptedDataEncryptionKey getEncKey() {
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
