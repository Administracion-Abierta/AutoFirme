package org.spongycastle.crypto.params;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/params/SM2KeyExchangePrivateParameters.class */
public class SM2KeyExchangePrivateParameters implements CipherParameters {
    private final boolean initiator;
    private final ECPrivateKeyParameters staticPrivateKey;
    private final ECPoint staticPublicPoint;
    private final ECPrivateKeyParameters ephemeralPrivateKey;
    private final ECPoint ephemeralPublicPoint;

    public SM2KeyExchangePrivateParameters(boolean initiator, ECPrivateKeyParameters staticPrivateKey, ECPrivateKeyParameters ephemeralPrivateKey) {
        if (staticPrivateKey == null) {
            throw new NullPointerException("staticPrivateKey cannot be null");
        }
        if (ephemeralPrivateKey == null) {
            throw new NullPointerException("ephemeralPrivateKey cannot be null");
        }
        ECDomainParameters parameters = staticPrivateKey.getParameters();
        if (!parameters.equals(ephemeralPrivateKey.getParameters())) {
            throw new IllegalArgumentException("Static and ephemeral private keys have different domain parameters");
        }
        this.initiator = initiator;
        this.staticPrivateKey = staticPrivateKey;
        this.staticPublicPoint = parameters.getG().multiply(staticPrivateKey.getD()).normalize();
        this.ephemeralPrivateKey = ephemeralPrivateKey;
        this.ephemeralPublicPoint = parameters.getG().multiply(ephemeralPrivateKey.getD()).normalize();
    }

    public boolean isInitiator() {
        return this.initiator;
    }

    public ECPrivateKeyParameters getStaticPrivateKey() {
        return this.staticPrivateKey;
    }

    public ECPoint getStaticPublicPoint() {
        return this.staticPublicPoint;
    }

    public ECPrivateKeyParameters getEphemeralPrivateKey() {
        return this.ephemeralPrivateKey;
    }

    public ECPoint getEphemeralPublicPoint() {
        return this.ephemeralPublicPoint;
    }
}
