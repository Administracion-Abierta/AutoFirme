package org.spongycastle.pqc.crypto.mceliece;

import java.security.SecureRandom;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.digests.SHA1Digest;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.crypto.prng.DigestRandomGenerator;
import org.spongycastle.pqc.crypto.MessageEncryptor;
import org.spongycastle.pqc.math.linearalgebra.ByteUtils;
import org.spongycastle.pqc.math.linearalgebra.GF2Vector;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/mceliece/McEliecePointchevalCipher.class */
public class McEliecePointchevalCipher implements MessageEncryptor {
    public static final String OID = "1.3.6.1.4.1.8301.3.1.3.4.2.2";
    private Digest messDigest;
    private SecureRandom sr;
    private int n;
    private int k;
    private int t;
    McElieceCCA2KeyParameters key;
    private boolean forEncryption;

    @Override // org.spongycastle.pqc.crypto.MessageEncryptor
    public void init(boolean forEncryption, CipherParameters param) {
        this.forEncryption = forEncryption;
        if (forEncryption) {
            if (param instanceof ParametersWithRandom) {
                ParametersWithRandom rParam = (ParametersWithRandom) param;
                this.sr = rParam.getRandom();
                this.key = (McElieceCCA2PublicKeyParameters) rParam.getParameters();
                initCipherEncrypt((McElieceCCA2PublicKeyParameters) this.key);
                return;
            }
            this.sr = new SecureRandom();
            this.key = (McElieceCCA2PublicKeyParameters) param;
            initCipherEncrypt((McElieceCCA2PublicKeyParameters) this.key);
            return;
        }
        this.key = (McElieceCCA2PrivateKeyParameters) param;
        initCipherDecrypt((McElieceCCA2PrivateKeyParameters) this.key);
    }

    public int getKeySize(McElieceCCA2KeyParameters key) throws IllegalArgumentException {
        if (key instanceof McElieceCCA2PublicKeyParameters) {
            return ((McElieceCCA2PublicKeyParameters) key).getN();
        }
        if (key instanceof McElieceCCA2PrivateKeyParameters) {
            return ((McElieceCCA2PrivateKeyParameters) key).getN();
        }
        throw new IllegalArgumentException("unsupported type");
    }

    protected int decryptOutputSize(int inLen) {
        return 0;
    }

    protected int encryptOutputSize(int inLen) {
        return 0;
    }

    private void initCipherEncrypt(McElieceCCA2PublicKeyParameters pubKey) {
        this.sr = this.sr != null ? this.sr : new SecureRandom();
        this.messDigest = Utils.getDigest(pubKey.getDigest());
        this.n = pubKey.getN();
        this.k = pubKey.getK();
        this.t = pubKey.getT();
    }

    private void initCipherDecrypt(McElieceCCA2PrivateKeyParameters privKey) {
        this.messDigest = Utils.getDigest(privKey.getDigest());
        this.n = privKey.getN();
        this.k = privKey.getK();
        this.t = privKey.getT();
    }

    @Override // org.spongycastle.pqc.crypto.MessageEncryptor
    public byte[] messageEncrypt(byte[] input) {
        if (!this.forEncryption) {
            throw new IllegalStateException("cipher initialised for decryption");
        }
        int kDiv8 = this.k >> 3;
        byte[] r = new byte[kDiv8];
        this.sr.nextBytes(r);
        GF2Vector rPrime = new GF2Vector(this.k, this.sr);
        byte[] rPrimeBytes = rPrime.getEncoded();
        byte[] mr = ByteUtils.concatenate(input, r);
        this.messDigest.update(mr, 0, mr.length);
        byte[] hmr = new byte[this.messDigest.getDigestSize()];
        this.messDigest.doFinal(hmr, 0);
        GF2Vector z = Conversions.encode(this.n, this.t, hmr);
        byte[] c1 = McElieceCCA2Primitives.encryptionPrimitive((McElieceCCA2PublicKeyParameters) this.key, rPrime, z).getEncoded();
        DigestRandomGenerator sr0 = new DigestRandomGenerator(new SHA1Digest());
        sr0.addSeedMaterial(rPrimeBytes);
        byte[] c2 = new byte[input.length + kDiv8];
        sr0.nextBytes(c2);
        for (int i = 0; i < input.length; i++) {
            int i2 = i;
            c2[i2] = (byte) (c2[i2] ^ input[i]);
        }
        for (int i3 = 0; i3 < kDiv8; i3++) {
            int length = input.length + i3;
            c2[length] = (byte) (c2[length] ^ r[i3]);
        }
        return ByteUtils.concatenate(c1, c2);
    }

    @Override // org.spongycastle.pqc.crypto.MessageEncryptor
    public byte[] messageDecrypt(byte[] input) throws InvalidCipherTextException {
        if (this.forEncryption) {
            throw new IllegalStateException("cipher initialised for decryption");
        }
        int c1Len = (this.n + 7) >> 3;
        int c2Len = input.length - c1Len;
        byte[][] c1c2 = ByteUtils.split(input, c1Len);
        byte[] c1 = c1c2[0];
        byte[] c2 = c1c2[1];
        GF2Vector c1Vec = GF2Vector.OS2VP(this.n, c1);
        GF2Vector[] c1Dec = McElieceCCA2Primitives.decryptionPrimitive((McElieceCCA2PrivateKeyParameters) this.key, c1Vec);
        byte[] rPrimeBytes = c1Dec[0].getEncoded();
        GF2Vector z = c1Dec[1];
        DigestRandomGenerator sr0 = new DigestRandomGenerator(new SHA1Digest());
        sr0.addSeedMaterial(rPrimeBytes);
        byte[] mrBytes = new byte[c2Len];
        sr0.nextBytes(mrBytes);
        for (int i = 0; i < c2Len; i++) {
            int i2 = i;
            mrBytes[i2] = (byte) (mrBytes[i2] ^ c2[i]);
        }
        this.messDigest.update(mrBytes, 0, mrBytes.length);
        byte[] hmr = new byte[this.messDigest.getDigestSize()];
        this.messDigest.doFinal(hmr, 0);
        GF2Vector c1Vec2 = Conversions.encode(this.n, this.t, hmr);
        if (!c1Vec2.equals(z)) {
            throw new InvalidCipherTextException("Bad Padding: Invalid ciphertext.");
        }
        int kDiv8 = this.k >> 3;
        byte[][] mr = ByteUtils.split(mrBytes, c2Len - kDiv8);
        return mr[0];
    }
}
