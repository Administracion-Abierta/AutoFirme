package org.spongycastle.jcajce.util;

import java.security.Provider;
import java.security.Security;
import org.spongycastle.jce.provider.BouncyCastleProvider;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/util/BCJcaJceHelper.class */
public class BCJcaJceHelper extends ProviderJcaJceHelper {
    private static volatile Provider bcProvider;

    private static Provider getBouncyCastleProvider() {
        if (Security.getProvider("SC") != null) {
            return Security.getProvider("SC");
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
