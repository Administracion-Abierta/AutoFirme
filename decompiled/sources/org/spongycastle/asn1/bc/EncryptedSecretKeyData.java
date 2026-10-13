package org.spongycastle.asn1.bc;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/bc/EncryptedSecretKeyData.class */
public class EncryptedSecretKeyData extends ASN1Object {
    private final AlgorithmIdentifier keyEncryptionAlgorithm;
    private final ASN1OctetString encryptedKeyData;

    public EncryptedSecretKeyData(AlgorithmIdentifier keyEncryptionAlgorithm, byte[] encryptedKeyData) {
        this.keyEncryptionAlgorithm = keyEncryptionAlgorithm;
        this.encryptedKeyData = new DEROctetString(Arrays.clone(encryptedKeyData));
    }

    private EncryptedSecretKeyData(ASN1Sequence seq) {
        this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(0));
        this.encryptedKeyData = ASN1OctetString.getInstance(seq.getObjectAt(1));
    }

    public static EncryptedSecretKeyData getInstance(Object o) {
        if (o instanceof EncryptedSecretKeyData) {
            return (EncryptedSecretKeyData) o;
        }
        if (o != null) {
            return new EncryptedSecretKeyData(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public AlgorithmIdentifier getKeyEncryptionAlgorithm() {
        return this.keyEncryptionAlgorithm;
    }

    public byte[] getEncryptedKeyData() {
        return Arrays.clone(this.encryptedKeyData.getOctets());
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.keyEncryptionAlgorithm);
        v.add(this.encryptedKeyData);
        return new DERSequence(v);
    }
}
