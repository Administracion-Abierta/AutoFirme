package org.spongycastle.util.encoders;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/util/encoders/EncoderException.class */
public class EncoderException extends IllegalStateException {
    private Throwable cause;

    EncoderException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
