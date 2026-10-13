package org.bouncycastle.asn1.cryptopro;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.internal.asn1.rosstandart.RosstandartObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cryptopro/GOST3410PublicKeyAlgParameters.class */
public class GOST3410PublicKeyAlgParameters extends ASN1Object {
    private ASN1ObjectIdentifier publicKeyParamSet;
    private ASN1ObjectIdentifier digestParamSet;
    private ASN1ObjectIdentifier encryptionParamSet;

    public static GOST3410PublicKeyAlgParameters getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    public static GOST3410PublicKeyAlgParameters getInstance(Object obj) {
        if (obj instanceof GOST3410PublicKeyAlgParameters) {
            return (GOST3410PublicKeyAlgParameters) obj;
        }
        if (obj != null) {
            return new GOST3410PublicKeyAlgParameters(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public GOST3410PublicKeyAlgParameters(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1ObjectIdentifier aSN1ObjectIdentifier2) {
        this.publicKeyParamSet = aSN1ObjectIdentifier;
        this.digestParamSet = aSN1ObjectIdentifier2;
        this.encryptionParamSet = null;
    }

    public GOST3410PublicKeyAlgParameters(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1ObjectIdentifier aSN1ObjectIdentifier2, ASN1ObjectIdentifier aSN1ObjectIdentifier3) {
        this.publicKeyParamSet = aSN1ObjectIdentifier;
        this.digestParamSet = aSN1ObjectIdentifier2;
        this.encryptionParamSet = aSN1ObjectIdentifier3;
    }

    private GOST3410PublicKeyAlgParameters(ASN1Sequence aSN1Sequence) {
        this.publicKeyParamSet = ASN1ObjectIdentifier.getInstance(aSN1Sequence.getObjectAt(0));
        if (this.publicKeyParamSet.equals((ASN1Primitive) RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256_paramSetA)) {
            if (aSN1Sequence.size() > 1) {
                this.digestParamSet = ASN1ObjectIdentifier.getInstance(aSN1Sequence.getObjectAt(1));
            }
        } else if (this.publicKeyParamSet.equals((ASN1Primitive) RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256_paramSetB) || this.publicKeyParamSet.equals((ASN1Primitive) RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256_paramSetC) || this.publicKeyParamSet.equals((ASN1Primitive) RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256_paramSetD)) {
            if (aSN1Sequence.size() > 1) {
                throw new IllegalArgumentException("digestParamSet expected to be absent");
            }
        } else if (aSN1Sequence.size() > 1) {
            this.digestParamSet = ASN1ObjectIdentifier.getInstance(aSN1Sequence.getObjectAt(1));
        }
        if (aSN1Sequence.size() > 2) {
            this.encryptionParamSet = (ASN1ObjectIdentifier) aSN1Sequence.getObjectAt(2);
        }
    }

    public ASN1ObjectIdentifier getPublicKeyParamSet() {
        return this.publicKeyParamSet;
    }

    public ASN1ObjectIdentifier getDigestParamSet() {
        return this.digestParamSet;
    }

    public ASN1ObjectIdentifier getEncryptionParamSet() {
        return this.encryptionParamSet;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        aSN1EncodableVector.add(this.publicKeyParamSet);
        if (this.digestParamSet != null) {
            aSN1EncodableVector.add(this.digestParamSet);
        }
        if (this.encryptionParamSet != null) {
            aSN1EncodableVector.add(this.encryptionParamSet);
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
