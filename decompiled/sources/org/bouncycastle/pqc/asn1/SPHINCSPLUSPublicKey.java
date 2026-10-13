package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/asn1/SPHINCSPLUSPublicKey.class */
public class SPHINCSPLUSPublicKey extends ASN1Object {
    private byte[] pkseed;
    private byte[] pkroot;

    public SPHINCSPLUSPublicKey(byte[] bArr, byte[] bArr2) {
        this.pkseed = bArr;
        this.pkroot = bArr2;
    }

    public SPHINCSPLUSPublicKey(ASN1Sequence aSN1Sequence) {
        this.pkseed = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0)).getOctets());
        this.pkroot = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets());
    }

    public byte[] getPkseed() {
        return Arrays.clone(this.pkseed);
    }

    public byte[] getPkroot() {
        return Arrays.clone(this.pkroot);
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(new DEROctetString(this.pkseed));
        aSN1EncodableVector.add(new DEROctetString(this.pkroot));
        return new DERSequence(aSN1EncodableVector);
    }

    public static SPHINCSPLUSPublicKey getInstance(Object obj) {
        if (obj instanceof SPHINCSPLUSPublicKey) {
            return (SPHINCSPLUSPublicKey) obj;
        }
        if (obj != null) {
            return new SPHINCSPLUSPublicKey(ASN1Sequence.getInstance(obj));
        }
        return null;
    }
}
