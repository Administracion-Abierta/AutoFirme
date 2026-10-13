package org.bouncycastle.asn1.sec;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/sec/ECPrivateKey.class */
public class ECPrivateKey extends ASN1Object {
    private ASN1Sequence seq;

    private ECPrivateKey(ASN1Sequence aSN1Sequence) {
        this.seq = aSN1Sequence;
    }

    public static ECPrivateKey getInstance(Object obj) {
        if (obj instanceof ECPrivateKey) {
            return (ECPrivateKey) obj;
        }
        if (obj != null) {
            return new ECPrivateKey(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ECPrivateKey(BigInteger bigInteger) {
        this(bigInteger.bitLength(), bigInteger);
    }

    public ECPrivateKey(int i, BigInteger bigInteger) {
        this.seq = new DERSequence(new ASN1Integer(1L), new DEROctetString(BigIntegers.asUnsignedByteArray((i + 7) / 8, bigInteger)));
    }

    public ECPrivateKey(BigInteger bigInteger, ASN1Encodable aSN1Encodable) {
        this(bigInteger, (ASN1BitString) null, aSN1Encodable);
    }

    public ECPrivateKey(BigInteger bigInteger, ASN1BitString aSN1BitString, ASN1Encodable aSN1Encodable) {
        this(bigInteger.bitLength(), bigInteger, aSN1BitString, aSN1Encodable);
    }

    public ECPrivateKey(int i, BigInteger bigInteger, ASN1Encodable aSN1Encodable) {
        this(i, bigInteger, null, aSN1Encodable);
    }

    public ECPrivateKey(int i, BigInteger bigInteger, ASN1BitString aSN1BitString, ASN1Encodable aSN1Encodable) {
        byte[] bArrAsUnsignedByteArray = BigIntegers.asUnsignedByteArray((i + 7) / 8, bigInteger);
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(4);
        aSN1EncodableVector.add(new ASN1Integer(1L));
        aSN1EncodableVector.add(new DEROctetString(bArrAsUnsignedByteArray));
        if (aSN1Encodable != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 0, aSN1Encodable));
        }
        if (aSN1BitString != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 1, (ASN1Encodable) aSN1BitString));
        }
        this.seq = new DERSequence(aSN1EncodableVector);
    }

    public BigInteger getKey() {
        return new BigInteger(1, ((ASN1OctetString) this.seq.getObjectAt(1)).getOctets());
    }

    public ASN1BitString getPublicKey() {
        return (ASN1BitString) getObjectInTag(1, 3);
    }

    public ASN1Primitive getParameters() {
        return getParametersObject().toASN1Primitive();
    }

    public ASN1Object getParametersObject() {
        return getObjectInTag(0, -1);
    }

    private ASN1Object getObjectInTag(int i, int i2) {
        Enumeration objects = this.seq.getObjects();
        while (objects.hasMoreElements()) {
            ASN1Encodable aSN1Encodable = (ASN1Encodable) objects.nextElement();
            if (aSN1Encodable instanceof ASN1TaggedObject) {
                ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) aSN1Encodable;
                if (aSN1TaggedObject.hasContextTag(i)) {
                    return i2 < 0 ? aSN1TaggedObject.getExplicitBaseObject().toASN1Primitive() : aSN1TaggedObject.getBaseUniversal(true, i2);
                }
            }
        }
        return null;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }
}
