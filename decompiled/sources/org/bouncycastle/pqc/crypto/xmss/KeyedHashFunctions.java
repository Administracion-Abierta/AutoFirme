package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Xof;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xmss/KeyedHashFunctions.class */
final class KeyedHashFunctions {
    private final Digest digest;
    private final int digestSize;

    protected KeyedHashFunctions(ASN1ObjectIdentifier aSN1ObjectIdentifier, int i) {
        if (aSN1ObjectIdentifier == null) {
            throw new NullPointerException("digest == null");
        }
        this.digest = DigestUtil.getDigest(aSN1ObjectIdentifier);
        this.digestSize = i;
    }

    private byte[] coreDigest(int i, byte[] bArr, byte[] bArr2) {
        byte[] bytesBigEndian = XMSSUtil.toBytesBigEndian(i, this.digestSize);
        this.digest.update(bytesBigEndian, 0, bytesBigEndian.length);
        this.digest.update(bArr, 0, bArr.length);
        this.digest.update(bArr2, 0, bArr2.length);
        byte[] bArr3 = new byte[this.digestSize];
        if (this.digest instanceof Xof) {
            ((Xof) this.digest).doFinal(bArr3, 0, this.digestSize);
        } else {
            this.digest.doFinal(bArr3, 0);
        }
        return bArr3;
    }

    protected byte[] F(byte[] bArr, byte[] bArr2) {
        if (bArr.length != this.digestSize) {
            throw new IllegalArgumentException("wrong key length");
        }
        if (bArr2.length != this.digestSize) {
            throw new IllegalArgumentException("wrong in length");
        }
        return coreDigest(0, bArr, bArr2);
    }

    protected byte[] H(byte[] bArr, byte[] bArr2) {
        if (bArr.length != this.digestSize) {
            throw new IllegalArgumentException("wrong key length");
        }
        if (bArr2.length != 2 * this.digestSize) {
            throw new IllegalArgumentException("wrong in length");
        }
        return coreDigest(1, bArr, bArr2);
    }

    protected byte[] HMsg(byte[] bArr, byte[] bArr2) {
        if (bArr.length != 3 * this.digestSize) {
            throw new IllegalArgumentException("wrong key length");
        }
        return coreDigest(2, bArr, bArr2);
    }

    protected byte[] PRF(byte[] bArr, byte[] bArr2) {
        if (bArr.length != this.digestSize) {
            throw new IllegalArgumentException("wrong key length");
        }
        if (bArr2.length != 32) {
            throw new IllegalArgumentException("wrong address length");
        }
        return coreDigest(3, bArr, bArr2);
    }
}
