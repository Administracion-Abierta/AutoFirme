package org.spongycastle.asn1.cryptopro;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cryptopro/GOST3410PublicKeyAlgParameters.class */
public class GOST3410PublicKeyAlgParameters extends ASN1Object {
    private ASN1ObjectIdentifier publicKeyParamSet;
    private ASN1ObjectIdentifier digestParamSet;
    private ASN1ObjectIdentifier encryptionParamSet;

    public static GOST3410PublicKeyAlgParameters getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
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

    public GOST3410PublicKeyAlgParameters(ASN1ObjectIdentifier publicKeyParamSet, ASN1ObjectIdentifier digestParamSet) {
        this.publicKeyParamSet = publicKeyParamSet;
        this.digestParamSet = digestParamSet;
        this.encryptionParamSet = null;
    }

    public GOST3410PublicKeyAlgParameters(ASN1ObjectIdentifier publicKeyParamSet, ASN1ObjectIdentifier digestParamSet, ASN1ObjectIdentifier encryptionParamSet) {
        this.publicKeyParamSet = publicKeyParamSet;
        this.digestParamSet = digestParamSet;
        this.encryptionParamSet = encryptionParamSet;
    }

    public GOST3410PublicKeyAlgParameters(ASN1Sequence seq) {
        this.publicKeyParamSet = (ASN1ObjectIdentifier) seq.getObjectAt(0);
        this.digestParamSet = (ASN1ObjectIdentifier) seq.getObjectAt(1);
        if (seq.size() > 2) {
            this.encryptionParamSet = (ASN1ObjectIdentifier) seq.getObjectAt(2);
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

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.publicKeyParamSet);
        v.add(this.digestParamSet);
        if (this.encryptionParamSet != null) {
            v.add(this.encryptionParamSet);
        }
        return new DERSequence(v);
    }
}
