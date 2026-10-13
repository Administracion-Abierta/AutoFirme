package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.util.DigestFactory;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/generators/PKCS5S2ParametersGenerator.class */
public class PKCS5S2ParametersGenerator extends PBEParametersGenerator {
    private Mac hMac;
    private byte[] state;

    public PKCS5S2ParametersGenerator() {
        this(DigestFactory.createSHA1());
    }

    public PKCS5S2ParametersGenerator(Digest digest) {
        this.hMac = new HMac(digest);
        this.state = new byte[this.hMac.getMacSize()];
    }

    private void F(byte[] bArr, int i, byte[] bArr2, byte[] bArr3, int i2) {
        if (i == 0) {
            throw new IllegalArgumentException("iteration count must be at least 1.");
        }
        if (bArr != null) {
            this.hMac.update(bArr, 0, bArr.length);
        }
        this.hMac.update(bArr2, 0, bArr2.length);
        this.hMac.doFinal(this.state, 0);
        System.arraycopy(this.state, 0, bArr3, i2, this.state.length);
        for (int i3 = 1; i3 < i; i3++) {
            this.hMac.update(this.state, 0, this.state.length);
            this.hMac.doFinal(this.state, 0);
            for (int i4 = 0; i4 != this.state.length; i4++) {
                int i5 = i2 + i4;
                bArr3[i5] = (byte) (bArr3[i5] ^ this.state[i4]);
            }
        }
    }

    private byte[] generateDerivedKey(int i) {
        int i2;
        int macSize = this.hMac.getMacSize();
        int i3 = ((i + macSize) - 1) / macSize;
        byte[] bArr = new byte[4];
        byte[] bArr2 = new byte[i3 * macSize];
        int i4 = 0;
        this.hMac.init(new KeyParameter(this.password));
        for (int i5 = 1; i5 <= i3; i5++) {
            while (true) {
                int i6 = i2;
                byte b = (byte) (bArr[i6] + 1);
                bArr[i6] = b;
                i2 = b == 0 ? i2 - 1 : 3;
            }
            F(this.salt, this.iterationCount, bArr, bArr2, i4);
            i4 += macSize;
        }
        return bArr2;
    }

    @Override // org.bouncycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedParameters(int i) {
        int i2 = i / 8;
        return new KeyParameter(generateDerivedKey(i2), 0, i2);
    }

    @Override // org.bouncycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedParameters(int i, int i2) {
        int i3 = i / 8;
        int i4 = i2 / 8;
        byte[] bArrGenerateDerivedKey = generateDerivedKey(i3 + i4);
        return new ParametersWithIV(new KeyParameter(bArrGenerateDerivedKey, 0, i3), bArrGenerateDerivedKey, i3, i4);
    }

    @Override // org.bouncycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedMacParameters(int i) {
        return generateDerivedParameters(i);
    }
}
