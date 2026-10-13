package org.bouncycastle.pqc.crypto.cmce;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/cmce/CMCEPrivateKeyParameters.class */
public class CMCEPrivateKeyParameters extends CMCEKeyParameters {
    private final byte[] privateKey;

    public byte[] getPrivateKey() {
        return Arrays.clone(this.privateKey);
    }

    public CMCEPrivateKeyParameters(CMCEParameters cMCEParameters, byte[] bArr) {
        super(true, cMCEParameters);
        this.privateKey = Arrays.clone(bArr);
    }

    public CMCEPrivateKeyParameters(CMCEParameters cMCEParameters, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5) {
        super(true, cMCEParameters);
        this.privateKey = new byte[bArr.length + bArr2.length + bArr3.length + bArr4.length + bArr5.length];
        System.arraycopy(bArr, 0, this.privateKey, 0, bArr.length);
        int length = 0 + bArr.length;
        System.arraycopy(bArr2, 0, this.privateKey, length, bArr2.length);
        int length2 = length + bArr2.length;
        System.arraycopy(bArr3, 0, this.privateKey, length2, bArr3.length);
        int length3 = length2 + bArr3.length;
        System.arraycopy(bArr4, 0, this.privateKey, length3, bArr4.length);
        System.arraycopy(bArr5, 0, this.privateKey, length3 + bArr4.length, bArr5.length);
    }

    public byte[] reconstructPublicKey() {
        CMCEEngine engine = getParameters().getEngine();
        byte[] bArr = new byte[engine.getPublicKeySize()];
        engine.generate_public_key_from_private_key(this.privateKey);
        return bArr;
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.privateKey);
    }

    public byte[] getDelta() {
        return Arrays.copyOfRange(this.privateKey, 0, 32);
    }

    public byte[] getC() {
        return Arrays.copyOfRange(this.privateKey, 32, 40);
    }

    public byte[] getG() {
        return Arrays.copyOfRange(this.privateKey, 40, 40 + (getParameters().getT() * 2));
    }

    public byte[] getAlpha() {
        return Arrays.copyOfRange(this.privateKey, 40 + (getParameters().getT() * 2), this.privateKey.length - 32);
    }

    public byte[] getS() {
        return Arrays.copyOfRange(this.privateKey, this.privateKey.length - 32, this.privateKey.length);
    }
}
