package com.aowagie.text.pdf;

import com.aowagie.text.Document;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.channels.FileChannel;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/RandomAccessFileOrArray.class */
public class RandomAccessFileOrArray implements DataInput {
    private MappedRandomAccessFile rf;
    private RandomAccessFile trf;
    private boolean plainRandomAccess;
    private String filename;
    private byte[] arrayIn;
    private int arrayInPtr;
    private byte back;
    private boolean isBack;
    private int startOffset;

    public RandomAccessFileOrArray(String filename) throws IOException {
        this(filename, false, Document.plainRandomAccess);
    }

    RandomAccessFileOrArray(String filename, boolean forceRead, boolean plainRandomAccess) throws IOException {
        this.isBack = false;
        this.startOffset = 0;
        this.plainRandomAccess = plainRandomAccess;
        File file = new File(filename);
        if (!file.canRead()) {
            if (filename.startsWith("file:/") || filename.startsWith("http://") || filename.startsWith("https://") || filename.startsWith("jar:")) {
                InputStream is = new URL(filename).openStream();
                try {
                    this.arrayIn = InputStreamToArray(is);
                    try {
                        return;
                    } catch (IOException e) {
                        return;
                    }
                } finally {
                    try {
                        is.close();
                    } catch (IOException e2) {
                    }
                }
            }
            InputStream is2 = BaseFont.getResourceStream(filename);
            if (is2 == null) {
                throw new IOException(filename + " not found as file or resource.");
            }
            try {
                this.arrayIn = InputStreamToArray(is2);
                try {
                    return;
                } catch (IOException e3) {
                    return;
                }
            } finally {
                try {
                    is2.close();
                } catch (IOException e4) {
                }
            }
        }
        if (forceRead) {
            InputStream s = null;
            try {
                InputStream s2 = new FileInputStream(file);
                this.arrayIn = InputStreamToArray(s2);
                if (s2 != null) {
                    try {
                    } catch (Exception e5) {
                        return;
                    }
                }
                return;
            } finally {
                if (s != null) {
                    try {
                        s.close();
                    } catch (Exception e6) {
                    }
                }
            }
        }
        this.filename = filename;
        if (plainRandomAccess) {
            this.trf = new RandomAccessFile(filename, PDPageLabelRange.STYLE_ROMAN_LOWER);
        } else {
            this.rf = new MappedRandomAccessFile(filename, PDPageLabelRange.STYLE_ROMAN_LOWER);
        }
    }

    public RandomAccessFileOrArray(URL url) throws IOException {
        this.isBack = false;
        this.startOffset = 0;
        InputStream is = url.openStream();
        try {
            this.arrayIn = InputStreamToArray(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
    }

    RandomAccessFileOrArray(InputStream is) throws IOException {
        this.isBack = false;
        this.startOffset = 0;
        this.arrayIn = InputStreamToArray(is);
    }

    private static byte[] InputStreamToArray(InputStream is) throws IOException {
        byte[] b = new byte[8192];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (true) {
            int read = is.read(b);
            if (read >= 1) {
                out.write(b, 0, read);
            } else {
                out.close();
                return out.toByteArray();
            }
        }
    }

    public RandomAccessFileOrArray(byte[] arrayIn) {
        this.isBack = false;
        this.startOffset = 0;
        this.arrayIn = arrayIn;
    }

    RandomAccessFileOrArray(RandomAccessFileOrArray file) {
        this.isBack = false;
        this.startOffset = 0;
        this.filename = file.filename;
        this.arrayIn = file.arrayIn;
        this.startOffset = file.startOffset;
        this.plainRandomAccess = file.plainRandomAccess;
    }

    void pushBack(byte b) {
        this.back = b;
        this.isBack = true;
    }

    public int read() throws IOException {
        if (this.isBack) {
            this.isBack = false;
            return this.back & 255;
        }
        if (this.arrayIn == null) {
            return this.plainRandomAccess ? this.trf.read() : this.rf.read();
        }
        if (this.arrayInPtr >= this.arrayIn.length) {
            return -1;
        }
        byte[] bArr = this.arrayIn;
        int i = this.arrayInPtr;
        this.arrayInPtr = i + 1;
        return bArr[i] & 255;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        int n = 0;
        if (this.isBack) {
            this.isBack = false;
            if (len == 1) {
                b[off] = this.back;
                return 1;
            }
            n = 1;
            off++;
            b[off] = this.back;
            len--;
        }
        if (this.arrayIn == null) {
            return (this.plainRandomAccess ? this.trf.read(b, off, len) : this.rf.read(b, off, len)) + n;
        }
        if (this.arrayInPtr >= this.arrayIn.length) {
            return -1;
        }
        if (this.arrayInPtr + len > this.arrayIn.length) {
            len = this.arrayIn.length - this.arrayInPtr;
        }
        System.arraycopy(this.arrayIn, this.arrayInPtr, b, off, len);
        this.arrayInPtr += len;
        return len + n;
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override // java.io.DataInput
    public void readFully(byte[] b) throws IOException {
        readFully(b, 0, b.length);
    }

    @Override // java.io.DataInput
    public void readFully(byte[] b, int off, int len) throws IOException {
        int n = 0;
        do {
            int count = read(b, off + n, len - n);
            if (count < 0) {
                throw new EOFException();
            }
            n += count;
        } while (n < len);
    }

    public long skip(long n) throws IOException {
        return skipBytes((int) n);
    }

    @Override // java.io.DataInput
    public int skipBytes(int n) throws IOException {
        if (n <= 0) {
            return 0;
        }
        int adj = 0;
        if (this.isBack) {
            this.isBack = false;
            if (n == 1) {
                return 1;
            }
            n--;
            adj = 1;
        }
        int pos = getFilePointer();
        int len = length();
        int newpos = pos + n;
        if (newpos > len) {
            newpos = len;
        }
        seek(newpos);
        return (newpos - pos) + adj;
    }

    public void reOpen() throws IOException {
        if (this.filename != null && this.rf == null && this.trf == null) {
            if (this.plainRandomAccess) {
                this.trf = new RandomAccessFile(this.filename, PDPageLabelRange.STYLE_ROMAN_LOWER);
            } else {
                this.rf = new MappedRandomAccessFile(this.filename, PDPageLabelRange.STYLE_ROMAN_LOWER);
            }
        }
        seek(0);
    }

    private void insureOpen() throws IOException {
        if (this.filename != null && this.rf == null && this.trf == null) {
            reOpen();
        }
    }

    public boolean isOpen() {
        return (this.filename != null && this.rf == null && this.trf == null) ? false : true;
    }

    public void close() throws IOException {
        this.isBack = false;
        if (this.rf != null) {
            this.rf.close();
            this.rf = null;
            this.plainRandomAccess = true;
        } else if (this.trf != null) {
            this.trf.close();
            this.trf = null;
        }
    }

    public int length() throws IOException {
        if (this.arrayIn == null) {
            insureOpen();
            return ((int) (this.plainRandomAccess ? this.trf.length() : this.rf.length())) - this.startOffset;
        }
        return this.arrayIn.length - this.startOffset;
    }

    public void seek(int pos) throws IOException {
        int pos2 = pos + this.startOffset;
        this.isBack = false;
        if (this.arrayIn == null) {
            insureOpen();
            if (this.plainRandomAccess) {
                this.trf.seek(pos2);
                return;
            } else {
                this.rf.seek(pos2);
                return;
            }
        }
        this.arrayInPtr = pos2;
    }

    public void seek(long pos) throws IOException {
        seek((int) pos);
    }

    public int getFilePointer() throws IOException {
        insureOpen();
        int n = this.isBack ? 1 : 0;
        if (this.arrayIn == null) {
            return (((int) (this.plainRandomAccess ? this.trf.getFilePointer() : this.rf.getFilePointer())) - n) - this.startOffset;
        }
        return (this.arrayInPtr - n) - this.startOffset;
    }

    @Override // java.io.DataInput
    public boolean readBoolean() throws IOException {
        int ch = read();
        if (ch < 0) {
            throw new EOFException();
        }
        return ch != 0;
    }

    @Override // java.io.DataInput
    public byte readByte() throws IOException {
        int ch = read();
        if (ch < 0) {
            throw new EOFException();
        }
        return (byte) ch;
    }

    @Override // java.io.DataInput
    public int readUnsignedByte() throws IOException {
        int ch = read();
        if (ch < 0) {
            throw new EOFException();
        }
        return ch;
    }

    @Override // java.io.DataInput
    public short readShort() throws IOException {
        int ch1 = read();
        int ch2 = read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (short) ((ch1 << 8) + ch2);
    }

    public final short readShortLE() throws IOException {
        int ch1 = read();
        int ch2 = read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (short) ((ch2 << 8) + (ch1 << 0));
    }

    @Override // java.io.DataInput
    public int readUnsignedShort() throws IOException {
        int ch1 = read();
        int ch2 = read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (ch1 << 8) + ch2;
    }

    public final int readUnsignedShortLE() throws IOException {
        int ch1 = read();
        int ch2 = read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (ch2 << 8) + (ch1 << 0);
    }

    @Override // java.io.DataInput
    public char readChar() throws IOException {
        int ch1 = read();
        int ch2 = read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (char) ((ch1 << 8) + ch2);
    }

    @Override // java.io.DataInput
    public int readInt() throws IOException {
        int ch1 = read();
        int ch2 = read();
        int ch3 = read();
        int ch4 = read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4;
    }

    public final int readIntLE() throws IOException {
        int ch1 = read();
        int ch2 = read();
        int ch3 = read();
        int ch4 = read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
    }

    public final long readUnsignedInt() throws IOException {
        long ch1 = read();
        long ch2 = read();
        long ch3 = read();
        long ch4 = read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
    }

    public final long readUnsignedIntLE() throws IOException {
        long ch1 = read();
        long ch2 = read();
        long ch3 = read();
        long ch4 = read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
    }

    @Override // java.io.DataInput
    public long readLong() throws IOException {
        return (((long) readInt()) << 32) + (((long) readInt()) & 4294967295L);
    }

    public final long readLongLE() throws IOException {
        int i1 = readIntLE();
        int i2 = readIntLE();
        return (((long) i2) << 32) + (((long) i1) & 4294967295L);
    }

    @Override // java.io.DataInput
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public final float readFloatLE() throws IOException {
        return Float.intBitsToFloat(readIntLE());
    }

    @Override // java.io.DataInput
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    public final double readDoubleLE() throws IOException {
        return Double.longBitsToDouble(readLongLE());
    }

    @Override // java.io.DataInput
    public String readLine() throws IOException {
        StringBuffer input = new StringBuffer();
        int c = -1;
        boolean eol = false;
        while (!eol) {
            int i = read();
            c = i;
            switch (i) {
                case -1:
                case 10:
                    eol = true;
                    break;
                case 13:
                    eol = true;
                    int cur = getFilePointer();
                    if (read() != 10) {
                        seek(cur);
                    }
                    break;
                default:
                    input.append((char) c);
                    break;
            }
        }
        if (c == -1 && input.length() == 0) {
            return null;
        }
        return input.toString();
    }

    @Override // java.io.DataInput
    public String readUTF() throws IOException {
        return DataInputStream.readUTF(this);
    }

    public int getStartOffset() {
        return this.startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public java.nio.ByteBuffer getNioByteBuffer() throws IOException {
        FileChannel channel;
        if (this.filename != null) {
            if (this.plainRandomAccess) {
                channel = this.trf.getChannel();
            } else {
                channel = this.rf.getChannel();
            }
            return channel.map(FileChannel.MapMode.READ_ONLY, 0L, channel.size());
        }
        return java.nio.ByteBuffer.wrap(this.arrayIn);
    }
}
