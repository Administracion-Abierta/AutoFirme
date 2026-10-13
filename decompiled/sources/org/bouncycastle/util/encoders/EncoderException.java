package org.bouncycastle.util.encoders;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/encoders/EncoderException.class */
public class EncoderException extends IllegalStateException {
    private Throwable cause;

    EncoderException(String str, Throwable th) {
        super(str);
        this.cause = th;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
