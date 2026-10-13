package es.gob.afirma.signers.ooxml;

import java.io.FilterInputStream;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/ooxml/NoCloseInputStream.class */
final class NoCloseInputStream extends FilterInputStream {
    NoCloseInputStream(InputStream inputStream) {
        super(inputStream);
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }
}
