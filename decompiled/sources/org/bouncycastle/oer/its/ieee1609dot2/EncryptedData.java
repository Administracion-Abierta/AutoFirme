package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/EncryptedData.class */
public class EncryptedData extends ASN1Object {
    private final SequenceOfRecipientInfo recipients;
    private final SymmetricCiphertext ciphertext;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/EncryptedData$Builder.class */
    public static class Builder {
        private SequenceOfRecipientInfo recipients;
        private SymmetricCiphertext ciphertext;

        public Builder setRecipients(SequenceOfRecipientInfo sequenceOfRecipientInfo) {
            this.recipients = sequenceOfRecipientInfo;
            return this;
        }

        public Builder setCiphertext(SymmetricCiphertext symmetricCiphertext) {
            this.ciphertext = symmetricCiphertext;
            return this;
        }

        public EncryptedData createEncryptedData() {
            return new EncryptedData(this.recipients, this.ciphertext);
        }
    }

    public EncryptedData(SequenceOfRecipientInfo sequenceOfRecipientInfo, SymmetricCiphertext symmetricCiphertext) {
        this.recipients = sequenceOfRecipientInfo;
        this.ciphertext = symmetricCiphertext;
    }

    private EncryptedData(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.recipients = SequenceOfRecipientInfo.getInstance(aSN1Sequence.getObjectAt(0));
        this.ciphertext = SymmetricCiphertext.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static EncryptedData getInstance(Object obj) {
        if (obj instanceof EncryptedData) {
            return (EncryptedData) obj;
        }
        if (obj != null) {
            return new EncryptedData(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.recipients, this.ciphertext);
    }

    public SequenceOfRecipientInfo getRecipients() {
        return this.recipients;
    }

    public SymmetricCiphertext getCiphertext() {
        return this.ciphertext;
    }

    public static Builder builder() {
        return new Builder();
    }
}
