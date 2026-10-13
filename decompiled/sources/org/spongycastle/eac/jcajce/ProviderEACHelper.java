package org.spongycastle.eac.jcajce;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/eac/jcajce/ProviderEACHelper.class */
class ProviderEACHelper implements EACHelper {
    private final Provider provider;

    ProviderEACHelper(Provider provider) {
        this.provider = provider;
    }

    @Override // org.spongycastle.eac.jcajce.EACHelper
    public KeyFactory createKeyFactory(String type) throws NoSuchAlgorithmException {
        return KeyFactory.getInstance(type, this.provider);
    }
}
