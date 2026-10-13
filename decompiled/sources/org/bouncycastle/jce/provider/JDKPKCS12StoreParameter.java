package org.bouncycastle.jce.provider;

import java.io.OutputStream;
import java.security.KeyStore;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/provider/JDKPKCS12StoreParameter.class */
public class JDKPKCS12StoreParameter implements KeyStore.LoadStoreParameter {
    private OutputStream outputStream;
    private KeyStore.ProtectionParameter protectionParameter;
    private boolean useDEREncoding;
    private boolean overwriteFriendlyName;

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

    public boolean isOverwriteFriendlyName() {
        return this.overwriteFriendlyName;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setPassword(char[] cArr) {
        this.protectionParameter = new KeyStore.PasswordProtection(cArr);
    }

    public void setProtectionParameter(KeyStore.ProtectionParameter protectionParameter) {
        this.protectionParameter = protectionParameter;
    }

    public void setUseDEREncoding(boolean z) {
        this.useDEREncoding = z;
    }

    public void setOverwriteFriendlyName(boolean z) {
        this.overwriteFriendlyName = z;
    }
}
