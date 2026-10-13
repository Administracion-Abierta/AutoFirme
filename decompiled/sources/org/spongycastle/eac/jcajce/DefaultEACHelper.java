package org.spongycastle.eac.jcajce;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/eac/jcajce/DefaultEACHelper.class */
class DefaultEACHelper implements EACHelper {
    DefaultEACHelper() {
    }

    @Override // org.spongycastle.eac.jcajce.EACHelper
    public KeyFactory createKeyFactory(String type) throws NoSuchAlgorithmException {
        return KeyFactory.getInstance(type);
    }
}
