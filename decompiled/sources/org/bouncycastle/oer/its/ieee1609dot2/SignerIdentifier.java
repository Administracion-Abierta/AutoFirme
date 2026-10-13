package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/SignerIdentifier.class */
public class SignerIdentifier extends ASN1Object implements ASN1Choice {
    public static final int digest = 0;
    public static final int certificate = 1;
    public static final int self = 2;
    private final int choice;
    private final ASN1Encodable signerIdentifier;

    public SignerIdentifier(int i, ASN1Encodable aSN1Encodable) {
        this.choice = i;
        this.signerIdentifier = aSN1Encodable;
    }

    private SignerIdentifier(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        switch (this.choice) {
            case 0:
                this.signerIdentifier = HashedId8.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            case 1:
                this.signerIdentifier = SequenceOfCertificate.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            case 2:
                this.signerIdentifier = DERNull.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            default:
                throw new IllegalArgumentException("invalid choice value " + this.choice);
        }
    }

    public static SignerIdentifier getInstance(Object obj) {
        if (obj instanceof SignerIdentifier) {
            return (SignerIdentifier) obj;
        }
        if (obj != null) {
            return new SignerIdentifier(ASN1TaggedObject.getInstance(obj, 128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public static SignerIdentifier digest(HashedId8 hashedId8) {
        return new SignerIdentifier(0, hashedId8);
    }

    public static SignerIdentifier certificate(SequenceOfCertificate sequenceOfCertificate) {
        return new SignerIdentifier(1, sequenceOfCertificate);
    }

    public static SignerIdentifier self() {
        return new SignerIdentifier(2, DERNull.INSTANCE);
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.signerIdentifier);
    }

    public ASN1Encodable getSignerIdentifier() {
        return this.signerIdentifier;
    }
}
