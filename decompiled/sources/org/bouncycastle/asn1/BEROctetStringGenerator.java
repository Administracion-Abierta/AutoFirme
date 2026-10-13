package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/BEROctetStringGenerator.class */
public class BEROctetStringGenerator extends BERGenerator {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/BEROctetStringGenerator$BufferedBEROctetStream.class */
    private class BufferedBEROctetStream extends OutputStream {
        private byte[] _buf;
        private int _off = 0;
        private DEROutputStream _derOut;

        BufferedBEROctetStream(byte[] bArr) {
            this._buf = bArr;
            this._derOut = new DEROutputStream(BEROctetStringGenerator.this._out);
        }

        @Override // java.io.OutputStream
        public void write(int i) throws IOException {
            byte[] bArr = this._buf;
            int i2 = this._off;
            this._off = i2 + 1;
            bArr[i2] = (byte) i;
            if (this._off == this._buf.length) {
                DEROctetString.encode(this._derOut, true, this._buf, 0, this._buf.length);
                this._off = 0;
            }
        }

        @Override // java.io.OutputStream
        public void write(byte[] bArr, int i, int i2) throws IOException {
            int length = this._buf.length;
            int i3 = length - this._off;
            if (i2 < i3) {
                System.arraycopy(bArr, i, this._buf, this._off, i2);
                this._off += i2;
                return;
            }
            int i4 = 0;
            if (this._off > 0) {
                System.arraycopy(bArr, i, this._buf, this._off, i3);
                i4 = 0 + i3;
                DEROctetString.encode(this._derOut, true, this._buf, 0, length);
            }
            while (true) {
                int i5 = i2 - i4;
                if (i5 < length) {
                    System.arraycopy(bArr, i + i4, this._buf, 0, i5);
                    this._off = i5;
                    return;
                } else {
                    DEROctetString.encode(this._derOut, true, bArr, i + i4, length);
                    i4 += length;
                }
            }
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            if (this._off != 0) {
                DEROctetString.encode(this._derOut, true, this._buf, 0, this._off);
            }
            this._derOut.flushInternal();
            BEROctetStringGenerator.this.writeBEREnd();
        }
    }

    public BEROctetStringGenerator(OutputStream outputStream) throws IOException {
        super(outputStream);
        writeBERHeader(36);
    }

    public BEROctetStringGenerator(OutputStream outputStream, int i, boolean z) throws IOException {
        super(outputStream, i, z);
        writeBERHeader(36);
    }

    public OutputStream getOctetOutputStream() {
        return getOctetOutputStream(new byte[1000]);
    }

    public OutputStream getOctetOutputStream(byte[] bArr) {
        return new BufferedBEROctetStream(bArr);
    }
}
