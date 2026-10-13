package org.apache.fontbox.cff;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/DataOutput.class */
public class DataOutput {
    private ByteArrayOutputStream outputBuffer;
    private String outputEncoding;

    public DataOutput() {
        this("ISO-8859-1");
    }

    public DataOutput(String encoding) {
        this.outputBuffer = new ByteArrayOutputStream();
        this.outputEncoding = null;
        this.outputEncoding = encoding;
    }

    public byte[] getBytes() {
        return this.outputBuffer.toByteArray();
    }

    public void write(int value) {
        this.outputBuffer.write(value);
    }

    public void write(byte[] buffer) {
        this.outputBuffer.write(buffer, 0, buffer.length);
    }

    public void write(byte[] buffer, int offset, int length) {
        this.outputBuffer.write(buffer, offset, length);
    }

    public void print(String string) throws IOException {
        write(string.getBytes(this.outputEncoding));
    }

    public void println(String string) throws IOException {
        write(string.getBytes(this.outputEncoding));
        write(10);
    }

    public void println() {
        write(10);
    }
}
