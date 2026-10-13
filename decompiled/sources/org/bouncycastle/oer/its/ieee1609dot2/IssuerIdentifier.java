package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashAlgorithm;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/IssuerIdentifier.class */
public class IssuerIdentifier extends ASN1Object implements ASN1Choice {
    public static final int sha256AndDigest = 0;
    public static final int self = 1;
    public static final int sha384AndDigest = 2;
    private final int choice;
    private final ASN1Encodable issuerIdentifier;

    public static IssuerIdentifier sha256AndDigest(HashedId8 hashedId8) {
        return new IssuerIdentifier(0, hashedId8);
    }

    public static IssuerIdentifier self(HashAlgorithm hashAlgorithm) {
        return new IssuerIdentifier(1, hashAlgorithm);
    }

    public static IssuerIdentifier sha384AndDigest(HashedId8 hashedId8) {
        return new IssuerIdentifier(2, hashedId8);
    }

    public IssuerIdentifier(int i, ASN1Encodable aSN1Encodable) {
        this.choice = i;
        this.issuerIdentifier = aSN1Encodable;
    }

    private IssuerIdentifier(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        ASN1Object explicitBaseObject = aSN1TaggedObject.getExplicitBaseObject();
        switch (this.choice) {
            case 0:
            case 2:
                this.issuerIdentifier = HashedId8.getInstance(explicitBaseObject);
                return;
            case 1:
                this.issuerIdentifier = HashAlgorithm.getInstance((Object) explicitBaseObject);
                return;
            default:
                throw new IllegalArgumentException("invalid choice value " + this.choice);
        }
    }

    public static IssuerIdentifier getInstance(Object obj) {
        if (obj instanceof IssuerIdentifier) {
            return (IssuerIdentifier) obj;
        }
        if (obj != null) {
            return new IssuerIdentifier(ASN1TaggedObject.getInstance(obj, 128));
        }
        return null;
    }

    public boolean isSelf() {
        return this.choice == 1;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getIssuerIdentifier() {
        return this.issuerIdentifier;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.issuerIdentifier);
    }
}
