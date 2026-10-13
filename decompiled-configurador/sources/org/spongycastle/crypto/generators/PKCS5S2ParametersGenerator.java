package org.spongycastle.crypto.generators;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.crypto.util.DigestFactory;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/generators/PKCS5S2ParametersGenerator.class */
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

    private void F(byte[] S, int c, byte[] iBuf, byte[] out, int outOff) {
        if (c == 0) {
            throw new IllegalArgumentException("iteration count must be at least 1.");
        }
        if (S != null) {
            this.hMac.update(S, 0, S.length);
        }
        this.hMac.update(iBuf, 0, iBuf.length);
        this.hMac.doFinal(this.state, 0);
        System.arraycopy(this.state, 0, out, outOff, this.state.length);
        for (int count = 1; count < c; count++) {
            this.hMac.update(this.state, 0, this.state.length);
            this.hMac.doFinal(this.state, 0);
            for (int j = 0; j != this.state.length; j++) {
                int i = outOff + j;
                out[i] = (byte) (out[i] ^ this.state[j]);
            }
        }
    }

    private byte[] generateDerivedKey(int dkLen) {
        int hLen = this.hMac.getMacSize();
        int l = ((dkLen + hLen) - 1) / hLen;
        byte[] iBuf = new byte[4];
        byte[] outBytes = new byte[l * hLen];
        int outPos = 0;
        CipherParameters param = new KeyParameter(this.password);
        this.hMac.init(param);
        for (int i = 1; i <= l; i++) {
            int pos = 3;
            while (true) {
                int i2 = pos;
                byte b = (byte) (iBuf[i2] + 1);
                iBuf[i2] = b;
                if (b == 0) {
                    pos--;
                }
            }
            F(this.salt, this.iterationCount, iBuf, outBytes, outPos);
            outPos += hLen;
        }
        return outBytes;
    }

    @Override // org.spongycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedParameters(int keySize) {
        int keySize2 = keySize / 8;
        byte[] dKey = Arrays.copyOfRange(generateDerivedKey(keySize2), 0, keySize2);
        return new KeyParameter(dKey, 0, keySize2);
    }

    @Override // org.spongycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedParameters(int keySize, int ivSize) {
        int keySize2 = keySize / 8;
        int ivSize2 = ivSize / 8;
        byte[] dKey = generateDerivedKey(keySize2 + ivSize2);
        return new ParametersWithIV(new KeyParameter(dKey, 0, keySize2), dKey, keySize2, ivSize2);
    }

    @Override // org.spongycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedMacParameters(int keySize) {
        return generateDerivedParameters(keySize);
    }
}
