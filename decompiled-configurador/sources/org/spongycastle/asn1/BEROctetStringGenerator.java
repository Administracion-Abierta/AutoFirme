package org.spongycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/BEROctetStringGenerator.class */
public class BEROctetStringGenerator extends BERGenerator {
    public BEROctetStringGenerator(OutputStream out) throws IOException {
        super(out);
        writeBERHeader(36);
    }

    public BEROctetStringGenerator(OutputStream out, int tagNo, boolean isExplicit) throws IOException {
        super(out, tagNo, isExplicit);
        writeBERHeader(36);
    }

    public OutputStream getOctetOutputStream() {
        return getOctetOutputStream(new byte[1000]);
    }

    public OutputStream getOctetOutputStream(byte[] buf) {
        return new BufferedBEROctetStream(buf);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/BEROctetStringGenerator$BufferedBEROctetStream.class */
    private class BufferedBEROctetStream extends OutputStream {
        private byte[] _buf;
        private int _off = 0;
        private DEROutputStream _derOut;

        BufferedBEROctetStream(byte[] buf) {
            this._buf = buf;
            this._derOut = new DEROutputStream(BEROctetStringGenerator.this._out);
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            byte[] bArr = this._buf;
            int i = this._off;
            this._off = i + 1;
            bArr[i] = (byte) b;
            if (this._off == this._buf.length) {
                DEROctetString.encode(this._derOut, this._buf);
                this._off = 0;
            }
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            while (len > 0) {
                int numToCopy = Math.min(len, this._buf.length - this._off);
                System.arraycopy(b, off, this._buf, this._off, numToCopy);
                this._off += numToCopy;
                if (this._off >= this._buf.length) {
                    DEROctetString.encode(this._derOut, this._buf);
                    this._off = 0;
                    off += numToCopy;
                    len -= numToCopy;
                } else {
                    return;
                }
            }
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            if (this._off != 0) {
                byte[] bytes = new byte[this._off];
                System.arraycopy(this._buf, 0, bytes, 0, this._off);
                DEROctetString.encode(this._derOut, bytes);
            }
            BEROctetStringGenerator.this.writeBEREnd();
        }
    }
}
