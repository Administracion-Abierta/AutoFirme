package org.bouncycastle.pqc.crypto;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ExchangePairGenerator.class */
public interface ExchangePairGenerator {
    ExchangePair generateExchange(AsymmetricKeyParameter asymmetricKeyParameter);
}
