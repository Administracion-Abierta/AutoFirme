package org.apache.pdfbox.pdfwriter;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfwriter/COSStandardOutputStream.class */
public class COSStandardOutputStream extends FilterOutputStream {
    public static final byte[] CRLF = {13, 10};
    public static final byte[] LF = {10};
    public static final byte[] EOL = {10};
    private long position;
    private boolean onNewLine;

    public COSStandardOutputStream(OutputStream out) {
        super(out);
        this.position = 0L;
        this.onNewLine = false;
    }

    @Deprecated
    public COSStandardOutputStream(OutputStream out, int position) {
        super(out);
        this.position = 0L;
        this.onNewLine = false;
        this.position = position;
    }

    public COSStandardOutputStream(OutputStream out, long position) {
        super(out);
        this.position = 0L;
        this.onNewLine = false;
        this.position = position;
    }

    public long getPos() {
        return this.position;
    }

    public boolean isOnNewLine() {
        return this.onNewLine;
    }

    public void setOnNewLine(boolean newOnNewLine) {
        this.onNewLine = newOnNewLine;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        setOnNewLine(false);
        this.out.write(b, off, len);
        this.position += (long) len;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int b) throws IOException {
        setOnNewLine(false);
        this.out.write(b);
        this.position++;
    }

    public void writeCRLF() throws IOException {
        write(CRLF);
    }

    public void writeEOL() throws IOException {
        if (!isOnNewLine()) {
            write(EOL);
            setOnNewLine(true);
        }
    }

    public void writeLF() throws IOException {
        write(LF);
    }
}
