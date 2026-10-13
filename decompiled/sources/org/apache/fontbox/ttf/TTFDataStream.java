package org.apache.fontbox.ttf;

import com.sun.jna.platform.win32.WinError;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.TimeZone;
import org.apache.fontbox.util.Charsets;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/TTFDataStream.class */
abstract class TTFDataStream implements Closeable {
    public abstract int read() throws IOException;

    public abstract long readLong() throws IOException;

    public abstract int readUnsignedShort() throws IOException;

    public abstract short readSignedShort() throws IOException;

    public abstract void seek(long j) throws IOException;

    public abstract int read(byte[] bArr, int i, int i2) throws IOException;

    public abstract long getCurrentPosition() throws IOException;

    public abstract InputStream getOriginalData() throws IOException;

    public abstract long getOriginalDataSize();

    TTFDataStream() {
    }

    public float read32Fixed() throws IOException {
        float retval = readSignedShort();
        return (float) (((double) retval) + (((double) readUnsignedShort()) / 65536.0d));
    }

    public String readString(int length) throws IOException {
        return readString(length, Charsets.ISO_8859_1);
    }

    public String readString(int length, String charset) throws IOException {
        byte[] buffer = read(length);
        return new String(buffer, charset);
    }

    public String readString(int length, Charset charset) throws IOException {
        byte[] buffer = read(length);
        return new String(buffer, charset);
    }

    public int readSignedByte() throws IOException {
        int signedByte = read();
        return signedByte <= 127 ? signedByte : signedByte - 256;
    }

    public int readUnsignedByte() throws IOException {
        int unsignedByte = read();
        if (unsignedByte == -1) {
            throw new EOFException("premature EOF");
        }
        return unsignedByte;
    }

    public long readUnsignedInt() throws IOException {
        long byte1 = read();
        long byte2 = read();
        long byte3 = read();
        long byte4 = read();
        if (byte4 < 0) {
            throw new EOFException();
        }
        return (byte1 << 24) + (byte2 << 16) + (byte3 << 8) + byte4;
    }

    public int[] readUnsignedByteArray(int length) throws IOException {
        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = read();
        }
        return array;
    }

    public int[] readUnsignedShortArray(int length) throws IOException {
        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = readUnsignedShort();
        }
        return array;
    }

    public Calendar readInternationalDate() throws IOException {
        long secondsSince1904 = readLong();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(WinError.ERROR_ALREADY_WAITING, 0, 1, 0, 0, 0);
        cal.set(14, 0);
        long millisFor1904 = cal.getTimeInMillis();
        cal.setTimeInMillis(millisFor1904 + (secondsSince1904 * 1000));
        return cal;
    }

    public String readTag() throws IOException {
        return new String(read(4), Charsets.US_ASCII);
    }

    public byte[] read(int numberOfBytes) throws IOException {
        int totalAmountRead;
        int amountRead;
        byte[] data = new byte[numberOfBytes];
        int i = 0;
        while (true) {
            totalAmountRead = i;
            if (totalAmountRead >= numberOfBytes || (amountRead = read(data, totalAmountRead, numberOfBytes - totalAmountRead)) == -1) {
                break;
            }
            i = totalAmountRead + amountRead;
        }
        if (totalAmountRead == numberOfBytes) {
            return data;
        }
        throw new IOException("Unexpected end of TTF stream reached");
    }
}
