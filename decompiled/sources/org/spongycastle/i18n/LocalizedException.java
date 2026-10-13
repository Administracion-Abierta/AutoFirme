package org.spongycastle.i18n;

import java.util.Locale;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/i18n/LocalizedException.class */
public class LocalizedException extends Exception {
    protected ErrorBundle message;
    private Throwable cause;

    public LocalizedException(ErrorBundle message) {
        super(message.getText(Locale.getDefault()));
        this.message = message;
    }

    public LocalizedException(ErrorBundle message, Throwable throwable) {
        super(message.getText(Locale.getDefault()));
        this.message = message;
        this.cause = throwable;
    }

    public ErrorBundle getErrorMessage() {
        return this.message;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
