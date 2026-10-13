package org.bouncycastle.jcajce.util;

import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/util/BCJcaJceHelper.class */
public class BCJcaJceHelper extends ProviderJcaJceHelper {
    private static volatile Provider bcProvider;

    private static synchronized Provider getBouncyCastleProvider() {
        Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider instanceof BouncyCastleProvider) {
            return provider;
        }
        if (bcProvider != null) {
            return bcProvider;
        }
        bcProvider = new BouncyCastleProvider();
        return bcProvider;
    }

    public BCJcaJceHelper() {
        super(getBouncyCastleProvider());
    }
}
