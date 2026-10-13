package org.bouncycastle.est.jcajce;

import java.security.Provider;
import java.security.SecureRandom;
import org.bouncycastle.est.HttpAuth;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/jcajce/JcaHttpAuthBuilder.class */
public class JcaHttpAuthBuilder {
    private JcaDigestCalculatorProviderBuilder providerBuilder;
    private final String realm;
    private final String username;
    private final char[] password;
    private SecureRandom random;

    public JcaHttpAuthBuilder(String str, char[] cArr) {
        this(null, str, cArr);
    }

    public JcaHttpAuthBuilder(String str, String str2, char[] cArr) {
        this.providerBuilder = new JcaDigestCalculatorProviderBuilder();
        this.random = new SecureRandom();
        this.realm = str;
        this.username = str2;
        this.password = cArr;
    }

    public JcaHttpAuthBuilder setProvider(Provider provider) {
        this.providerBuilder.setProvider(provider);
        return this;
    }

    public JcaHttpAuthBuilder setProvider(String str) {
        this.providerBuilder.setProvider(str);
        return this;
    }

    public JcaHttpAuthBuilder setNonceGenerator(SecureRandom secureRandom) {
        this.random = secureRandom;
        return this;
    }

    public HttpAuth build() throws OperatorCreationException {
        return new HttpAuth(this.realm, this.username, this.password, this.random, this.providerBuilder.build());
    }
}
