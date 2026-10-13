package org.bouncycastle.jcajce;

import java.io.OutputStream;
import java.security.KeyStore;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/PKCS12StoreParameter.class */
public class PKCS12StoreParameter implements KeyStore.LoadStoreParameter {
    private final OutputStream out;
    private final KeyStore.ProtectionParameter protectionParameter;
    private final boolean forDEREncoding;
    private final boolean overwriteFriendlyName;

    public PKCS12StoreParameter(OutputStream outputStream, char[] cArr) {
        this(outputStream, cArr, false);
    }

    public PKCS12StoreParameter(OutputStream outputStream, KeyStore.ProtectionParameter protectionParameter) {
        this(outputStream, protectionParameter, false, true);
    }

    public PKCS12StoreParameter(OutputStream outputStream, char[] cArr, boolean z) {
        this(outputStream, (KeyStore.ProtectionParameter) new KeyStore.PasswordProtection(cArr), z, true);
    }

    public PKCS12StoreParameter(OutputStream outputStream, KeyStore.ProtectionParameter protectionParameter, boolean z) {
        this(outputStream, protectionParameter, z, true);
    }

    public PKCS12StoreParameter(OutputStream outputStream, char[] cArr, boolean z, boolean z2) {
        this(outputStream, new KeyStore.PasswordProtection(cArr), z, z2);
    }

    public PKCS12StoreParameter(OutputStream outputStream, KeyStore.ProtectionParameter protectionParameter, boolean z, boolean z2) {
        this.out = outputStream;
        this.protectionParameter = protectionParameter;
        this.forDEREncoding = z;
        this.overwriteFriendlyName = z2;
    }

    public OutputStream getOutputStream() {
        return this.out;
    }

    @Override // java.security.KeyStore.LoadStoreParameter
    public KeyStore.ProtectionParameter getProtectionParameter() {
        return this.protectionParameter;
    }

    public boolean isForDEREncoding() {
        return this.forDEREncoding;
    }

    public boolean isOverwriteFriendlyName() {
        return this.overwriteFriendlyName;
    }
}
