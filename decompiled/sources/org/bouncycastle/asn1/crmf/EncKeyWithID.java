package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.GeneralName;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/crmf/EncKeyWithID.class */
public class EncKeyWithID extends ASN1Object {
    private final PrivateKeyInfo privKeyInfo;
    private final ASN1Encodable identifier;

    public static EncKeyWithID getInstance(Object obj) {
        if (obj instanceof EncKeyWithID) {
            return (EncKeyWithID) obj;
        }
        if (obj != null) {
            return new EncKeyWithID(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private EncKeyWithID(ASN1Sequence aSN1Sequence) {
        this.privKeyInfo = PrivateKeyInfo.getInstance(aSN1Sequence.getObjectAt(0));
        if (aSN1Sequence.size() <= 1) {
            this.identifier = null;
        } else if (aSN1Sequence.getObjectAt(1) instanceof ASN1UTF8String) {
            this.identifier = aSN1Sequence.getObjectAt(1);
        } else {
            this.identifier = GeneralName.getInstance(aSN1Sequence.getObjectAt(1));
        }
    }

    public EncKeyWithID(PrivateKeyInfo privateKeyInfo) {
        this.privKeyInfo = privateKeyInfo;
        this.identifier = null;
    }

    public EncKeyWithID(PrivateKeyInfo privateKeyInfo, ASN1UTF8String aSN1UTF8String) {
        this.privKeyInfo = privateKeyInfo;
        this.identifier = aSN1UTF8String;
    }

    public EncKeyWithID(PrivateKeyInfo privateKeyInfo, GeneralName generalName) {
        this.privKeyInfo = privateKeyInfo;
        this.identifier = generalName;
    }

    public PrivateKeyInfo getPrivateKey() {
        return this.privKeyInfo;
    }

    public boolean hasIdentifier() {
        return this.identifier != null;
    }

    public boolean isIdentifierUTF8String() {
        return this.identifier instanceof ASN1UTF8String;
    }

    public ASN1Encodable getIdentifier() {
        return this.identifier;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(2);
        aSN1EncodableVector.add(this.privKeyInfo);
        if (this.identifier != null) {
            aSN1EncodableVector.add(this.identifier);
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
