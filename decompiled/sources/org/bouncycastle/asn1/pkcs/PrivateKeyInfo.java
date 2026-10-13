package org.bouncycastle.asn1.pkcs;

import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/pkcs/PrivateKeyInfo.class */
public class PrivateKeyInfo extends ASN1Object {
    private ASN1Integer version;
    private AlgorithmIdentifier privateKeyAlgorithm;
    private ASN1OctetString privateKey;
    private ASN1Set attributes;
    private ASN1BitString publicKey;

    public static PrivateKeyInfo getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    public static PrivateKeyInfo getInstance(Object obj) {
        if (obj instanceof PrivateKeyInfo) {
            return (PrivateKeyInfo) obj;
        }
        if (obj != null) {
            return new PrivateKeyInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private static int getVersionValue(ASN1Integer aSN1Integer) {
        int iIntValueExact = aSN1Integer.intValueExact();
        if (iIntValueExact < 0 || iIntValueExact > 1) {
            throw new IllegalArgumentException("invalid version for private key info");
        }
        return iIntValueExact;
    }

    public PrivateKeyInfo(AlgorithmIdentifier algorithmIdentifier, byte[] bArr) throws IOException {
        this(algorithmIdentifier, bArr, (ASN1Set) null, (byte[]) null);
    }

    public PrivateKeyInfo(AlgorithmIdentifier algorithmIdentifier, ASN1Encodable aSN1Encodable) throws IOException {
        this(algorithmIdentifier, aSN1Encodable, (ASN1Set) null, (byte[]) null);
    }

    public PrivateKeyInfo(AlgorithmIdentifier algorithmIdentifier, ASN1Encodable aSN1Encodable, ASN1Set aSN1Set) throws IOException {
        this(algorithmIdentifier, aSN1Encodable, aSN1Set, (byte[]) null);
    }

    public PrivateKeyInfo(AlgorithmIdentifier algorithmIdentifier, byte[] bArr, ASN1Set aSN1Set) throws IOException {
        this(algorithmIdentifier, bArr, aSN1Set, (byte[]) null);
    }

    public PrivateKeyInfo(AlgorithmIdentifier algorithmIdentifier, ASN1Encodable aSN1Encodable, ASN1Set aSN1Set, byte[] bArr) throws IOException {
        this(algorithmIdentifier, aSN1Encodable.toASN1Primitive().getEncoded("DER"), aSN1Set, bArr);
    }

    public PrivateKeyInfo(AlgorithmIdentifier algorithmIdentifier, byte[] bArr, ASN1Set aSN1Set, byte[] bArr2) {
        this.version = new ASN1Integer(bArr2 != null ? BigIntegers.ONE : BigIntegers.ZERO);
        this.privateKeyAlgorithm = algorithmIdentifier;
        this.privateKey = new DEROctetString(bArr);
        this.attributes = aSN1Set;
        this.publicKey = bArr2 == null ? null : new DERBitString(bArr2);
    }

    private PrivateKeyInfo(ASN1Sequence aSN1Sequence) {
        Enumeration objects = aSN1Sequence.getObjects();
        this.version = ASN1Integer.getInstance(objects.nextElement());
        int versionValue = getVersionValue(this.version);
        this.privateKeyAlgorithm = AlgorithmIdentifier.getInstance(objects.nextElement());
        this.privateKey = ASN1OctetString.getInstance(objects.nextElement());
        int i = -1;
        while (objects.hasMoreElements()) {
            ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) objects.nextElement();
            int tagNo = aSN1TaggedObject.getTagNo();
            if (tagNo <= i) {
                throw new IllegalArgumentException("invalid optional field in private key info");
            }
            i = tagNo;
            switch (tagNo) {
                case 0:
                    this.attributes = ASN1Set.getInstance(aSN1TaggedObject, false);
                    break;
                case 1:
                    if (versionValue < 1) {
                        throw new IllegalArgumentException("'publicKey' requires version v2(1) or later");
                    }
                    this.publicKey = ASN1BitString.getInstance(aSN1TaggedObject, false);
                    break;
                default:
                    throw new IllegalArgumentException("unknown optional field in private key info");
            }
        }
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public ASN1Set getAttributes() {
        return this.attributes;
    }

    public AlgorithmIdentifier getPrivateKeyAlgorithm() {
        return this.privateKeyAlgorithm;
    }

    public ASN1OctetString getPrivateKey() {
        return new DEROctetString(this.privateKey.getOctets());
    }

    public int getPrivateKeyLength() {
        return this.privateKey.getOctetsLength();
    }

    public ASN1Encodable parsePrivateKey() throws IOException {
        return ASN1Primitive.fromByteArray(this.privateKey.getOctets());
    }

    public boolean hasPublicKey() {
        return this.publicKey != null;
    }

    public ASN1Encodable parsePublicKey() throws IOException {
        if (this.publicKey == null) {
            return null;
        }
        return ASN1Primitive.fromByteArray(this.publicKey.getOctets());
    }

    public ASN1BitString getPublicKeyData() {
        return this.publicKey;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(5);
        aSN1EncodableVector.add(this.version);
        aSN1EncodableVector.add(this.privateKeyAlgorithm);
        aSN1EncodableVector.add(this.privateKey);
        if (this.attributes != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 0, (ASN1Encodable) this.attributes));
        }
        if (this.publicKey != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 1, (ASN1Encodable) this.publicKey));
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
