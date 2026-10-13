package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/generators/HKDFBytesGenerator.class */
public class HKDFBytesGenerator implements DerivationFunction {
    private HMac hMacHash;
    private int hashLen;
    private byte[] info;
    private byte[] currentT;
    private int generatedBytes;

    public HKDFBytesGenerator(Digest digest) {
        this.hMacHash = new HMac(digest);
        this.hashLen = digest.getDigestSize();
    }

    @Override // org.bouncycastle.crypto.DerivationFunction
    public void init(DerivationParameters derivationParameters) {
        if (!(derivationParameters instanceof HKDFParameters)) {
            throw new IllegalArgumentException("HKDF parameters required for HKDFBytesGenerator");
        }
        HKDFParameters hKDFParameters = (HKDFParameters) derivationParameters;
        if (hKDFParameters.skipExtract()) {
            this.hMacHash.init(new KeyParameter(hKDFParameters.getIKM()));
        } else {
            this.hMacHash.init(new KeyParameter(extractPRK(hKDFParameters.getSalt(), hKDFParameters.getIKM())));
        }
        this.info = hKDFParameters.getInfo();
        this.generatedBytes = 0;
        this.currentT = new byte[this.hashLen];
    }

    public byte[] extractPRK(byte[] bArr, byte[] bArr2) {
        if (bArr == null) {
            this.hMacHash.init(new KeyParameter(new byte[this.hashLen]));
        } else {
            this.hMacHash.init(new KeyParameter(bArr));
        }
        this.hMacHash.update(bArr2, 0, bArr2.length);
        byte[] bArr3 = new byte[this.hashLen];
        this.hMacHash.doFinal(bArr3, 0);
        return bArr3;
    }

    private void expandNext() throws DataLengthException {
        int i = (this.generatedBytes / this.hashLen) + 1;
        if (i >= 256) {
            throw new DataLengthException("HKDF cannot generate more than 255 blocks of HashLen size");
        }
        if (this.generatedBytes != 0) {
            this.hMacHash.update(this.currentT, 0, this.hashLen);
        }
        this.hMacHash.update(this.info, 0, this.info.length);
        this.hMacHash.update((byte) i);
        this.hMacHash.doFinal(this.currentT, 0);
    }

    public Digest getDigest() {
        return this.hMacHash.getUnderlyingDigest();
    }

    @Override // org.bouncycastle.crypto.DerivationFunction
    public int generateBytes(byte[] bArr, int i, int i2) throws DataLengthException, IllegalArgumentException {
        if (this.generatedBytes + i2 > 255 * this.hashLen) {
            throw new DataLengthException("HKDF may only be used for 255 * HashLen bytes of output");
        }
        if (this.generatedBytes % this.hashLen == 0) {
            expandNext();
        }
        int i3 = i2;
        int i4 = this.generatedBytes % this.hashLen;
        int iMin = Math.min(this.hashLen - (this.generatedBytes % this.hashLen), i3);
        System.arraycopy(this.currentT, i4, bArr, i, iMin);
        this.generatedBytes += iMin;
        while (true) {
            i3 -= iMin;
            i += iMin;
            if (i3 <= 0) {
                return i2;
            }
            expandNext();
            iMin = Math.min(this.hashLen, i3);
            System.arraycopy(this.currentT, 0, bArr, i, iMin);
            this.generatedBytes += iMin;
        }
    }
}
