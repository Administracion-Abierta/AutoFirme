package org.bouncycastle.pqc.crypto.crystals.dilithium;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/crystals/dilithium/DilithiumPublicKeyParameters.class */
public class DilithiumPublicKeyParameters extends DilithiumKeyParameters {
    final byte[] rho;
    final byte[] t1;

    static byte[] getEncoded(byte[] bArr, byte[] bArr2) {
        return Arrays.concatenate(bArr, bArr2);
    }

    public DilithiumPublicKeyParameters(DilithiumParameters dilithiumParameters, byte[] bArr) {
        super(false, dilithiumParameters);
        this.rho = Arrays.copyOfRange(bArr, 0, 32);
        this.t1 = Arrays.copyOfRange(bArr, 32, bArr.length);
    }

    public DilithiumPublicKeyParameters(DilithiumParameters dilithiumParameters, byte[] bArr, byte[] bArr2) {
        super(false, dilithiumParameters);
        this.rho = Arrays.clone(bArr);
        this.t1 = Arrays.clone(bArr2);
    }

    public byte[] getEncoded() {
        return getEncoded(this.rho, this.t1);
    }

    public byte[] getRho() {
        return Arrays.clone(this.rho);
    }

    public byte[] getT1() {
        return Arrays.clone(this.t1);
    }
}
