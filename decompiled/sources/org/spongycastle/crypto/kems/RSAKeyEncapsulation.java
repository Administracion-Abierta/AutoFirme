package org.spongycastle.crypto.kems;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DerivationFunction;
import org.spongycastle.crypto.KeyEncapsulation;
import org.spongycastle.crypto.params.KDFParameters;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.RSAKeyParameters;
import org.spongycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/kems/RSAKeyEncapsulation.class */
public class RSAKeyEncapsulation implements KeyEncapsulation {
    private static final BigInteger ZERO = BigInteger.valueOf(0);
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private DerivationFunction kdf;
    private SecureRandom rnd;
    private RSAKeyParameters key;

    public RSAKeyEncapsulation(DerivationFunction kdf, SecureRandom rnd) {
        this.kdf = kdf;
        this.rnd = rnd;
    }

    @Override // org.spongycastle.crypto.KeyEncapsulation
    public void init(CipherParameters key) throws IllegalArgumentException {
        if (!(key instanceof RSAKeyParameters)) {
            throw new IllegalArgumentException("RSA key required");
        }
        this.key = (RSAKeyParameters) key;
    }

    @Override // org.spongycastle.crypto.KeyEncapsulation
    public CipherParameters encrypt(byte[] out, int outOff, int keyLen) throws IllegalArgumentException {
        if (this.key.isPrivate()) {
            throw new IllegalArgumentException("Public key required for encryption");
        }
        BigInteger n = this.key.getModulus();
        BigInteger e = this.key.getExponent();
        BigInteger r = BigIntegers.createRandomInRange(ZERO, n.subtract(ONE), this.rnd);
        BigInteger c = r.modPow(e, n);
        byte[] C = BigIntegers.asUnsignedByteArray((n.bitLength() + 7) / 8, c);
        System.arraycopy(C, 0, out, outOff, C.length);
        return generateKey(n, r, keyLen);
    }

    public CipherParameters encrypt(byte[] out, int keyLen) {
        return encrypt(out, 0, keyLen);
    }

    @Override // org.spongycastle.crypto.KeyEncapsulation
    public CipherParameters decrypt(byte[] in, int inOff, int inLen, int keyLen) throws IllegalArgumentException {
        if (!this.key.isPrivate()) {
            throw new IllegalArgumentException("Private key required for decryption");
        }
        BigInteger n = this.key.getModulus();
        BigInteger d = this.key.getExponent();
        byte[] C = new byte[inLen];
        System.arraycopy(in, inOff, C, 0, C.length);
        BigInteger c = new BigInteger(1, C);
        BigInteger r = c.modPow(d, n);
        return generateKey(n, r, keyLen);
    }

    public CipherParameters decrypt(byte[] in, int keyLen) {
        return decrypt(in, 0, in.length, keyLen);
    }

    protected KeyParameter generateKey(BigInteger n, BigInteger r, int keyLen) {
        byte[] R = BigIntegers.asUnsignedByteArray((n.bitLength() + 7) / 8, r);
        this.kdf.init(new KDFParameters(R, null));
        byte[] K = new byte[keyLen];
        this.kdf.generateBytes(K, 0, K.length);
        return new KeyParameter(K);
    }
}
