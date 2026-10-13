package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/crmf/EncryptedValue.class */
public class EncryptedValue extends ASN1Object {
    private AlgorithmIdentifier intendedAlg;
    private AlgorithmIdentifier symmAlg;
    private ASN1BitString encSymmKey;
    private AlgorithmIdentifier keyAlg;
    private ASN1OctetString valueHint;
    private ASN1BitString encValue;

    private EncryptedValue(ASN1Sequence aSN1Sequence) {
        int i = 0;
        while (aSN1Sequence.getObjectAt(i) instanceof ASN1TaggedObject) {
            ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) aSN1Sequence.getObjectAt(i);
            switch (aSN1TaggedObject.getTagNo()) {
                case 0:
                    this.intendedAlg = AlgorithmIdentifier.getInstance(aSN1TaggedObject, false);
                    break;
                case 1:
                    this.symmAlg = AlgorithmIdentifier.getInstance(aSN1TaggedObject, false);
                    break;
                case 2:
                    this.encSymmKey = ASN1BitString.getInstance(aSN1TaggedObject, false);
                    break;
                case 3:
                    this.keyAlg = AlgorithmIdentifier.getInstance(aSN1TaggedObject, false);
                    break;
                case 4:
                    this.valueHint = ASN1OctetString.getInstance(aSN1TaggedObject, false);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown tag encountered: " + aSN1TaggedObject.getTagNo());
            }
            i++;
        }
        this.encValue = ASN1BitString.getInstance(aSN1Sequence.getObjectAt(i));
    }

    public static EncryptedValue getInstance(Object obj) {
        if (obj instanceof EncryptedValue) {
            return (EncryptedValue) obj;
        }
        if (obj != null) {
            return new EncryptedValue(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public EncryptedValue(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2, ASN1BitString aSN1BitString, AlgorithmIdentifier algorithmIdentifier3, ASN1OctetString aSN1OctetString, ASN1BitString aSN1BitString2) {
        if (aSN1BitString2 == null) {
            throw new IllegalArgumentException("'encValue' cannot be null");
        }
        this.intendedAlg = algorithmIdentifier;
        this.symmAlg = algorithmIdentifier2;
        this.encSymmKey = aSN1BitString;
        this.keyAlg = algorithmIdentifier3;
        this.valueHint = aSN1OctetString;
        this.encValue = aSN1BitString2;
    }

    public AlgorithmIdentifier getIntendedAlg() {
        return this.intendedAlg;
    }

    public AlgorithmIdentifier getSymmAlg() {
        return this.symmAlg;
    }

    public ASN1BitString getEncSymmKey() {
        return this.encSymmKey;
    }

    public AlgorithmIdentifier getKeyAlg() {
        return this.keyAlg;
    }

    public ASN1OctetString getValueHint() {
        return this.valueHint;
    }

    public ASN1BitString getEncValue() {
        return this.encValue;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(6);
        addOptional(aSN1EncodableVector, 0, this.intendedAlg);
        addOptional(aSN1EncodableVector, 1, this.symmAlg);
        addOptional(aSN1EncodableVector, 2, this.encSymmKey);
        addOptional(aSN1EncodableVector, 3, this.keyAlg);
        addOptional(aSN1EncodableVector, 4, this.valueHint);
        aSN1EncodableVector.add(this.encValue);
        return new DERSequence(aSN1EncodableVector);
    }

    private void addOptional(ASN1EncodableVector aSN1EncodableVector, int i, ASN1Encodable aSN1Encodable) {
        if (aSN1Encodable != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, i, aSN1Encodable));
        }
    }
}
