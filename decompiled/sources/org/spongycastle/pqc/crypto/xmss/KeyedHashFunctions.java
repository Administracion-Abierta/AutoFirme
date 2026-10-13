package org.spongycastle.pqc.crypto.xmss;

import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.Xof;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/xmss/KeyedHashFunctions.class */
final class KeyedHashFunctions {
    private final Digest digest;
    private final int digestSize;

    protected KeyedHashFunctions(Digest digest, int digestSize) {
        if (digest == null) {
            throw new NullPointerException("digest == null");
        }
        this.digest = digest;
        this.digestSize = digestSize;
    }

    private byte[] coreDigest(int fixedValue, byte[] key, byte[] index) {
        byte[] in = XMSSUtil.toBytesBigEndian(fixedValue, this.digestSize);
        this.digest.update(in, 0, in.length);
        this.digest.update(key, 0, key.length);
        this.digest.update(index, 0, index.length);
        byte[] out = new byte[this.digestSize];
        if (this.digest instanceof Xof) {
            ((Xof) this.digest).doFinal(out, 0, this.digestSize);
        } else {
            this.digest.doFinal(out, 0);
        }
        return out;
    }

    protected byte[] F(byte[] key, byte[] in) {
        if (key.length != this.digestSize) {
            throw new IllegalArgumentException("wrong key length");
        }
        if (in.length != this.digestSize) {
            throw new IllegalArgumentException("wrong in length");
        }
        return coreDigest(0, key, in);
    }

    protected byte[] H(byte[] key, byte[] in) {
        if (key.length != this.digestSize) {
            throw new IllegalArgumentException("wrong key length");
        }
        if (in.length != 2 * this.digestSize) {
            throw new IllegalArgumentException("wrong in length");
        }
        return coreDigest(1, key, in);
    }

    protected byte[] HMsg(byte[] key, byte[] in) {
        if (key.length != 3 * this.digestSize) {
            throw new IllegalArgumentException("wrong key length");
        }
        return coreDigest(2, key, in);
    }

    protected byte[] PRF(byte[] key, byte[] address) {
        if (key.length != this.digestSize) {
            throw new IllegalArgumentException("wrong key length");
        }
        if (address.length != 32) {
            throw new IllegalArgumentException("wrong address length");
        }
        return coreDigest(3, key, address);
    }
}
