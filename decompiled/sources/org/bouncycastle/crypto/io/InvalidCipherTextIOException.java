package org.bouncycastle.crypto.io;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/io/InvalidCipherTextIOException.class */
public class InvalidCipherTextIOException extends CipherIOException {
    private static final long serialVersionUID = 1;

    public InvalidCipherTextIOException(String str, Throwable th) {
        super(str, th);
    }
}
