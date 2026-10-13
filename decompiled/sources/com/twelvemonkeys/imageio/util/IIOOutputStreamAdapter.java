package com.twelvemonkeys.imageio.util;

import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.stream.ImageOutputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/util/IIOOutputStreamAdapter.class */
class IIOOutputStreamAdapter extends OutputStream {
    private ImageOutputStream output;

    public IIOOutputStreamAdapter(ImageOutputStream imageOutputStream) {
        Validate.notNull(imageOutputStream, "stream == null");
        this.output = imageOutputStream;
    }

    @Override // java.io.OutputStream
    public void write(byte[] bArr) throws IOException {
        assertOpen();
        this.output.write(bArr);
    }

    @Override // java.io.OutputStream
    public void write(byte[] bArr, int i, int i2) throws IOException {
        assertOpen();
        this.output.write(bArr, i, i2);
    }

    @Override // java.io.OutputStream
    public void write(int i) throws IOException {
        assertOpen();
        this.output.write(i);
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        assertOpen();
    }

    private void assertOpen() throws IOException {
        if (this.output == null) {
            throw new IOException("stream already closed");
        }
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.output = null;
    }
}
