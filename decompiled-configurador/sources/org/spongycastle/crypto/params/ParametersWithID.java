package org.spongycastle.crypto.params;

import org.spongycastle.crypto.CipherParameters;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/params/ParametersWithID.class */
public class ParametersWithID implements CipherParameters {
    private CipherParameters parameters;
    private byte[] id;

    public ParametersWithID(CipherParameters parameters, byte[] id) {
        this.parameters = parameters;
        this.id = id;
    }

    public byte[] getID() {
        return this.id;
    }

    public CipherParameters getParameters() {
        return this.parameters;
    }
}
