package org.spongycastle.crypto.tls;

import java.math.BigInteger;
import org.spongycastle.crypto.params.SRP6GroupParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/TlsSRPLoginParameters.class */
public class TlsSRPLoginParameters {
    protected SRP6GroupParameters group;
    protected BigInteger verifier;
    protected byte[] salt;

    public TlsSRPLoginParameters(SRP6GroupParameters group, BigInteger verifier, byte[] salt) {
        this.group = group;
        this.verifier = verifier;
        this.salt = salt;
    }

    public SRP6GroupParameters getGroup() {
        return this.group;
    }

    public byte[] getSalt() {
        return this.salt;
    }

    public BigInteger getVerifier() {
        return this.verifier;
    }
}
