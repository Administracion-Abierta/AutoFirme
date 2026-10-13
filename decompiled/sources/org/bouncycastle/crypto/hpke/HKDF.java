package org.bouncycastle.crypto.hpke;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/hpke/HKDF.class */
class HKDF {
    private static final String versionLabel = "HPKE-v1";
    private final HKDFBytesGenerator kdf;
    private final int hashLength;

    HKDF(short s) {
        Digest sHA512Digest;
        switch (s) {
            case 1:
                sHA512Digest = new SHA256Digest();
                break;
            case 2:
                sHA512Digest = new SHA384Digest();
                break;
            case 3:
                sHA512Digest = new SHA512Digest();
                break;
            default:
                throw new IllegalArgumentException("invalid kdf id");
        }
        this.kdf = new HKDFBytesGenerator(sHA512Digest);
        this.hashLength = sHA512Digest.getDigestSize();
    }

    int getHashSize() {
        return this.hashLength;
    }

    protected byte[] LabeledExtract(byte[] bArr, byte[] bArr2, String str, byte[] bArr3) {
        if (bArr == null) {
            bArr = new byte[this.hashLength];
        }
        return this.kdf.extractPRK(bArr, Arrays.concatenate(versionLabel.getBytes(), bArr2, str.getBytes(), bArr3));
    }

    protected byte[] LabeledExpand(byte[] bArr, byte[] bArr2, String str, byte[] bArr3, int i) {
        if (i > 65536) {
            throw new IllegalArgumentException("Expand length cannot be larger than 2^16");
        }
        this.kdf.init(HKDFParameters.skipExtractParameters(bArr, Arrays.concatenate(Arrays.concatenate(Pack.shortToBigEndian((short) i), versionLabel.getBytes(), bArr2, str.getBytes()), bArr3)));
        byte[] bArr4 = new byte[i];
        this.kdf.generateBytes(bArr4, 0, bArr4.length);
        return bArr4;
    }

    protected byte[] Extract(byte[] bArr, byte[] bArr2) {
        if (bArr == null) {
            bArr = new byte[this.hashLength];
        }
        return this.kdf.extractPRK(bArr, bArr2);
    }

    protected byte[] Expand(byte[] bArr, byte[] bArr2, int i) {
        if (i > 65536) {
            throw new IllegalArgumentException("Expand length cannot be larger than 2^16");
        }
        this.kdf.init(HKDFParameters.skipExtractParameters(bArr, bArr2));
        byte[] bArr3 = new byte[i];
        this.kdf.generateBytes(bArr3, 0, bArr3.length);
        return bArr3;
    }
}
