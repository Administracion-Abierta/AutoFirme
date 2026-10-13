package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/bc/EncryptedObjectStoreData.class */
public class EncryptedObjectStoreData extends ASN1Object {
    private final AlgorithmIdentifier encryptionAlgorithm;
    private final ASN1OctetString encryptedContent;

    public EncryptedObjectStoreData(AlgorithmIdentifier algorithmIdentifier, byte[] bArr) {
        this.encryptionAlgorithm = algorithmIdentifier;
        this.encryptedContent = new DEROctetString(Arrays.clone(bArr));
    }

    private EncryptedObjectStoreData(ASN1Sequence aSN1Sequence) {
        this.encryptionAlgorithm = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(0));
        this.encryptedContent = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static EncryptedObjectStoreData getInstance(Object obj) {
        if (obj instanceof EncryptedObjectStoreData) {
            return (EncryptedObjectStoreData) obj;
        }
        if (obj != null) {
            return new EncryptedObjectStoreData(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1OctetString getEncryptedContent() {
        return this.encryptedContent;
    }

    public AlgorithmIdentifier getEncryptionAlgorithm() {
        return this.encryptionAlgorithm;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.encryptionAlgorithm, this.encryptedContent);
    }
}
