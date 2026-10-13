package org.spongycastle.jce.provider;

import java.io.OutputStream;
import java.security.KeyStore;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/provider/JDKPKCS12StoreParameter.class */
public class JDKPKCS12StoreParameter implements KeyStore.LoadStoreParameter {
    private OutputStream outputStream;
    private KeyStore.ProtectionParameter protectionParameter;
    private boolean useDEREncoding;

    public OutputStream getOutputStream() {
        return this.outputStream;
    }

    @Override // java.security.KeyStore.LoadStoreParameter
    public KeyStore.ProtectionParameter getProtectionParameter() {
        return this.protectionParameter;
    }

    public boolean isUseDEREncoding() {
        return this.useDEREncoding;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setPassword(char[] password) {
        this.protectionParameter = new KeyStore.PasswordProtection(password);
    }

    public void setProtectionParameter(KeyStore.ProtectionParameter protectionParameter) {
        this.protectionParameter = protectionParameter;
    }

    public void setUseDEREncoding(boolean useDEREncoding) {
        this.useDEREncoding = useDEREncoding;
    }
}
