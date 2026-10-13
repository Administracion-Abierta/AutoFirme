package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cms/OriginatorPublicKey.class */
public class OriginatorPublicKey extends ASN1Object {
    private AlgorithmIdentifier algorithm;
    private ASN1BitString publicKey;

    public OriginatorPublicKey(AlgorithmIdentifier algorithmIdentifier, byte[] bArr) {
        this.algorithm = algorithmIdentifier;
        this.publicKey = new DERBitString(bArr);
    }

    public OriginatorPublicKey(AlgorithmIdentifier algorithmIdentifier, ASN1BitString aSN1BitString) {
        this.algorithm = algorithmIdentifier;
        this.publicKey = aSN1BitString;
    }

    private OriginatorPublicKey(ASN1Sequence aSN1Sequence) {
        this.algorithm = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(0));
        this.publicKey = (DERBitString) aSN1Sequence.getObjectAt(1);
    }

    public static OriginatorPublicKey getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return new OriginatorPublicKey(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    public static OriginatorPublicKey getInstance(Object obj) {
        if (obj instanceof OriginatorPublicKey) {
            return (OriginatorPublicKey) obj;
        }
        if (obj != null) {
            return new OriginatorPublicKey(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public AlgorithmIdentifier getAlgorithm() {
        return this.algorithm;
    }

    public DERBitString getPublicKey() {
        return DERBitString.convert(this.publicKey);
    }

    public ASN1BitString getPublicKeyData() {
        return this.publicKey;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.algorithm, this.publicKey);
    }
}
