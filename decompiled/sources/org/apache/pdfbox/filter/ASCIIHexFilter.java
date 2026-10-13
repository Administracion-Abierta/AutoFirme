package org.apache.pdfbox.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.util.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/filter/ASCIIHexFilter.class */
final class ASCIIHexFilter extends Filter {
    private static final Log LOG = LogFactory.getLog(ASCIIHexFilter.class);
    private static final int[] REVERSE_HEX = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

    ASCIIHexFilter() {
    }

    @Override // org.apache.pdfbox.filter.Filter
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index) throws IOException {
        while (true) {
            int i = encoded.read();
            int firstByte = i;
            if (i == -1) {
                break;
            }
            while (isWhitespace(firstByte)) {
                firstByte = encoded.read();
            }
            if (firstByte == -1 || isEOD(firstByte)) {
                break;
            }
            if (REVERSE_HEX[firstByte] == -1) {
                LOG.error("Invalid hex, int: " + firstByte + " char: " + ((char) firstByte));
            }
            int value = REVERSE_HEX[firstByte] * 16;
            int secondByte = encoded.read();
            if (secondByte == -1 || isEOD(secondByte)) {
                decoded.write(value);
                break;
            }
            if (secondByte >= 0) {
                if (REVERSE_HEX[secondByte] == -1) {
                    LOG.error("Invalid hex, int: " + secondByte + " char: " + ((char) secondByte));
                }
                value += REVERSE_HEX[secondByte];
            }
            decoded.write(value);
        }
        decoded.flush();
        return new DecodeResult(parameters);
    }

    private boolean isWhitespace(int c) {
        return c == 0 || c == 9 || c == 10 || c == 12 || c == 13 || c == 32;
    }

    private boolean isEOD(int c) {
        return c == 62;
    }

    @Override // org.apache.pdfbox.filter.Filter
    public void encode(InputStream input, OutputStream encoded, COSDictionary parameters) throws IOException {
        while (true) {
            int byteRead = input.read();
            if (byteRead != -1) {
                Hex.writeHexByte((byte) byteRead, encoded);
            } else {
                encoded.flush();
                return;
            }
        }
    }
}
