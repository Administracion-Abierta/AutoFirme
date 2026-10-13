package org.spongycastle.openssl.jcajce;

import java.security.Provider;
import org.spongycastle.jcajce.util.DefaultJcaJceHelper;
import org.spongycastle.jcajce.util.JcaJceHelper;
import org.spongycastle.jcajce.util.NamedJcaJceHelper;
import org.spongycastle.jcajce.util.ProviderJcaJceHelper;
import org.spongycastle.openssl.PEMDecryptor;
import org.spongycastle.openssl.PEMDecryptorProvider;
import org.spongycastle.openssl.PEMException;
import org.spongycastle.openssl.PasswordException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/jcajce/JcePEMDecryptorProviderBuilder.class */
public class JcePEMDecryptorProviderBuilder {
    private JcaJceHelper helper = new DefaultJcaJceHelper();

    public JcePEMDecryptorProviderBuilder setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public JcePEMDecryptorProviderBuilder setProvider(String providerName) {
        this.helper = new NamedJcaJceHelper(providerName);
        return this;
    }

    public PEMDecryptorProvider build(final char[] password) {
        return new PEMDecryptorProvider() { // from class: org.spongycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder.1
            @Override // org.spongycastle.openssl.PEMDecryptorProvider
            public PEMDecryptor get(final String dekAlgName) {
                return new PEMDecryptor() { // from class: org.spongycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder.1.1
                    @Override // org.spongycastle.openssl.PEMDecryptor
                    public byte[] decrypt(byte[] keyBytes, byte[] iv) throws PEMException {
                        if (password == null) {
                            throw new PasswordException("Password is null, but a password is required");
                        }
                        return PEMUtilities.crypt(false, JcePEMDecryptorProviderBuilder.this.helper, keyBytes, password, dekAlgName, iv);
                    }
                };
            }
        };
    }
}
