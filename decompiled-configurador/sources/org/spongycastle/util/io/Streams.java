package org.spongycastle.util.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/util/io/Streams.class */
public final class Streams {
    private static int BUFFER_SIZE = 4096;

    public static void drain(InputStream inStr) throws IOException {
        byte[] bs = new byte[BUFFER_SIZE];
        while (inStr.read(bs, 0, bs.length) >= 0) {
        }
    }

    public static byte[] readAll(InputStream inStr) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        pipeAll(inStr, buf);
        return buf.toByteArray();
    }

    public static byte[] readAllLimited(InputStream inStr, int limit) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        pipeAllLimited(inStr, limit, buf);
        return buf.toByteArray();
    }

    public static int readFully(InputStream inStr, byte[] buf) throws IOException {
        return readFully(inStr, buf, 0, buf.length);
    }

    public static int readFully(InputStream inStr, byte[] buf, int off, int len) throws IOException {
        int totalRead;
        int numRead;
        int i = 0;
        while (true) {
            totalRead = i;
            if (totalRead >= len || (numRead = inStr.read(buf, off + totalRead, len - totalRead)) < 0) {
                break;
            }
            i = totalRead + numRead;
        }
        return totalRead;
    }

    public static void pipeAll(InputStream inStr, OutputStream outStr) throws IOException {
        byte[] bs = new byte[BUFFER_SIZE];
        while (true) {
            int numRead = inStr.read(bs, 0, bs.length);
            if (numRead >= 0) {
                outStr.write(bs, 0, numRead);
            } else {
                return;
            }
        }
    }

    public static long pipeAllLimited(InputStream inStr, long limit, OutputStream outStr) throws IOException {
        long total = 0;
        byte[] bs = new byte[BUFFER_SIZE];
        while (true) {
            int numRead = inStr.read(bs, 0, bs.length);
            if (numRead < 0) {
                return total;
            }
            if (limit - total < numRead) {
                throw new StreamOverflowException("Data Overflow");
            }
            total += (long) numRead;
            outStr.write(bs, 0, numRead);
        }
    }

    public static void writeBufTo(ByteArrayOutputStream buf, OutputStream output) throws IOException {
        buf.writeTo(output);
    }
}
