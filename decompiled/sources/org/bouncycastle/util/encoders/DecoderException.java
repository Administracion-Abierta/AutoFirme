package org.bouncycastle.util.encoders;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/encoders/DecoderException.class */
public class DecoderException extends IllegalStateException {
    private Throwable cause;

    DecoderException(String str, Throwable th) {
        super(str);
        this.cause = th;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
