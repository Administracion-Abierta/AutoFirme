package org.bouncycastle.crypto.encodings;

import com.sun.jna.platform.win32.WinNT;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/encodings/OAEPEncoding.class */
public class OAEPEncoding implements AsymmetricBlockCipher {
    private final AsymmetricBlockCipher engine;
    private final Digest mgf1Hash;
    private final int mgf1NoMemoLimit;
    private final byte[] defHash;
    private SecureRandom random;
    private boolean forEncryption;

    private static int getMGF1NoMemoLimit(Digest digest) {
        return ((digest instanceof Memoable) && (digest instanceof ExtendedDigest)) ? ((ExtendedDigest) digest).getByteLength() - 1 : WinNT.MAXLONG;
    }

    public OAEPEncoding(AsymmetricBlockCipher asymmetricBlockCipher) {
        this(asymmetricBlockCipher, DigestFactory.createSHA1(), null);
    }

    public OAEPEncoding(AsymmetricBlockCipher asymmetricBlockCipher, Digest digest) {
        this(asymmetricBlockCipher, digest, null);
    }

    public OAEPEncoding(AsymmetricBlockCipher asymmetricBlockCipher, Digest digest, byte[] bArr) {
        this(asymmetricBlockCipher, digest, digest, bArr);
    }

    public OAEPEncoding(AsymmetricBlockCipher asymmetricBlockCipher, Digest digest, Digest digest2, byte[] bArr) {
        this.engine = asymmetricBlockCipher;
        this.mgf1Hash = digest2;
        this.mgf1NoMemoLimit = getMGF1NoMemoLimit(digest2);
        this.defHash = new byte[digest.getDigestSize()];
        digest.reset();
        if (bArr != null) {
            digest.update(bArr, 0, bArr.length);
        }
        digest.doFinal(this.defHash, 0);
    }

    public AsymmetricBlockCipher getUnderlyingCipher() {
        return this.engine;
    }

    @Override // org.bouncycastle.crypto.AsymmetricBlockCipher
    public void init(boolean z, CipherParameters cipherParameters) {
        SecureRandom random = null;
        if (cipherParameters instanceof ParametersWithRandom) {
            random = ((ParametersWithRandom) cipherParameters).getRandom();
        }
        this.random = z ? CryptoServicesRegistrar.getSecureRandom(random) : null;
        this.forEncryption = z;
        this.engine.init(z, cipherParameters);
    }

    @Override // org.bouncycastle.crypto.AsymmetricBlockCipher
    public int getInputBlockSize() {
        int inputBlockSize = this.engine.getInputBlockSize();
        return this.forEncryption ? (inputBlockSize - 1) - (2 * this.defHash.length) : inputBlockSize;
    }

    @Override // org.bouncycastle.crypto.AsymmetricBlockCipher
    public int getOutputBlockSize() {
        int outputBlockSize = this.engine.getOutputBlockSize();
        return this.forEncryption ? outputBlockSize : (outputBlockSize - 1) - (2 * this.defHash.length);
    }

    @Override // org.bouncycastle.crypto.AsymmetricBlockCipher
    public byte[] processBlock(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        return this.forEncryption ? encodeBlock(bArr, i, i2) : decodeBlock(bArr, i, i2);
    }

    public byte[] encodeBlock(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        int inputBlockSize = getInputBlockSize();
        if (i2 > inputBlockSize) {
            throw new DataLengthException("input data too long");
        }
        byte[] bArr2 = new byte[inputBlockSize + 1 + (2 * this.defHash.length)];
        System.arraycopy(bArr, i, bArr2, bArr2.length - i2, i2);
        bArr2[(bArr2.length - i2) - 1] = 1;
        System.arraycopy(this.defHash, 0, bArr2, this.defHash.length, this.defHash.length);
        byte[] bArr3 = new byte[this.defHash.length];
        this.random.nextBytes(bArr3);
        System.arraycopy(bArr3, 0, bArr2, 0, this.defHash.length);
        this.mgf1Hash.reset();
        maskGeneratorFunction1(bArr3, 0, bArr3.length, bArr2, this.defHash.length, bArr2.length - this.defHash.length);
        maskGeneratorFunction1(bArr2, this.defHash.length, bArr2.length - this.defHash.length, bArr2, 0, this.defHash.length);
        return this.engine.processBlock(bArr2, 0, bArr2.length);
    }

    public byte[] decodeBlock(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        int outputBlockSize = getOutputBlockSize() >> 31;
        byte[] bArr2 = new byte[this.engine.getOutputBlockSize()];
        byte[] bArrProcessBlock = this.engine.processBlock(bArr, i, i2);
        int length = outputBlockSize | ((bArr2.length - bArrProcessBlock.length) >> 31);
        int iMin = Math.min(bArr2.length, bArrProcessBlock.length);
        System.arraycopy(bArrProcessBlock, 0, bArr2, bArr2.length - iMin, iMin);
        Arrays.fill(bArrProcessBlock, (byte) 0);
        this.mgf1Hash.reset();
        maskGeneratorFunction1(bArr2, this.defHash.length, bArr2.length - this.defHash.length, bArr2, 0, this.defHash.length);
        maskGeneratorFunction1(bArr2, 0, this.defHash.length, bArr2, this.defHash.length, bArr2.length - this.defHash.length);
        for (int i3 = 0; i3 != this.defHash.length; i3++) {
            length |= this.defHash[i3] ^ bArr2[this.defHash.length + i3];
        }
        int i4 = -1;
        for (int length2 = 2 * this.defHash.length; length2 != bArr2.length; length2++) {
            i4 += length2 & (((-(bArr2[length2] & 255)) & i4) >> 31);
        }
        int i5 = length | (i4 >> 31);
        int i6 = i4 + 1;
        if ((i5 | (bArr2[i6] ^ 1)) != 0) {
            Arrays.fill(bArr2, (byte) 0);
            throw new InvalidCipherTextException("data wrong");
        }
        int i7 = i6 + 1;
        byte[] bArr3 = new byte[bArr2.length - i7];
        System.arraycopy(bArr2, i7, bArr3, 0, bArr3.length);
        Arrays.fill(bArr2, (byte) 0);
        return bArr3;
    }

    private void maskGeneratorFunction1(byte[] bArr, int i, int i2, byte[] bArr2, int i3, int i4) {
        int digestSize = this.mgf1Hash.getDigestSize();
        byte[] bArr3 = new byte[digestSize];
        byte[] bArr4 = new byte[4];
        int i5 = 0;
        int i6 = i3 + i4;
        int i7 = i6 - digestSize;
        int i8 = i3;
        this.mgf1Hash.update(bArr, i, i2);
        if (i2 > this.mgf1NoMemoLimit) {
            Memoable memoable = (Memoable) this.mgf1Hash;
            Memoable memoableCopy = memoable.copy();
            while (i8 < i7) {
                int i9 = i5;
                i5++;
                Pack.intToBigEndian(i9, bArr4, 0);
                this.mgf1Hash.update(bArr4, 0, bArr4.length);
                this.mgf1Hash.doFinal(bArr3, 0);
                memoable.reset(memoableCopy);
                Bytes.xorTo(digestSize, bArr3, 0, bArr2, i8);
                i8 += digestSize;
            }
        } else {
            while (i8 < i7) {
                int i10 = i5;
                i5++;
                Pack.intToBigEndian(i10, bArr4, 0);
                this.mgf1Hash.update(bArr4, 0, bArr4.length);
                this.mgf1Hash.doFinal(bArr3, 0);
                this.mgf1Hash.update(bArr, i, i2);
                Bytes.xorTo(digestSize, bArr3, 0, bArr2, i8);
                i8 += digestSize;
            }
        }
        Pack.intToBigEndian(i5, bArr4, 0);
        this.mgf1Hash.update(bArr4, 0, bArr4.length);
        this.mgf1Hash.doFinal(bArr3, 0);
        Bytes.xorTo(i6 - i8, bArr3, 0, bArr2, i8);
    }
}
