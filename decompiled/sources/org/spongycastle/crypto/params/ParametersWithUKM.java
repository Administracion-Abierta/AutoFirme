package org.spongycastle.crypto.params;

import org.spongycastle.crypto.CipherParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/params/ParametersWithUKM.class */
public class ParametersWithUKM implements CipherParameters {
    private byte[] ukm;
    private CipherParameters parameters;

    public ParametersWithUKM(CipherParameters parameters, byte[] ukm) {
        this(parameters, ukm, 0, ukm.length);
    }

    public ParametersWithUKM(CipherParameters parameters, byte[] ukm, int ivOff, int ivLen) {
        this.ukm = new byte[ivLen];
        this.parameters = parameters;
        System.arraycopy(ukm, ivOff, this.ukm, 0, ivLen);
    }

    public byte[] getUKM() {
        return this.ukm;
    }

    public CipherParameters getParameters() {
        return this.parameters;
    }
}
