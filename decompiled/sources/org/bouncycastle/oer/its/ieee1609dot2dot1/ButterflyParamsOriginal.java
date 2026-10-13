package org.bouncycastle.oer.its.ieee1609dot2dot1;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2dot1/ButterflyParamsOriginal.class */
public class ButterflyParamsOriginal extends ASN1Object {
    private final ButterflyExpansion signingExpansion;
    private final PublicEncryptionKey encryptionKey;
    private final ButterflyExpansion encryptionExpansion;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2dot1/ButterflyParamsOriginal$Builder.class */
    public static class Builder {
        private ButterflyExpansion signingExpansion;
        private PublicEncryptionKey encryptionKey;
        private ButterflyExpansion encryptionExpansion;

        public Builder setSigningExpansion(ButterflyExpansion butterflyExpansion) {
            this.signingExpansion = butterflyExpansion;
            return this;
        }

        public Builder setEncryptionKey(PublicEncryptionKey publicEncryptionKey) {
            this.encryptionKey = publicEncryptionKey;
            return this;
        }

        public Builder setEncryptionExpansion(ButterflyExpansion butterflyExpansion) {
            this.encryptionExpansion = butterflyExpansion;
            return this;
        }

        public ButterflyParamsOriginal createButterflyParamsOriginal() {
            return new ButterflyParamsOriginal(this.signingExpansion, this.encryptionKey, this.encryptionExpansion);
        }
    }

    public ButterflyParamsOriginal(ButterflyExpansion butterflyExpansion, PublicEncryptionKey publicEncryptionKey, ButterflyExpansion butterflyExpansion2) {
        this.signingExpansion = butterflyExpansion;
        this.encryptionKey = publicEncryptionKey;
        this.encryptionExpansion = butterflyExpansion2;
    }

    private ButterflyParamsOriginal(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 3) {
            throw new IllegalArgumentException("expected sequence size of 3");
        }
        this.signingExpansion = ButterflyExpansion.getInstance(aSN1Sequence.getObjectAt(0));
        this.encryptionKey = PublicEncryptionKey.getInstance(aSN1Sequence.getObjectAt(1));
        this.encryptionExpansion = ButterflyExpansion.getInstance(aSN1Sequence.getObjectAt(2));
    }

    public static ButterflyParamsOriginal getInstance(Object obj) {
        if (obj instanceof ButterflyParamsOriginal) {
            return (ButterflyParamsOriginal) obj;
        }
        if (obj != null) {
            return new ButterflyParamsOriginal(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.signingExpansion, this.encryptionKey, this.encryptionExpansion});
    }

    public ButterflyExpansion getSigningExpansion() {
        return this.signingExpansion;
    }

    public PublicEncryptionKey getEncryptionKey() {
        return this.encryptionKey;
    }

    public ButterflyExpansion getEncryptionExpansion() {
        return this.encryptionExpansion;
    }
}
