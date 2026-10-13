package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/asn1/SABERPrivateKey.class */
public class SABERPrivateKey extends ASN1Object {
    private int version;
    private byte[] z;
    private byte[] s;
    private byte[] hpk;
    private SABERPublicKey PublicKey;

    public SABERPrivateKey(int i, byte[] bArr, byte[] bArr2, byte[] bArr3) {
        this.version = i;
        if (i != 0) {
            throw new IllegalArgumentException("unrecognized version");
        }
        this.z = bArr;
        this.s = bArr2;
        this.hpk = bArr3;
    }

    public SABERPrivateKey(int i, byte[] bArr, byte[] bArr2, byte[] bArr3, SABERPublicKey sABERPublicKey) {
        this.version = i;
        if (i != 0) {
            throw new IllegalArgumentException("unrecognized version");
        }
        this.z = bArr;
        this.s = bArr2;
        this.hpk = bArr3;
        this.PublicKey = sABERPublicKey;
    }

    private SABERPrivateKey(ASN1Sequence aSN1Sequence) {
        this.version = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0)).intValueExact();
        if (this.version != 0) {
            throw new IllegalArgumentException("unrecognized version");
        }
        this.z = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets());
        this.s = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(2)).getOctets());
        this.PublicKey = SABERPublicKey.getInstance(aSN1Sequence.getObjectAt(3));
        this.hpk = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(4)).getOctets());
    }

    public int getVersion() {
        return this.version;
    }

    public byte[] getZ() {
        return this.z;
    }

    public byte[] getS() {
        return this.s;
    }

    public byte[] getHpk() {
        return this.hpk;
    }

    public SABERPublicKey getPublicKey() {
        return this.PublicKey;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(new ASN1Integer(this.version));
        aSN1EncodableVector.add(new DEROctetString(this.z));
        aSN1EncodableVector.add(new DEROctetString(this.s));
        aSN1EncodableVector.add(new DEROctetString(this.hpk));
        return new DERSequence(aSN1EncodableVector);
    }

    public static SABERPrivateKey getInstance(Object obj) {
        if (obj instanceof SABERPrivateKey) {
            return (SABERPrivateKey) obj;
        }
        if (obj != null) {
            return new SABERPrivateKey(ASN1Sequence.getInstance(obj));
        }
        return null;
    }
}
