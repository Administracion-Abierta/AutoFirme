package com.twelvemonkeys.io;

import com.twelvemonkeys.lang.DateUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/WriterOutputStream.class */
public class WriterOutputStream extends OutputStream {
    protected Writer writer;
    protected final Decoder decoder;
    final ByteArrayOutputStream bufferStream;
    private volatile boolean isFlushing;
    private static final boolean NIO_AVAILABLE = isNIOAvailable();

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/WriterOutputStream$Decoder.class */
    private interface Decoder {
        void decodeTo(Writer writer, byte[] bArr, int i, int i2) throws IOException;
    }

    private static boolean isNIOAvailable() {
        try {
            Class.forName("java.nio.charset.Charset");
            return true;
        } catch (Throwable th) {
            return false;
        }
    }

    public WriterOutputStream(Writer writer, String str) {
        this.bufferStream = new FastByteArrayOutputStream(1024);
        this.isFlushing = false;
        this.writer = writer;
        this.decoder = getDecoder(str);
    }

    public WriterOutputStream(Writer writer) {
        this(writer, null);
    }

    private static Decoder getDecoder(String str) {
        if (NIO_AVAILABLE) {
            return new CharsetDecoder(str);
        }
        return new StringDecoder(str);
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        flush();
        this.writer.close();
        this.writer = null;
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        flushBuffer();
        this.writer.flush();
    }

    @Override // java.io.OutputStream
    public final void write(byte[] bArr) throws IOException {
        if (bArr == null) {
            throw new NullPointerException("bytes == null");
        }
        write(bArr, 0, bArr.length);
    }

    @Override // java.io.OutputStream
    public final void write(byte[] bArr, int i, int i2) throws IOException {
        flushBuffer();
        this.decoder.decodeTo(this.writer, bArr, i, i2);
    }

    @Override // java.io.OutputStream
    public final void write(int i) {
        this.bufferStream.write(i);
    }

    private void flushBuffer() throws IOException {
        if (!this.isFlushing && this.bufferStream.size() > 0) {
            this.isFlushing = true;
            this.bufferStream.writeTo(this);
            this.bufferStream.reset();
            this.isFlushing = false;
        }
    }

    public static void main(String[] strArr) throws IOException {
        byte[] bytes = "������ klashf lkash ljah lhaaklhghdfgu ksd".getBytes("UTF-8");
        PrintWriter printWriter = new PrintWriter(new NullOutputStream());
        StringDecoder stringDecoder = new StringDecoder("UTF-8");
        for (int i = 0; i < 10000; i++) {
            stringDecoder.decodeTo(printWriter, bytes, 0, bytes.length);
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        for (int i2 = 0; i2 < 1000000; i2++) {
            stringDecoder.decodeTo(printWriter, bytes, 0, bytes.length);
        }
        long jDelta = DateUtil.delta(jCurrentTimeMillis);
        System.out.println("StringDecoder");
        System.out.println("time: " + jDelta);
        StringWriter stringWriter = new StringWriter();
        stringDecoder.decodeTo(stringWriter, bytes, 0, bytes.length);
        String string = stringWriter.toString();
        System.out.println("str: \"" + string + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
        System.out.println("chars.length: " + string.length());
        System.out.println();
        if (NIO_AVAILABLE) {
            CharsetDecoder charsetDecoder = new CharsetDecoder("UTF-8");
            for (int i3 = 0; i3 < 10000; i3++) {
                charsetDecoder.decodeTo(printWriter, bytes, 0, bytes.length);
            }
            long jCurrentTimeMillis2 = System.currentTimeMillis();
            for (int i4 = 0; i4 < 1000000; i4++) {
                charsetDecoder.decodeTo(printWriter, bytes, 0, bytes.length);
            }
            long jDelta2 = DateUtil.delta(jCurrentTimeMillis2);
            System.out.println("CharsetDecoder");
            System.out.println("time: " + jDelta2);
            StringWriter stringWriter2 = new StringWriter();
            charsetDecoder.decodeTo(stringWriter2, bytes, 0, bytes.length);
            String string2 = stringWriter2.toString();
            System.out.println("str: \"" + string2 + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
            System.out.println("chars.length: " + string2.length());
            System.out.println();
        }
        WriterOutputStream writerOutputStream = new WriterOutputStream(new PrintWriter(System.out), "UTF-8");
        writerOutputStream.write(bytes);
        writerOutputStream.flush();
        System.out.println();
        for (byte b : bytes) {
            writerOutputStream.write(b & 255);
        }
        writerOutputStream.flush();
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/WriterOutputStream$CharsetDecoder.class */
    private static final class CharsetDecoder implements Decoder {
        final Charset mCharset;

        CharsetDecoder(String str) {
            this.mCharset = Charset.forName(str != null ? str : System.getProperty("file.encoding", "ISO-8859-1"));
        }

        @Override // com.twelvemonkeys.io.WriterOutputStream.Decoder
        public void decodeTo(Writer writer, byte[] bArr, int i, int i2) throws IOException {
            CharBuffer charBufferDecode = this.mCharset.decode(ByteBuffer.wrap(bArr, i, i2));
            writer.write(charBufferDecode.array(), 0, charBufferDecode.length());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/WriterOutputStream$StringDecoder.class */
    private static final class StringDecoder implements Decoder {
        final String mCharset;

        StringDecoder(String str) {
            this.mCharset = str;
        }

        @Override // com.twelvemonkeys.io.WriterOutputStream.Decoder
        public void decodeTo(Writer writer, byte[] bArr, int i, int i2) throws IOException {
            writer.write(this.mCharset == null ? new String(bArr, i, i2) : new String(bArr, i, i2, this.mCharset));
        }
    }
}
