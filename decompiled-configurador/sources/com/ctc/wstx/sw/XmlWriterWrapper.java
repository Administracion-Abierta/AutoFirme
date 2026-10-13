package com.ctc.wstx.sw;

import java.io.IOException;
import java.io.Writer;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/sw/XmlWriterWrapper.class */
public abstract class XmlWriterWrapper extends Writer {
    protected final XmlWriter mWriter;
    private char[] mBuffer = null;

    @Override // java.io.Writer
    public abstract void write(char[] cArr, int i, int i2) throws IOException;

    @Override // java.io.Writer
    public abstract void write(String str) throws IOException;

    @Override // java.io.Writer
    public abstract void write(String str, int i, int i2) throws IOException;

    public static XmlWriterWrapper wrapWriteRaw(XmlWriter xw) {
        return new RawWrapper(xw);
    }

    public static XmlWriterWrapper wrapWriteCharacters(XmlWriter xw) {
        return new TextWrapper(xw);
    }

    protected XmlWriterWrapper(XmlWriter writer) {
        this.mWriter = writer;
    }

    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public final void close() throws IOException {
        this.mWriter.close(false);
    }

    @Override // java.io.Writer, java.io.Flushable
    public final void flush() throws IOException {
        this.mWriter.flush();
    }

    @Override // java.io.Writer
    public final void write(char[] cbuf) throws IOException {
        write(cbuf, 0, cbuf.length);
    }

    @Override // java.io.Writer
    public final void write(int c) throws IOException {
        if (this.mBuffer == null) {
            this.mBuffer = new char[1];
        }
        this.mBuffer[0] = (char) c;
        write(this.mBuffer, 0, 1);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/sw/XmlWriterWrapper$RawWrapper.class */
    private static final class RawWrapper extends XmlWriterWrapper {
        protected RawWrapper(XmlWriter writer) {
            super(writer);
        }

        @Override // com.ctc.wstx.sw.XmlWriterWrapper, java.io.Writer
        public void write(char[] cbuf, int off, int len) throws IOException {
            this.mWriter.writeRaw(cbuf, off, len);
        }

        @Override // com.ctc.wstx.sw.XmlWriterWrapper, java.io.Writer
        public void write(String str, int off, int len) throws IOException {
            this.mWriter.writeRaw(str, off, len);
        }

        @Override // com.ctc.wstx.sw.XmlWriterWrapper, java.io.Writer
        public final void write(String str) throws IOException {
            this.mWriter.writeRaw(str, 0, str.length());
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/sw/XmlWriterWrapper$TextWrapper.class */
    private static class TextWrapper extends XmlWriterWrapper {
        protected TextWrapper(XmlWriter writer) {
            super(writer);
        }

        @Override // com.ctc.wstx.sw.XmlWriterWrapper, java.io.Writer
        public void write(char[] cbuf, int off, int len) throws IOException {
            this.mWriter.writeCharacters(cbuf, off, len);
        }

        @Override // com.ctc.wstx.sw.XmlWriterWrapper, java.io.Writer
        public void write(String str) throws IOException {
            this.mWriter.writeCharacters(str);
        }

        @Override // com.ctc.wstx.sw.XmlWriterWrapper, java.io.Writer
        public void write(String str, int off, int len) throws IOException {
            this.mWriter.writeCharacters(str.substring(off, off + len));
        }
    }
}
