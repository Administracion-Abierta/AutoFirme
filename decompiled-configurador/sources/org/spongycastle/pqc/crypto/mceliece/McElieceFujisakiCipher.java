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

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/mceliece/McElieceFujisakiCipher.class */
public class McElieceFujisakiCipher implements MessageEncryptor {
    public static final String OID = "1.3.6.1.4.1.8301.3.1.3.4.2.1";
    private static final String DEFAULT_PRNG_NAME = "SHA1PRNG";
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
        this.t = privKey.getT();
    }

    @Override // org.spongycastle.pqc.crypto.MessageEncryptor
    public byte[] messageEncrypt(byte[] input) {
        if (!this.forEncryption) {
            throw new IllegalStateException("cipher initialised for decryption");
        }
        GF2Vector r = new GF2Vector(this.k, this.sr);
        byte[] rBytes = r.getEncoded();
        byte[] rm = ByteUtils.concatenate(rBytes, input);
        this.messDigest.update(rm, 0, rm.length);
        byte[] hrm = new byte[this.messDigest.getDigestSize()];
        this.messDigest.doFinal(hrm, 0);
        GF2Vector z = Conversions.encode(this.n, this.t, hrm);
        byte[] c1 = McElieceCCA2Primitives.encryptionPrimitive((McElieceCCA2PublicKeyParameters) this.key, r, z).getEncoded();
        DigestRandomGenerator sr0 = new DigestRandomGenerator(new SHA1Digest());
        sr0.addSeedMaterial(rBytes);
        byte[] c2 = new byte[input.length];
        sr0.nextBytes(c2);
        for (int i = 0; i < input.length; i++) {
            int i2 = i;
            c2[i2] = (byte) (c2[i2] ^ input[i]);
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
        GF2Vector hrmVec = GF2Vector.OS2VP(this.n, c1);
        GF2Vector[] decC1 = McElieceCCA2Primitives.decryptionPrimitive((McElieceCCA2PrivateKeyParameters) this.key, hrmVec);
        byte[] rBytes = decC1[0].getEncoded();
        GF2Vector z = decC1[1];
        DigestRandomGenerator sr0 = new DigestRandomGenerator(new SHA1Digest());
        sr0.addSeedMaterial(rBytes);
        byte[] mBytes = new byte[c2Len];
        sr0.nextBytes(mBytes);
        for (int i = 0; i < c2Len; i++) {
            int i2 = i;
            mBytes[i2] = (byte) (mBytes[i2] ^ c2[i]);
        }
        byte[] rmBytes = ByteUtils.concatenate(rBytes, mBytes);
        byte[] hrm = new byte[this.messDigest.getDigestSize()];
        this.messDigest.update(rmBytes, 0, rmBytes.length);
        this.messDigest.doFinal(hrm, 0);
        GF2Vector hrmVec2 = Conversions.encode(this.n, this.t, hrm);
        if (!hrmVec2.equals(z)) {
            throw new InvalidCipherTextException("Bad Padding: invalid ciphertext");
        }
        return mBytes;
    }
}
