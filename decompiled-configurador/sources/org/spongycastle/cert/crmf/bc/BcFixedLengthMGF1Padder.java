package org.spongycastle.cert.crmf.bc;

import java.security.SecureRandom;
import org.spongycastle.cert.crmf.EncryptedValuePadder;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.SHA1Digest;
import org.spongycastle.crypto.generators.MGF1BytesGenerator;
import org.spongycastle.crypto.params.MGFParameters;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/crmf/bc/BcFixedLengthMGF1Padder.class */
public class BcFixedLengthMGF1Padder implements EncryptedValuePadder {
    private int length;
    private SecureRandom random;
    private Digest dig;

    public BcFixedLengthMGF1Padder(int length) {
        this(length, null);
    }

    public BcFixedLengthMGF1Padder(int length, SecureRandom random) {
        this.dig = new SHA1Digest();
        this.length = length;
        this.random = random;
    }

    @Override // org.spongycastle.cert.crmf.EncryptedValuePadder
    public byte[] getPaddedData(byte[] data) {
        byte[] bytes = new byte[this.length];
        byte[] seed = new byte[this.dig.getDigestSize()];
        byte[] mask = new byte[this.length - this.dig.getDigestSize()];
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        this.random.nextBytes(seed);
        MGF1BytesGenerator maskGen = new MGF1BytesGenerator(this.dig);
        maskGen.init(new MGFParameters(seed));
        maskGen.generateBytes(mask, 0, mask.length);
        System.arraycopy(seed, 0, bytes, 0, seed.length);
        System.arraycopy(data, 0, bytes, seed.length, data.length);
        for (int i = seed.length + data.length + 1; i != bytes.length; i++) {
            bytes[i] = (byte) (1 + this.random.nextInt(255));
        }
        for (int i2 = 0; i2 != mask.length; i2++) {
            int length = i2 + seed.length;
            bytes[length] = (byte) (bytes[length] ^ mask[i2]);
        }
        return bytes;
    }

    @Override // org.spongycastle.cert.crmf.EncryptedValuePadder
    public byte[] getUnpaddedData(byte[] paddedData) {
        byte[] seed = new byte[this.dig.getDigestSize()];
        byte[] mask = new byte[this.length - this.dig.getDigestSize()];
        System.arraycopy(paddedData, 0, seed, 0, seed.length);
        MGF1BytesGenerator maskGen = new MGF1BytesGenerator(this.dig);
        maskGen.init(new MGFParameters(seed));
        maskGen.generateBytes(mask, 0, mask.length);
        for (int i = 0; i != mask.length; i++) {
            int length = i + seed.length;
            paddedData[length] = (byte) (paddedData[length] ^ mask[i]);
        }
        int end = 0;
        for (int i2 = paddedData.length - 1; i2 != seed.length; i2--) {
            if (paddedData[i2] == 0) {
                end = i2;
                break;
            }
        }
        if (end == 0) {
            throw new IllegalStateException("bad padding in encoding");
        }
        byte[] data = new byte[end - seed.length];
        System.arraycopy(paddedData, seed.length, data, 0, data.length);
        return data;
    }
}
