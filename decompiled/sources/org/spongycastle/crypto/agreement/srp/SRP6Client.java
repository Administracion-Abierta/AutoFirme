package org.spongycastle.crypto.agreement.srp;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.params.SRP6GroupParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/agreement/srp/SRP6Client.class */
public class SRP6Client {
    protected BigInteger N;
    protected BigInteger g;
    protected BigInteger a;
    protected BigInteger A;
    protected BigInteger B;
    protected BigInteger x;
    protected BigInteger u;
    protected BigInteger S;
    protected BigInteger M1;
    protected BigInteger M2;
    protected BigInteger Key;
    protected Digest digest;
    protected SecureRandom random;

    public void init(BigInteger N, BigInteger g, Digest digest, SecureRandom random) {
        this.N = N;
        this.g = g;
        this.digest = digest;
        this.random = random;
    }

    public void init(SRP6GroupParameters group, Digest digest, SecureRandom random) {
        init(group.getN(), group.getG(), digest, random);
    }

    public BigInteger generateClientCredentials(byte[] salt, byte[] identity, byte[] password) {
        this.x = SRP6Util.calculateX(this.digest, this.N, salt, identity, password);
        this.a = selectPrivateValue();
        this.A = this.g.modPow(this.a, this.N);
        return this.A;
    }

    public BigInteger calculateSecret(BigInteger serverB) throws CryptoException {
        this.B = SRP6Util.validatePublicValue(this.N, serverB);
        this.u = SRP6Util.calculateU(this.digest, this.N, this.A, this.B);
        this.S = calculateS();
        return this.S;
    }

    protected BigInteger selectPrivateValue() {
        return SRP6Util.generatePrivateValue(this.digest, this.N, this.g, this.random);
    }

    private BigInteger calculateS() {
        BigInteger k = SRP6Util.calculateK(this.digest, this.N, this.g);
        BigInteger exp = this.u.multiply(this.x).add(this.a);
        BigInteger tmp = this.g.modPow(this.x, this.N).multiply(k).mod(this.N);
        return this.B.subtract(tmp).mod(this.N).modPow(exp, this.N);
    }

    public BigInteger calculateClientEvidenceMessage() throws CryptoException {
        if (this.A == null || this.B == null || this.S == null) {
            throw new CryptoException("Impossible to compute M1: some data are missing from the previous operations (A,B,S)");
        }
        this.M1 = SRP6Util.calculateM1(this.digest, this.N, this.A, this.B, this.S);
        return this.M1;
    }

    public boolean verifyServerEvidenceMessage(BigInteger serverM2) throws CryptoException {
        if (this.A == null || this.M1 == null || this.S == null) {
            throw new CryptoException("Impossible to compute and verify M2: some data are missing from the previous operations (A,M1,S)");
        }
        BigInteger computedM2 = SRP6Util.calculateM2(this.digest, this.N, this.A, this.M1, this.S);
        if (computedM2.equals(serverM2)) {
            this.M2 = serverM2;
            return true;
        }
        return false;
    }

    public BigInteger calculateSessionKey() throws CryptoException {
        if (this.S == null || this.M1 == null || this.M2 == null) {
            throw new CryptoException("Impossible to compute Key: some data are missing from the previous operations (S,M1,M2)");
        }
        this.Key = SRP6Util.calculateKey(this.digest, this.N, this.S);
        return this.Key;
    }
}
