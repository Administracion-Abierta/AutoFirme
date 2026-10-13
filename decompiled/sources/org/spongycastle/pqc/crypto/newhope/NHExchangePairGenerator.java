package org.spongycastle.pqc.crypto.newhope;

import java.security.SecureRandom;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.pqc.crypto.ExchangePair;
import org.spongycastle.pqc.crypto.ExchangePairGenerator;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/newhope/NHExchangePairGenerator.class */
public class NHExchangePairGenerator implements ExchangePairGenerator {
    private final SecureRandom random;

    public NHExchangePairGenerator(SecureRandom random) {
        this.random = random;
    }

    @Override // org.spongycastle.pqc.crypto.ExchangePairGenerator
    public ExchangePair GenerateExchange(AsymmetricKeyParameter senderPublicKey) {
        return generateExchange(senderPublicKey);
    }

    @Override // org.spongycastle.pqc.crypto.ExchangePairGenerator
    public ExchangePair generateExchange(AsymmetricKeyParameter senderPublicKey) {
        NHPublicKeyParameters pubKey = (NHPublicKeyParameters) senderPublicKey;
        byte[] sharedValue = new byte[32];
        byte[] publicKeyValue = new byte[2048];
        NewHope.sharedB(this.random, sharedValue, publicKeyValue, pubKey.pubData);
        return new ExchangePair(new NHPublicKeyParameters(publicKeyValue), sharedValue);
    }
}
