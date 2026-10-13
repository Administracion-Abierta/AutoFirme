package org.bouncycastle.oer.its.etsi102941.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/basetypes/PublicKeys.class */
public class PublicKeys extends ASN1Object {
    private final PublicVerificationKey verificationKey;
    private final PublicEncryptionKey encryptionKey;

    public PublicKeys(PublicVerificationKey publicVerificationKey, PublicEncryptionKey publicEncryptionKey) {
        this.verificationKey = publicVerificationKey;
        this.encryptionKey = publicEncryptionKey;
    }

    public static PublicKeys getInstance(Object obj) {
        if (obj instanceof PublicKeys) {
            return (PublicKeys) obj;
        }
        if (obj != null) {
            return new PublicKeys(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private PublicKeys(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.verificationKey = PublicVerificationKey.getInstance(aSN1Sequence.getObjectAt(0));
        this.encryptionKey = (PublicEncryptionKey) OEROptional.getValue(PublicEncryptionKey.class, aSN1Sequence.getObjectAt(1));
    }

    public PublicVerificationKey getVerificationKey() {
        return this.verificationKey;
    }

    public PublicEncryptionKey getEncryptionKey() {
        return this.encryptionKey;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.verificationKey, OEROptional.getInstance(this.encryptionKey)});
    }
}
