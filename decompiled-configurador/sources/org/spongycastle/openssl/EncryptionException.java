package org.spongycastle.openssl;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/openssl/EncryptionException.class */
public class EncryptionException extends PEMException {
    private Throwable cause;

    public EncryptionException(String msg) {
        super(msg);
    }

    public EncryptionException(String msg, Throwable ex) {
        super(msg);
        this.cause = ex;
    }

    @Override // org.spongycastle.openssl.PEMException, java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
