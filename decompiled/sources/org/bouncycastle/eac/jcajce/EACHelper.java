package org.bouncycastle.eac.jcajce;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/eac/jcajce/EACHelper.class */
interface EACHelper {
    KeyFactory createKeyFactory(String str) throws NoSuchAlgorithmException, NoSuchProviderException;
}
