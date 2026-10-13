package org.apache.xml.security.utils;

import java.io.ByteArrayOutputStream;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.signature.XMLSignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/utils/SignerOutputStream.class */
public class SignerOutputStream extends ByteArrayOutputStream {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) SignerOutputStream.class);
    final SignatureAlgorithm sa;

    public SignerOutputStream(SignatureAlgorithm sa) {
        this.sa = sa;
    }

    @Override // java.io.OutputStream
    public void write(byte[] arg0) {
        try {
            this.sa.update(arg0);
        } catch (XMLSignatureException e) {
            throw new RuntimeException("" + e);
        }
    }

    @Override // java.io.ByteArrayOutputStream, java.io.OutputStream
    public void write(int arg0) {
        try {
            this.sa.update((byte) arg0);
        } catch (XMLSignatureException e) {
            throw new RuntimeException("" + e);
        }
    }

    @Override // java.io.ByteArrayOutputStream, java.io.OutputStream
    public void write(byte[] arg0, int arg1, int arg2) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Canonicalized SignedInfo:");
            StringBuilder sb = new StringBuilder(arg2);
            for (int i = arg1; i < arg1 + arg2; i++) {
                sb.append((char) arg0[i]);
            }
            LOG.debug(sb.toString());
        }
        try {
            this.sa.update(arg0, arg1, arg2);
        } catch (XMLSignatureException e) {
            throw new RuntimeException("" + e);
        }
    }
}
