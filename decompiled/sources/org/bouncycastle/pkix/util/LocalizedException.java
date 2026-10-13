package org.bouncycastle.pkix.util;

import java.util.Locale;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkix/util/LocalizedException.class */
public class LocalizedException extends Exception {
    protected ErrorBundle message;
    private Throwable cause;

    public LocalizedException(ErrorBundle errorBundle) {
        super(errorBundle.getText(Locale.getDefault()));
        this.message = errorBundle;
    }

    public LocalizedException(ErrorBundle errorBundle, Throwable th) {
        super(errorBundle.getText(Locale.getDefault()));
        this.message = errorBundle;
        this.cause = th;
    }

    public ErrorBundle getErrorMessage() {
        return this.message;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
