package org.spongycastle.crypto.params;

import org.spongycastle.crypto.CipherParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/params/ParametersWithSalt.class */
public class ParametersWithSalt implements CipherParameters {
    private byte[] salt;
    private CipherParameters parameters;

    public ParametersWithSalt(CipherParameters parameters, byte[] salt) {
        this(parameters, salt, 0, salt.length);
    }

    public ParametersWithSalt(CipherParameters parameters, byte[] salt, int saltOff, int saltLen) {
        this.salt = new byte[saltLen];
        this.parameters = parameters;
        System.arraycopy(salt, saltOff, this.salt, 0, saltLen);
    }

    public byte[] getSalt() {
        return this.salt;
    }

    public CipherParameters getParameters() {
        return this.parameters;
    }
}
