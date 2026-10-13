package org.spongycastle.jcajce.provider.asymmetric.util;

import java.security.spec.InvalidKeySpecException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/util/ExtendedInvalidKeySpecException.class */
public class ExtendedInvalidKeySpecException extends InvalidKeySpecException {
    private Throwable cause;

    public ExtendedInvalidKeySpecException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
