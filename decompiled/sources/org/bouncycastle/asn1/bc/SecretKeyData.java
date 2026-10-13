package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/bc/SecretKeyData.class */
public class SecretKeyData extends ASN1Object {
    private final ASN1ObjectIdentifier keyAlgorithm;
    private final ASN1OctetString keyBytes;

    public SecretKeyData(ASN1ObjectIdentifier aSN1ObjectIdentifier, byte[] bArr) {
        this.keyAlgorithm = aSN1ObjectIdentifier;
        this.keyBytes = new DEROctetString(Arrays.clone(bArr));
    }

    private SecretKeyData(ASN1Sequence aSN1Sequence) {
        this.keyAlgorithm = ASN1ObjectIdentifier.getInstance(aSN1Sequence.getObjectAt(0));
        this.keyBytes = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static SecretKeyData getInstance(Object obj) {
        if (obj instanceof SecretKeyData) {
            return (SecretKeyData) obj;
        }
        if (obj != null) {
            return new SecretKeyData(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public byte[] getKeyBytes() {
        return Arrays.clone(this.keyBytes.getOctets());
    }

    public ASN1ObjectIdentifier getKeyAlgorithm() {
        return this.keyAlgorithm;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.keyAlgorithm, this.keyBytes);
    }
}
