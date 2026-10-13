package org.spongycastle.crypto.agreement.srp;

import java.math.BigInteger;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.params.SRP6GroupParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/agreement/srp/SRP6VerifierGenerator.class */
public class SRP6VerifierGenerator {
    protected BigInteger N;
    protected BigInteger g;
    protected Digest digest;

    public void init(BigInteger N, BigInteger g, Digest digest) {
        this.N = N;
        this.g = g;
        this.digest = digest;
    }

    public void init(SRP6GroupParameters group, Digest digest) {
        this.N = group.getN();
        this.g = group.getG();
        this.digest = digest;
    }

    public BigInteger generateVerifier(byte[] salt, byte[] identity, byte[] password) {
        BigInteger x = SRP6Util.calculateX(this.digest, this.N, salt, identity, password);
        return this.g.modPow(x, this.N);
    }
}
