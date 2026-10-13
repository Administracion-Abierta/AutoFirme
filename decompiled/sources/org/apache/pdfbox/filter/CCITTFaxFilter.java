package org.apache.pdfbox.filter;

import com.sun.jna.platform.win32.WinError;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/filter/CCITTFaxFilter.class */
final class CCITTFaxFilter extends Filter {
    CCITTFaxFilter() {
    }

    @Override // org.apache.pdfbox.filter.Filter
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index) throws IOException {
        int rows;
        int type;
        COSDictionary decodeParms = getDecodeParams(parameters, index);
        int cols = decodeParms.getInt(COSName.COLUMNS, WinError.RPC_S_PROTOCOL_ERROR);
        int rows2 = decodeParms.getInt(COSName.ROWS, 0);
        int height = parameters.getInt(COSName.HEIGHT, COSName.H, 0);
        if (rows2 > 0 && height > 0) {
            rows = height;
        } else {
            rows = Math.max(rows2, height);
        }
        int k = decodeParms.getInt(COSName.K, 0);
        boolean encodedByteAlign = decodeParms.getBoolean(COSName.ENCODED_BYTE_ALIGN, false);
        int arraySize = ((cols + 7) / 8) * rows;
        byte[] decompressed = new byte[arraySize];
        long tiffOptions = 0;
        if (k == 0) {
            type = 3;
            byte[] streamData = new byte[20];
            int bytesRead = encoded.read(streamData);
            if (bytesRead != streamData.length) {
                throw new EOFException("Can't read " + streamData.length + " bytes");
            }
            PushbackInputStream pushbackInputStream = new PushbackInputStream(encoded, streamData.length);
            pushbackInputStream.unread(streamData);
            encoded = pushbackInputStream;
            if (streamData[0] != 0 || ((streamData[1] >> 4) != 1 && streamData[1] != 1)) {
                type = 2;
                short b = (short) (((streamData[0] << 8) + (streamData[1] & 255)) >> 4);
                for (int i = 12; i < 160; i++) {
                    b = (short) ((b << 1) + ((streamData[i / 8] >> (7 - (i % 8))) & 1));
                    if ((b & 4095) == 1) {
                        type = 3;
                        break;
                    }
                }
            }
        } else if (k > 0) {
            type = 3;
            tiffOptions = 1;
        } else {
            type = 4;
        }
        CCITTFaxDecoderStream s = new CCITTFaxDecoderStream(encoded, cols, type, tiffOptions, encodedByteAlign);
        readFromDecoderStream(s, decompressed);
        boolean blackIsOne = decodeParms.getBoolean(COSName.BLACK_IS_1, false);
        if (!blackIsOne) {
            invertBitmap(decompressed);
        }
        decoded.write(decompressed);
        return new DecodeResult(parameters);
    }

    void readFromDecoderStream(CCITTFaxDecoderStream decoderStream, byte[] result) throws IOException {
        int pos = 0;
        do {
            int read = decoderStream.read(result, pos, result.length - pos);
            if (read > -1) {
                pos += read;
            } else {
                return;
            }
        } while (pos < result.length);
    }

    private void invertBitmap(byte[] bufferData) {
        int c = bufferData.length;
        for (int i = 0; i < c; i++) {
            bufferData[i] = (byte) ((bufferData[i] ^ (-1)) & 255);
        }
    }

    @Override // org.apache.pdfbox.filter.Filter
    protected void encode(InputStream input, OutputStream encoded, COSDictionary parameters) throws IOException {
        int cols = parameters.getInt(COSName.COLUMNS);
        int rows = parameters.getInt(COSName.ROWS);
        CCITTFaxEncoderStream ccittFaxEncoderStream = new CCITTFaxEncoderStream(encoded, cols, rows, 1);
        IOUtils.copy(input, ccittFaxEncoderStream);
    }
}
