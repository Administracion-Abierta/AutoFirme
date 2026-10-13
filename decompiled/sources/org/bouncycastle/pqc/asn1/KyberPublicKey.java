package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/asn1/KyberPublicKey.class */
public class KyberPublicKey extends ASN1Object {
    private byte[] t;
    private byte[] rho;

    public KyberPublicKey(byte[] bArr, byte[] bArr2) {
        this.t = bArr;
        this.rho = bArr2;
    }

    public KyberPublicKey(ASN1Sequence aSN1Sequence) {
        this.t = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0)).getOctets());
        this.rho = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets());
    }

    public byte[] getT() {
        return Arrays.clone(this.t);
    }

    public byte[] getRho() {
        return Arrays.clone(this.rho);
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(new DEROctetString(this.t));
        aSN1EncodableVector.add(new DEROctetString(this.rho));
        return new DERSequence(aSN1EncodableVector);
    }

    public static KyberPublicKey getInstance(Object obj) {
        if (obj instanceof KyberPublicKey) {
            return (KyberPublicKey) obj;
        }
        if (obj != null) {
            return new KyberPublicKey(ASN1Sequence.getInstance(obj));
        }
        return null;
    }
}
