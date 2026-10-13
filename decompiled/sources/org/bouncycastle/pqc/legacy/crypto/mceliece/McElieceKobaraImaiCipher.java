package org.bouncycastle.pqc.legacy.crypto.mceliece;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.prng.DigestRandomGenerator;
import org.bouncycastle.pqc.crypto.MessageEncryptor;
import org.bouncycastle.pqc.legacy.math.linearalgebra.ByteUtils;
import org.bouncycastle.pqc.legacy.math.linearalgebra.GF2Vector;
import org.bouncycastle.pqc.legacy.math.linearalgebra.IntegerFunctions;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/mceliece/McElieceKobaraImaiCipher.class */
public class McElieceKobaraImaiCipher implements MessageEncryptor {
    public static final String OID = "1.3.6.1.4.1.8301.3.1.3.4.2.3";
    private static final String DEFAULT_PRNG_NAME = "SHA1PRNG";
    public static final byte[] PUBLIC_CONSTANT = "a predetermined public constant".getBytes();
    private Digest messDigest;
    private SecureRandom sr;
    McElieceCCA2KeyParameters key;
    private int n;
    private int k;
    private int t;
    private boolean forEncryption;

    @Override // org.bouncycastle.pqc.crypto.MessageEncryptor
    public void init(boolean z, CipherParameters cipherParameters) {
        this.forEncryption = z;
        if (!z) {
            this.key = (McElieceCCA2PrivateKeyParameters) cipherParameters;
            initCipherDecrypt((McElieceCCA2PrivateKeyParameters) this.key);
        } else if (!(cipherParameters instanceof ParametersWithRandom)) {
            this.sr = CryptoServicesRegistrar.getSecureRandom();
            this.key = (McElieceCCA2PublicKeyParameters) cipherParameters;
            initCipherEncrypt((McElieceCCA2PublicKeyParameters) this.key);
        } else {
            ParametersWithRandom parametersWithRandom = (ParametersWithRandom) cipherParameters;
            this.sr = parametersWithRandom.getRandom();
            this.key = (McElieceCCA2PublicKeyParameters) parametersWithRandom.getParameters();
            initCipherEncrypt((McElieceCCA2PublicKeyParameters) this.key);
        }
    }

    public int getKeySize(McElieceCCA2KeyParameters mcElieceCCA2KeyParameters) {
        if (mcElieceCCA2KeyParameters instanceof McElieceCCA2PublicKeyParameters) {
            return ((McElieceCCA2PublicKeyParameters) mcElieceCCA2KeyParameters).getN();
        }
        if (mcElieceCCA2KeyParameters instanceof McElieceCCA2PrivateKeyParameters) {
            return ((McElieceCCA2PrivateKeyParameters) mcElieceCCA2KeyParameters).getN();
        }
        throw new IllegalArgumentException("unsupported type");
    }

    private void initCipherEncrypt(McElieceCCA2PublicKeyParameters mcElieceCCA2PublicKeyParameters) {
        this.messDigest = Utils.getDigest(mcElieceCCA2PublicKeyParameters.getDigest());
        this.n = mcElieceCCA2PublicKeyParameters.getN();
        this.k = mcElieceCCA2PublicKeyParameters.getK();
        this.t = mcElieceCCA2PublicKeyParameters.getT();
    }

    private void initCipherDecrypt(McElieceCCA2PrivateKeyParameters mcElieceCCA2PrivateKeyParameters) {
        this.messDigest = Utils.getDigest(mcElieceCCA2PrivateKeyParameters.getDigest());
        this.n = mcElieceCCA2PrivateKeyParameters.getN();
        this.k = mcElieceCCA2PrivateKeyParameters.getK();
        this.t = mcElieceCCA2PrivateKeyParameters.getT();
    }

    @Override // org.bouncycastle.pqc.crypto.MessageEncryptor
    public byte[] messageEncrypt(byte[] bArr) {
        if (!this.forEncryption) {
            throw new IllegalStateException("cipher initialised for decryption");
        }
        int digestSize = this.messDigest.getDigestSize();
        int i = this.k >> 3;
        int iBitLength = (IntegerFunctions.binomial(this.n, this.t).bitLength() - 1) >> 3;
        int length = ((i + iBitLength) - digestSize) - PUBLIC_CONSTANT.length;
        if (bArr.length > length) {
            length = bArr.length;
        }
        int length2 = length + PUBLIC_CONSTANT.length;
        int i2 = ((length2 + digestSize) - i) - iBitLength;
        byte[] bArr2 = new byte[length2];
        System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
        System.arraycopy(PUBLIC_CONSTANT, 0, bArr2, length, PUBLIC_CONSTANT.length);
        byte[] bArr3 = new byte[digestSize];
        this.sr.nextBytes(bArr3);
        DigestRandomGenerator digestRandomGenerator = new DigestRandomGenerator(new SHA1Digest());
        digestRandomGenerator.addSeedMaterial(bArr3);
        byte[] bArr4 = new byte[length2];
        digestRandomGenerator.nextBytes(bArr4);
        for (int i3 = length2 - 1; i3 >= 0; i3--) {
            int i4 = i3;
            bArr4[i4] = (byte) (bArr4[i4] ^ bArr2[i3]);
        }
        byte[] bArr5 = new byte[this.messDigest.getDigestSize()];
        this.messDigest.update(bArr4, 0, bArr4.length);
        this.messDigest.doFinal(bArr5, 0);
        for (int i5 = digestSize - 1; i5 >= 0; i5--) {
            int i6 = i5;
            bArr5[i6] = (byte) (bArr5[i6] ^ bArr3[i5]);
        }
        byte[] bArrConcatenate = ByteUtils.concatenate(bArr5, bArr4);
        byte[] bArr6 = new byte[0];
        if (i2 > 0) {
            bArr6 = new byte[i2];
            System.arraycopy(bArrConcatenate, 0, bArr6, 0, i2);
        }
        byte[] bArr7 = new byte[iBitLength];
        System.arraycopy(bArrConcatenate, i2, bArr7, 0, iBitLength);
        byte[] bArr8 = new byte[i];
        System.arraycopy(bArrConcatenate, i2 + iBitLength, bArr8, 0, i);
        byte[] encoded = McElieceCCA2Primitives.encryptionPrimitive((McElieceCCA2PublicKeyParameters) this.key, GF2Vector.OS2VP(this.k, bArr8), Conversions.encode(this.n, this.t, bArr7)).getEncoded();
        return i2 > 0 ? ByteUtils.concatenate(bArr6, encoded) : encoded;
    }

    @Override // org.bouncycastle.pqc.crypto.MessageEncryptor
    public byte[] messageDecrypt(byte[] bArr) throws InvalidCipherTextException {
        byte[] bArr2;
        byte[] bArr3;
        if (this.forEncryption) {
            throw new IllegalStateException("cipher initialised for decryption");
        }
        int i = this.n >> 3;
        if (bArr.length < i) {
            throw new InvalidCipherTextException("Bad Padding: Ciphertext too short.");
        }
        int digestSize = this.messDigest.getDigestSize();
        int i2 = this.k >> 3;
        int iBitLength = (IntegerFunctions.binomial(this.n, this.t).bitLength() - 1) >> 3;
        int length = bArr.length - i;
        if (length > 0) {
            byte[][] bArrSplit = ByteUtils.split(bArr, length);
            bArr2 = bArrSplit[0];
            bArr3 = bArrSplit[1];
        } else {
            bArr2 = new byte[0];
            bArr3 = bArr;
        }
        GF2Vector[] gF2VectorArrDecryptionPrimitive = McElieceCCA2Primitives.decryptionPrimitive((McElieceCCA2PrivateKeyParameters) this.key, GF2Vector.OS2VP(this.n, bArr3));
        byte[] encoded = gF2VectorArrDecryptionPrimitive[0].getEncoded();
        GF2Vector gF2Vector = gF2VectorArrDecryptionPrimitive[1];
        if (encoded.length > i2) {
            encoded = ByteUtils.subArray(encoded, 0, i2);
        }
        byte[] bArrDecode = Conversions.decode(this.n, this.t, gF2Vector);
        if (bArrDecode.length < iBitLength) {
            byte[] bArr4 = new byte[iBitLength];
            System.arraycopy(bArrDecode, 0, bArr4, iBitLength - bArrDecode.length, bArrDecode.length);
            bArrDecode = bArr4;
        }
        byte[] bArrConcatenate = ByteUtils.concatenate(ByteUtils.concatenate(bArr2, bArrDecode), encoded);
        int length2 = bArrConcatenate.length - digestSize;
        byte[][] bArrSplit2 = ByteUtils.split(bArrConcatenate, digestSize);
        byte[] bArr5 = bArrSplit2[0];
        byte[] bArr6 = bArrSplit2[1];
        byte[] bArr7 = new byte[this.messDigest.getDigestSize()];
        this.messDigest.update(bArr6, 0, bArr6.length);
        this.messDigest.doFinal(bArr7, 0);
        for (int i3 = digestSize - 1; i3 >= 0; i3--) {
            int i4 = i3;
            bArr7[i4] = (byte) (bArr7[i4] ^ bArr5[i3]);
        }
        DigestRandomGenerator digestRandomGenerator = new DigestRandomGenerator(new SHA1Digest());
        digestRandomGenerator.addSeedMaterial(bArr7);
        byte[] bArr8 = new byte[length2];
        digestRandomGenerator.nextBytes(bArr8);
        for (int i5 = length2 - 1; i5 >= 0; i5--) {
            int i6 = i5;
            bArr8[i6] = (byte) (bArr8[i6] ^ bArr6[i5]);
        }
        if (bArr8.length < length2) {
            throw new InvalidCipherTextException("Bad Padding: invalid ciphertext");
        }
        byte[][] bArrSplit3 = ByteUtils.split(bArr8, length2 - PUBLIC_CONSTANT.length);
        byte[] bArr9 = bArrSplit3[0];
        if (ByteUtils.equals(bArrSplit3[1], PUBLIC_CONSTANT)) {
            return bArr9;
        }
        throw new InvalidCipherTextException("Bad Padding: invalid ciphertext");
    }
}
