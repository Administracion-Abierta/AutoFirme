package org.spongycastle.asn1.bc;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/bc/EncryptedObjectStoreData.class */
public class EncryptedObjectStoreData extends ASN1Object {
    private final AlgorithmIdentifier encryptionAlgorithm;
    private final ASN1OctetString encryptedContent;

    public EncryptedObjectStoreData(AlgorithmIdentifier encryptionAlgorithm, byte[] encryptedContent) {
        this.encryptionAlgorithm = encryptionAlgorithm;
        this.encryptedContent = new DEROctetString(encryptedContent);
    }

    private EncryptedObjectStoreData(ASN1Sequence seq) {
        this.encryptionAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(0));
        this.encryptedContent = ASN1OctetString.getInstance(seq.getObjectAt(1));
    }

    public static EncryptedObjectStoreData getInstance(Object o) {
        if (o instanceof EncryptedObjectStoreData) {
            return (EncryptedObjectStoreData) o;
        }
        if (o != null) {
            return new EncryptedObjectStoreData(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public ASN1OctetString getEncryptedContent() {
        return this.encryptedContent;
    }

    public AlgorithmIdentifier getEncryptionAlgorithm() {
        return this.encryptionAlgorithm;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.encryptionAlgorithm);
        v.add(this.encryptedContent);
        return new DERSequence(v);
    }
}
