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

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/asn1/CMCEPrivateKey.class */
public class CMCEPrivateKey extends ASN1Object {
    private int version;
    private byte[] delta;
    private byte[] C;
    private byte[] g;
    private byte[] alpha;
    private byte[] s;
    private CMCEPublicKey PublicKey;

    public CMCEPrivateKey(int i, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5) {
        this(i, bArr, bArr2, bArr3, bArr4, bArr5, null);
    }

    public CMCEPrivateKey(int i, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5, CMCEPublicKey cMCEPublicKey) {
        this.version = i;
        if (i != 0) {
            throw new IllegalArgumentException("unrecognized version");
        }
        this.delta = Arrays.clone(bArr);
        this.C = Arrays.clone(bArr2);
        this.g = Arrays.clone(bArr3);
        this.alpha = Arrays.clone(bArr4);
        this.s = Arrays.clone(bArr5);
        this.PublicKey = cMCEPublicKey;
    }

    private CMCEPrivateKey(ASN1Sequence aSN1Sequence) {
        this.version = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0)).intValueExact();
        if (this.version != 0) {
            throw new IllegalArgumentException("unrecognized version");
        }
        this.delta = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets());
        this.C = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(2)).getOctets());
        this.g = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(3)).getOctets());
        this.alpha = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(4)).getOctets());
        this.s = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(5)).getOctets());
        if (aSN1Sequence.size() == 7) {
            this.PublicKey = CMCEPublicKey.getInstance(aSN1Sequence.getObjectAt(6));
        }
    }

    public int getVersion() {
        return this.version;
    }

    public byte[] getDelta() {
        return Arrays.clone(this.delta);
    }

    public byte[] getC() {
        return Arrays.clone(this.C);
    }

    public byte[] getG() {
        return Arrays.clone(this.g);
    }

    public byte[] getAlpha() {
        return Arrays.clone(this.alpha);
    }

    public byte[] getS() {
        return Arrays.clone(this.s);
    }

    public CMCEPublicKey getPublicKey() {
        return this.PublicKey;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(new ASN1Integer(this.version));
        aSN1EncodableVector.add(new DEROctetString(this.delta));
        aSN1EncodableVector.add(new DEROctetString(this.C));
        aSN1EncodableVector.add(new DEROctetString(this.g));
        aSN1EncodableVector.add(new DEROctetString(this.alpha));
        aSN1EncodableVector.add(new DEROctetString(this.s));
        if (this.PublicKey != null) {
            aSN1EncodableVector.add(new CMCEPublicKey(this.PublicKey.getT()));
        }
        return new DERSequence(aSN1EncodableVector);
    }

    public static CMCEPrivateKey getInstance(Object obj) {
        if (obj instanceof CMCEPrivateKey) {
            return (CMCEPrivateKey) obj;
        }
        if (obj != null) {
            return new CMCEPrivateKey(ASN1Sequence.getInstance(obj));
        }
        return null;
    }
}
