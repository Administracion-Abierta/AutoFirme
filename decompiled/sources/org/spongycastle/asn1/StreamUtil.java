package org.spongycastle.asn1;

import com.sun.jna.platform.win32.WinNT;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/StreamUtil.class */
class StreamUtil {
    private static final long MAX_MEMORY = Runtime.getRuntime().maxMemory();

    StreamUtil() {
    }

    static int findLimit(InputStream in) {
        if (in instanceof LimitedInputStream) {
            return ((LimitedInputStream) in).getRemaining();
        }
        if (in instanceof ASN1InputStream) {
            return ((ASN1InputStream) in).getLimit();
        }
        if (in instanceof ByteArrayInputStream) {
            return ((ByteArrayInputStream) in).available();
        }
        if (in instanceof FileInputStream) {
            try {
                FileChannel channel = ((FileInputStream) in).getChannel();
                long size = channel != null ? channel.size() : 2147483647L;
                if (size < 2147483647L) {
                    return (int) size;
                }
            } catch (IOException e) {
            }
        }
        if (MAX_MEMORY > 2147483647L) {
            return WinNT.MAXLONG;
        }
        return (int) MAX_MEMORY;
    }

    static int calculateBodyLength(int length) {
        int count = 1;
        if (length > 127) {
            int size = 1;
            int val = length;
            while (true) {
                int i = val >>> 8;
                val = i;
                if (i == 0) {
                    break;
                }
                size++;
            }
            for (int i2 = (size - 1) * 8; i2 >= 0; i2 -= 8) {
                count++;
            }
        }
        return count;
    }

    static int calculateTagLength(int tagNo) throws IOException {
        int length = 1;
        if (tagNo >= 31) {
            if (tagNo < 128) {
                length = 1 + 1;
            } else {
                byte[] stack = new byte[5];
                int pos = stack.length - 1;
                stack[pos] = (byte) (tagNo & 127);
                do {
                    tagNo >>= 7;
                    pos--;
                    stack[pos] = (byte) ((tagNo & 127) | 128);
                } while (tagNo > 127);
                length = 1 + (stack.length - pos);
            }
        }
        return length;
    }
}
