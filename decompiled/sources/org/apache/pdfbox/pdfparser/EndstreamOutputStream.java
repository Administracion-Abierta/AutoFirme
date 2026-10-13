package org.apache.pdfbox.pdfparser;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfparser/EndstreamOutputStream.class */
class EndstreamOutputStream extends BufferedOutputStream {
    private boolean hasCR;
    private boolean hasLF;
    private int pos;
    private boolean mustFilter;

    EndstreamOutputStream(OutputStream out) {
        super(out);
        this.hasCR = false;
        this.hasLF = false;
        this.pos = 0;
        this.mustFilter = true;
    }

    @Override // java.io.BufferedOutputStream, java.io.FilterOutputStream, java.io.OutputStream
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        if (this.pos == 0 && len > 10) {
            this.mustFilter = false;
            for (int i = 0; i < 10; i++) {
                if (b[i] < 9 || (b[i] > 10 && b[i] < 32 && b[i] != 13)) {
                    this.mustFilter = true;
                    break;
                }
            }
        }
        if (this.mustFilter) {
            if (this.hasCR) {
                this.hasCR = false;
                if (!this.hasLF && len == 1 && b[off] == 10) {
                    return;
                } else {
                    super.write(13);
                }
            }
            if (this.hasLF) {
                super.write(10);
                this.hasLF = false;
            }
            if (len > 0) {
                if (b[(off + len) - 1] == 13) {
                    this.hasCR = true;
                    len--;
                } else if (b[(off + len) - 1] == 10) {
                    this.hasLF = true;
                    len--;
                    if (len > 0 && b[(off + len) - 1] == 13) {
                        this.hasCR = true;
                        len--;
                    }
                }
            }
        }
        super.write(b, off, len);
        this.pos += len;
    }

    @Override // java.io.BufferedOutputStream, java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public synchronized void flush() throws IOException {
        if (this.hasCR && !this.hasLF) {
            super.write(13);
            this.pos++;
        }
        this.hasCR = false;
        this.hasLF = false;
        super.flush();
    }
}
