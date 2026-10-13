package org.spongycastle.eac.jcajce;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/eac/jcajce/NamedEACHelper.class */
class NamedEACHelper implements EACHelper {
    private final String providerName;

    NamedEACHelper(String providerName) {
        this.providerName = providerName;
    }

    @Override // org.spongycastle.eac.jcajce.EACHelper
    public KeyFactory createKeyFactory(String type) throws NoSuchAlgorithmException, NoSuchProviderException {
        return KeyFactory.getInstance(type, this.providerName);
    }
}
