package org.apache.jcp.xml.dsig.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import org.apache.xml.security.utils.UnsyncByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/jcp/xml/dsig/internal/DigesterOutputStream.class */
public class DigesterOutputStream extends OutputStream {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) DigesterOutputStream.class);
    private final boolean buffer;
    private UnsyncByteArrayOutputStream bos;
    private final MessageDigest md;

    public DigesterOutputStream(MessageDigest md) {
        this(md, false);
    }

    public DigesterOutputStream(MessageDigest md, boolean buffer) {
        this.md = md;
        this.buffer = buffer;
        if (buffer) {
            this.bos = new UnsyncByteArrayOutputStream();
        }
    }

    @Override // java.io.OutputStream
    public void write(int input) {
        if (this.buffer) {
            this.bos.write(input);
        }
        this.md.update((byte) input);
    }

    @Override // java.io.OutputStream
    public void write(byte[] input, int offset, int len) {
        if (this.buffer) {
            this.bos.write(input, offset, len);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Pre-digested input:");
            StringBuilder sb = new StringBuilder(len);
            for (int i = offset; i < offset + len; i++) {
                sb.append((char) input[i]);
            }
            LOG.debug(sb.toString());
        }
        this.md.update(input, offset, len);
    }

    public byte[] getDigestValue() {
        return this.md.digest();
    }

    public InputStream getInputStream() {
        if (this.buffer) {
            return new ByteArrayInputStream(this.bos.toByteArray());
        }
        return null;
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.buffer) {
            this.bos.close();
        }
    }
}
