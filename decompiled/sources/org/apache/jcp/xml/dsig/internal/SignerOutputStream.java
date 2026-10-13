package org.apache.jcp.xml.dsig.internal;

import java.io.ByteArrayOutputStream;
import java.security.Signature;
import java.security.SignatureException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/SignerOutputStream.class */
public class SignerOutputStream extends ByteArrayOutputStream {
    private final Signature sig;

    public SignerOutputStream(Signature sig) {
        this.sig = sig;
    }

    @Override // java.io.ByteArrayOutputStream, java.io.OutputStream
    public void write(int arg0) {
        super.write(arg0);
        try {
            this.sig.update((byte) arg0);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // java.io.ByteArrayOutputStream, java.io.OutputStream
    public void write(byte[] arg0, int arg1, int arg2) {
        super.write(arg0, arg1, arg2);
        try {
            this.sig.update(arg0, arg1, arg2);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }
}
