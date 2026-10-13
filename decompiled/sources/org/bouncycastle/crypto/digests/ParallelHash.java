package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/ParallelHash.class */
public class ParallelHash implements Xof, Digest {
    private static final byte[] N_PARALLEL_HASH = Strings.toByteArray("ParallelHash");
    private final CSHAKEDigest cshake;
    private final CSHAKEDigest compressor;
    private final int bitLength;
    private final int outputLength;
    private final int B;
    private final byte[] buffer;
    private final byte[] compressorBuffer;
    private boolean firstOutput;
    private int nCount;
    private int bufOff;
    private final CryptoServicePurpose purpose;

    public ParallelHash(int i, byte[] bArr, int i2) {
        this(i, bArr, i2, i * 2, CryptoServicePurpose.ANY);
    }

    public ParallelHash(int i, byte[] bArr, int i2, int i3) {
        this(i, bArr, i2, i3, CryptoServicePurpose.ANY);
    }

    public ParallelHash(int i, byte[] bArr, int i2, int i3, CryptoServicePurpose cryptoServicePurpose) {
        this.cshake = new CSHAKEDigest(i, N_PARALLEL_HASH, bArr);
        this.compressor = new CSHAKEDigest(i, new byte[0], new byte[0]);
        this.bitLength = i;
        this.B = i2;
        this.outputLength = (i3 + 7) / 8;
        this.buffer = new byte[i2];
        this.compressorBuffer = new byte[(i * 2) / 8];
        this.purpose = cryptoServicePurpose;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, i, cryptoServicePurpose));
        reset();
    }

    public ParallelHash(ParallelHash parallelHash) {
        this.cshake = new CSHAKEDigest(parallelHash.cshake);
        this.compressor = new CSHAKEDigest(parallelHash.compressor);
        this.bitLength = parallelHash.bitLength;
        this.B = parallelHash.B;
        this.outputLength = parallelHash.outputLength;
        this.buffer = Arrays.clone(parallelHash.buffer);
        this.compressorBuffer = Arrays.clone(parallelHash.compressorBuffer);
        this.purpose = parallelHash.purpose;
        this.firstOutput = parallelHash.firstOutput;
        this.nCount = parallelHash.nCount;
        this.bufOff = parallelHash.bufOff;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, this.bitLength, this.purpose));
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "ParallelHash" + this.cshake.getAlgorithmName().substring(6);
    }

    @Override // org.bouncycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return this.cshake.getByteLength();
    }

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return this.outputLength;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte b) throws IllegalStateException {
        byte[] bArr = this.buffer;
        int i = this.bufOff;
        this.bufOff = i + 1;
        bArr[i] = b;
        if (this.bufOff == this.buffer.length) {
            compress();
        }
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte[] bArr, int i, int i2) throws IllegalStateException, DataLengthException {
        int iMax = Math.max(0, i2);
        int i3 = 0;
        if (this.bufOff != 0) {
            while (i3 < iMax && this.bufOff != this.buffer.length) {
                byte[] bArr2 = this.buffer;
                int i4 = this.bufOff;
                this.bufOff = i4 + 1;
                int i5 = i3;
                i3++;
                bArr2[i4] = bArr[i + i5];
            }
            if (this.bufOff == this.buffer.length) {
                compress();
            }
        }
        if (i3 < iMax) {
            while (iMax - i3 >= this.B) {
                compress(bArr, i + i3, this.B);
                i3 += this.B;
            }
        }
        while (i3 < iMax) {
            int i6 = i3;
            i3++;
            update(bArr[i + i6]);
        }
    }

    private void compress() {
        compress(this.buffer, 0, this.bufOff);
        this.bufOff = 0;
    }

    private void compress(byte[] bArr, int i, int i2) {
        this.compressor.update(bArr, i, i2);
        this.compressor.doFinal(this.compressorBuffer, 0, this.compressorBuffer.length);
        this.cshake.update(this.compressorBuffer, 0, this.compressorBuffer.length);
        this.nCount++;
    }

    private void wrapUp(int i) {
        if (this.bufOff != 0) {
            compress();
        }
        byte[] bArrRightEncode = XofUtils.rightEncode(this.nCount);
        byte[] bArrRightEncode2 = XofUtils.rightEncode(i * 8);
        this.cshake.update(bArrRightEncode, 0, bArrRightEncode.length);
        this.cshake.update(bArrRightEncode2, 0, bArrRightEncode2.length);
        this.firstOutput = false;
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) throws IllegalStateException, DataLengthException {
        if (this.firstOutput) {
            wrapUp(this.outputLength);
        }
        int iDoFinal = this.cshake.doFinal(bArr, i, getDigestSize());
        reset();
        return iDoFinal;
    }

    @Override // org.bouncycastle.crypto.Xof
    public int doFinal(byte[] bArr, int i, int i2) {
        if (this.firstOutput) {
            wrapUp(this.outputLength);
        }
        int iDoFinal = this.cshake.doFinal(bArr, i, i2);
        reset();
        return iDoFinal;
    }

    @Override // org.bouncycastle.crypto.Xof
    public int doOutput(byte[] bArr, int i, int i2) {
        if (this.firstOutput) {
            wrapUp(0);
        }
        return this.cshake.doOutput(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.Digest
    public void reset() {
        this.cshake.reset();
        Arrays.clear(this.buffer);
        byte[] bArrLeftEncode = XofUtils.leftEncode(this.B);
        this.cshake.update(bArrLeftEncode, 0, bArrLeftEncode.length);
        this.nCount = 0;
        this.bufOff = 0;
        this.firstOutput = true;
    }
}
