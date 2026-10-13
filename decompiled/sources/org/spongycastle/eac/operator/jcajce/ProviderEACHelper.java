package org.spongycastle.eac.operator.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Signature;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/eac/operator/jcajce/ProviderEACHelper.class */
class ProviderEACHelper extends EACHelper {
    private final Provider provider;

    ProviderEACHelper(Provider provider) {
        this.provider = provider;
    }

    @Override // org.spongycastle.eac.operator.jcajce.EACHelper
    protected Signature createSignature(String type) throws NoSuchAlgorithmException {
        return Signature.getInstance(type, this.provider);
    }
}
