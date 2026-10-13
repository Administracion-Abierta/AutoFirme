package org.spongycastle.asn1;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/ASN1ParsingException.class */
public class ASN1ParsingException extends IllegalStateException {
    private Throwable cause;

    public ASN1ParsingException(String message) {
        super(message);
    }

    public ASN1ParsingException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
