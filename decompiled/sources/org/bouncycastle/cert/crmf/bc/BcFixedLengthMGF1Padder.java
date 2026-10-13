package org.bouncycastle.cert.crmf.bc;

import java.security.SecureRandom;
import org.bouncycastle.cert.crmf.EncryptedValuePadder;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.MGF1BytesGenerator;
import org.bouncycastle.crypto.params.MGFParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/crmf/bc/BcFixedLengthMGF1Padder.class */
public class BcFixedLengthMGF1Padder implements EncryptedValuePadder {
    private int length;
    private SecureRandom random;
    private Digest dig;

    public BcFixedLengthMGF1Padder(int i) {
        this(i, null);
    }

    public BcFixedLengthMGF1Padder(int i, SecureRandom secureRandom) {
        this.dig = new SHA1Digest();
        this.length = i;
        this.random = secureRandom;
    }

    @Override // org.bouncycastle.cert.crmf.EncryptedValuePadder
    public byte[] getPaddedData(byte[] bArr) {
        byte[] bArr2 = new byte[this.length];
        byte[] bArr3 = new byte[this.dig.getDigestSize()];
        byte[] bArr4 = new byte[this.length - this.dig.getDigestSize()];
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        this.random.nextBytes(bArr3);
        MGF1BytesGenerator mGF1BytesGenerator = new MGF1BytesGenerator(this.dig);
        mGF1BytesGenerator.init(new MGFParameters(bArr3));
        mGF1BytesGenerator.generateBytes(bArr4, 0, bArr4.length);
        System.arraycopy(bArr3, 0, bArr2, 0, bArr3.length);
        System.arraycopy(bArr, 0, bArr2, bArr3.length, bArr.length);
        for (int length = bArr3.length + bArr.length + 1; length != bArr2.length; length++) {
            bArr2[length] = (byte) (1 + this.random.nextInt(255));
        }
        for (int i = 0; i != bArr4.length; i++) {
            int length2 = i + bArr3.length;
            bArr2[length2] = (byte) (bArr2[length2] ^ bArr4[i]);
        }
        return bArr2;
    }

    @Override // org.bouncycastle.cert.crmf.EncryptedValuePadder
    public byte[] getUnpaddedData(byte[] bArr) {
        byte[] bArr2 = new byte[this.dig.getDigestSize()];
        byte[] bArr3 = new byte[this.length - this.dig.getDigestSize()];
        System.arraycopy(bArr, 0, bArr2, 0, bArr2.length);
        MGF1BytesGenerator mGF1BytesGenerator = new MGF1BytesGenerator(this.dig);
        mGF1BytesGenerator.init(new MGFParameters(bArr2));
        mGF1BytesGenerator.generateBytes(bArr3, 0, bArr3.length);
        for (int i = 0; i != bArr3.length; i++) {
            int length = i + bArr2.length;
            bArr[length] = (byte) (bArr[length] ^ bArr3[i]);
        }
        int i2 = 0;
        for (int length2 = bArr.length - 1; length2 != bArr2.length; length2--) {
            if (bArr[length2] == 0) {
                i2 = length2;
                break;
            }
        }
        if (i2 == 0) {
            throw new IllegalStateException("bad padding in encoding");
        }
        byte[] bArr4 = new byte[i2 - bArr2.length];
        System.arraycopy(bArr, bArr2.length, bArr4, 0, bArr4.length);
        return bArr4;
    }
}
