package org.spongycastle.est.jcajce;

import java.security.Provider;
import java.security.SecureRandom;
import org.spongycastle.est.HttpAuth;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/est/jcajce/JcaHttpAuthBuilder.class */
public class JcaHttpAuthBuilder {
    private JcaDigestCalculatorProviderBuilder providerBuilder;
    private final String realm;
    private final String username;
    private final char[] password;
    private SecureRandom random;

    public JcaHttpAuthBuilder(String username, char[] password) {
        this(null, username, password);
    }

    public JcaHttpAuthBuilder(String realm, String username, char[] password) {
        this.providerBuilder = new JcaDigestCalculatorProviderBuilder();
        this.random = new SecureRandom();
        this.realm = realm;
        this.username = username;
        this.password = password;
    }

    public JcaHttpAuthBuilder setProvider(Provider provider) {
        this.providerBuilder.setProvider(provider);
        return this;
    }

    public JcaHttpAuthBuilder setProvider(String providerName) {
        this.providerBuilder.setProvider(providerName);
        return this;
    }

    public JcaHttpAuthBuilder setNonceGenerator(SecureRandom random) {
        this.random = random;
        return this;
    }

    public HttpAuth build() throws OperatorCreationException {
        return new HttpAuth(this.realm, this.username, this.password, this.random, this.providerBuilder.build());
    }
}
