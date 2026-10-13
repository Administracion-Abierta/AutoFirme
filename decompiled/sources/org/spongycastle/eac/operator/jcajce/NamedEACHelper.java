package org.spongycastle.eac.operator.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/eac/operator/jcajce/NamedEACHelper.class */
class NamedEACHelper extends EACHelper {
    private final String providerName;

    NamedEACHelper(String providerName) {
        this.providerName = providerName;
    }

    @Override // org.spongycastle.eac.operator.jcajce.EACHelper
    protected Signature createSignature(String type) throws NoSuchAlgorithmException, NoSuchProviderException {
        return Signature.getInstance(type, this.providerName);
    }
}
