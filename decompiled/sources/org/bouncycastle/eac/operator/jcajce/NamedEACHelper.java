package org.bouncycastle.eac.operator.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/eac/operator/jcajce/NamedEACHelper.class */
class NamedEACHelper extends EACHelper {
    private final String providerName;

    NamedEACHelper(String str) {
        this.providerName = str;
    }

    @Override // org.bouncycastle.eac.operator.jcajce.EACHelper
    protected Signature createSignature(String str) throws NoSuchAlgorithmException, NoSuchProviderException {
        return Signature.getInstance(str, this.providerName);
    }
}
