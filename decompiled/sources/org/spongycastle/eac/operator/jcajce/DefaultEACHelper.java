package org.spongycastle.eac.operator.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.Signature;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/eac/operator/jcajce/DefaultEACHelper.class */
class DefaultEACHelper extends EACHelper {
    DefaultEACHelper() {
    }

    @Override // org.spongycastle.eac.operator.jcajce.EACHelper
    protected Signature createSignature(String type) throws NoSuchAlgorithmException {
        return Signature.getInstance(type);
    }
}
