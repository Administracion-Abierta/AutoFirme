package org.spongycastle.asn1;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/ASN1Exception.class */
public class ASN1Exception extends IOException {
    private Throwable cause;

    ASN1Exception(String message) {
        super(message);
    }

    ASN1Exception(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
