package org.spongycastle.crypto.params;

import java.math.BigInteger;
import org.spongycastle.crypto.CipherParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/params/GOST3410Parameters.class */
public class GOST3410Parameters implements CipherParameters {
    private BigInteger p;
    private BigInteger q;
    private BigInteger a;
    private GOST3410ValidationParameters validation;

    public GOST3410Parameters(BigInteger p, BigInteger q, BigInteger a) {
        this.p = p;
        this.q = q;
        this.a = a;
    }

    public GOST3410Parameters(BigInteger p, BigInteger q, BigInteger a, GOST3410ValidationParameters params) {
        this.a = a;
        this.p = p;
        this.q = q;
        this.validation = params;
    }

    public BigInteger getP() {
        return this.p;
    }

    public BigInteger getQ() {
        return this.q;
    }

    public BigInteger getA() {
        return this.a;
    }

    public GOST3410ValidationParameters getValidationParameters() {
        return this.validation;
    }

    public int hashCode() {
        return (this.p.hashCode() ^ this.q.hashCode()) ^ this.a.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof GOST3410Parameters)) {
            return false;
        }
        GOST3410Parameters pm = (GOST3410Parameters) obj;
        return pm.getP().equals(this.p) && pm.getQ().equals(this.q) && pm.getA().equals(this.a);
    }
}
