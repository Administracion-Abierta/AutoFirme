package org.bouncycastle.jcajce;

import java.io.OutputStream;
import java.security.KeyStore;
import org.bouncycastle.crypto.util.PBKDFConfig;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/BCFKSStoreParameter.class */
public class BCFKSStoreParameter implements KeyStore.LoadStoreParameter {
    private final KeyStore.ProtectionParameter protectionParameter;
    private final PBKDFConfig storeConfig;
    private OutputStream out;

    public BCFKSStoreParameter(OutputStream outputStream, PBKDFConfig pBKDFConfig, char[] cArr) {
        this(outputStream, pBKDFConfig, new KeyStore.PasswordProtection(cArr));
    }

    public BCFKSStoreParameter(OutputStream outputStream, PBKDFConfig pBKDFConfig, KeyStore.ProtectionParameter protectionParameter) {
        this.out = outputStream;
        this.storeConfig = pBKDFConfig;
        this.protectionParameter = protectionParameter;
    }

    @Override // java.security.KeyStore.LoadStoreParameter
    public KeyStore.ProtectionParameter getProtectionParameter() {
        return this.protectionParameter;
    }

    public OutputStream getOutputStream() {
        return this.out;
    }

    public PBKDFConfig getStorePBKDFConfig() {
        return this.storeConfig;
    }
}
