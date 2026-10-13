package org.apache.pdfbox.io;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.logging.Log;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/io/IOUtils.class */
public final class IOUtils {
    private IOUtils() {
    }

    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream baout = new ByteArrayOutputStream();
        copy(in, baout);
        return baout.toByteArray();
    }

    public static long copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0;
        while (true) {
            int n = input.read(buffer);
            if (-1 != n) {
                output.write(buffer, 0, n);
                count += (long) n;
            } else {
                return count;
            }
        }
    }

    public static long populateBuffer(InputStream in, byte[] buffer) throws IOException {
        int remaining;
        int length = buffer.length;
        while (true) {
            remaining = length;
            if (remaining <= 0) {
                break;
            }
            int bufferWritePos = buffer.length - remaining;
            int bytesRead = in.read(buffer, bufferWritePos, remaining);
            if (bytesRead < 0) {
                break;
            }
            length = remaining - bytesRead;
        }
        return buffer.length - remaining;
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

    public static IOException closeAndLogException(Closeable closeable, Log logger, String resourceName, IOException initialException) {
        try {
            closeable.close();
        } catch (IOException ioe) {
            logger.warn("Error closing " + resourceName, ioe);
            if (initialException == null) {
                return ioe;
            }
        }
        return initialException;
    }
}
