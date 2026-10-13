package org.apache.pdfbox.cos;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.io.RandomAccess;
import org.apache.pdfbox.io.RandomAccessInputStream;
import org.apache.pdfbox.io.RandomAccessOutputStream;
import org.apache.pdfbox.io.ScratchFile;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/cos/COSOutputStream.class */
public final class COSOutputStream extends FilterOutputStream {
    private final List<Filter> filters;
    private final COSDictionary parameters;
    private final ScratchFile scratchFile;
    private RandomAccess buffer;

    COSOutputStream(List<Filter> filters, COSDictionary parameters, OutputStream output, ScratchFile scratchFile) throws IOException {
        super(output);
        this.filters = filters;
        this.parameters = parameters;
        this.scratchFile = scratchFile;
        if (filters.isEmpty()) {
            this.buffer = null;
        } else {
            this.buffer = scratchFile.createBuffer();
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b) throws IOException {
        if (this.buffer != null) {
            this.buffer.write(b);
        } else {
            super.write(b);
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        if (this.buffer != null) {
            this.buffer.write(b, off, len);
        } else {
            super.write(b, off, len);
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int b) throws IOException {
        if (this.buffer != null) {
            this.buffer.write(b);
        } else {
            super.write(b);
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        if (this.buffer == null) {
            super.flush();
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        try {
            if (this.buffer != null) {
                try {
                    for (int i = this.filters.size() - 1; i >= 0; i--) {
                        InputStream unfilteredIn = new RandomAccessInputStream(this.buffer);
                        if (i == 0) {
                            try {
                                this.filters.get(i).encode(unfilteredIn, this.out, this.parameters, i);
                            } catch (Throwable th) {
                                unfilteredIn.close();
                                throw th;
                            }
                        } else {
                            RandomAccess filteredBuffer = this.scratchFile.createBuffer();
                            try {
                                OutputStream filteredOut = new RandomAccessOutputStream(filteredBuffer);
                                try {
                                    this.filters.get(i).encode(unfilteredIn, filteredOut, this.parameters, i);
                                    filteredOut.close();
                                    RandomAccess filteredBuffer2 = this.buffer;
                                    this.buffer = filteredBuffer;
                                    filteredBuffer2.close();
                                } catch (Throwable th2) {
                                    filteredOut.close();
                                    throw th2;
                                }
                            } catch (Throwable th3) {
                                filteredBuffer.close();
                                throw th3;
                            }
                        }
                        unfilteredIn.close();
                    }
                    this.buffer.close();
                    this.buffer = null;
                } catch (Throwable th4) {
                    this.buffer.close();
                    this.buffer = null;
                    throw th4;
                }
            }
            super.close();
        } catch (Throwable th5) {
            super.close();
            throw th5;
        }
    }
}
