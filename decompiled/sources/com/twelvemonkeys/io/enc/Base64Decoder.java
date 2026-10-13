package com.twelvemonkeys.io.enc;

import com.github.markusbernhardt.proxy.search.wpad.dhcp.DHCPOptions;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/enc/Base64Decoder.class */
public final class Base64Decoder implements Decoder {
    static final byte[] PEM_ARRAY = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    static final byte[] PEM_CONVERT_ARRAY = new byte[256];
    private byte[] decodeBuffer = new byte[4];

    static {
        for (int i = 0; i < 255; i++) {
            PEM_CONVERT_ARRAY[i] = -1;
        }
        for (int i2 = 0; i2 < PEM_ARRAY.length; i2++) {
            PEM_CONVERT_ARRAY[PEM_ARRAY[i2]] = (byte) i2;
        }
    }

    protected static int readFully(InputStream inputStream, byte[] bArr, int i, int i2) throws IOException {
        for (int i3 = 0; i3 < i2; i3++) {
            int i4 = inputStream.read();
            if (i4 == -1) {
                if (i3 != 0) {
                    return i3;
                }
                return -1;
            }
            bArr[i3 + i] = (byte) i4;
        }
        return i2;
    }

    protected boolean decodeAtom(InputStream inputStream, ByteBuffer byteBuffer, int i) throws IOException {
        byte b = -1;
        byte b2 = -1;
        byte b3 = -1;
        byte b4 = -1;
        if (i < 2) {
            throw new IOException("BASE64Decoder: Not enough bytes for an atom.");
        }
        while (true) {
            int i2 = inputStream.read();
            if (i2 == -1) {
                return false;
            }
            if (i2 != 10 && i2 != 13) {
                this.decodeBuffer[0] = (byte) i2;
                if (readFully(inputStream, this.decodeBuffer, 1, i - 1) == -1) {
                    return false;
                }
                int i3 = i;
                if (i3 > 3 && this.decodeBuffer[3] == 61) {
                    i3 = 3;
                }
                if (i3 > 2 && this.decodeBuffer[2] == 61) {
                    i3 = 2;
                }
                switch (i3) {
                    case 4:
                        b4 = PEM_CONVERT_ARRAY[this.decodeBuffer[3] & 255];
                    case 3:
                        b3 = PEM_CONVERT_ARRAY[this.decodeBuffer[2] & 255];
                    case 2:
                        b2 = PEM_CONVERT_ARRAY[this.decodeBuffer[1] & 255];
                        b = PEM_CONVERT_ARRAY[this.decodeBuffer[0] & 255];
                        break;
                }
                switch (i3) {
                    case 2:
                        byteBuffer.put((byte) (((b << 2) & DHCPOptions.OPTION_PROXY_AUTODISCOVERY) | ((b2 >>> 4) & 3)));
                        return true;
                    case 3:
                        byteBuffer.put((byte) (((b << 2) & DHCPOptions.OPTION_PROXY_AUTODISCOVERY) | ((b2 >>> 4) & 3)));
                        byteBuffer.put((byte) (((b2 << 4) & 240) | ((b3 >>> 2) & 15)));
                        return true;
                    case 4:
                        byteBuffer.put((byte) (((b << 2) & DHCPOptions.OPTION_PROXY_AUTODISCOVERY) | ((b2 >>> 4) & 3)));
                        byteBuffer.put((byte) (((b2 << 4) & 240) | ((b3 >>> 2) & 15)));
                        byteBuffer.put((byte) (((b3 << 6) & 192) | (b4 & 63)));
                        return true;
                    default:
                        return true;
                }
            }
        }
    }

    @Override // com.twelvemonkeys.io.enc.Decoder
    public int decode(InputStream inputStream, ByteBuffer byteBuffer) throws IOException {
        do {
            int i = 0;
            while (i + 4 < 72 && decodeAtom(inputStream, byteBuffer, 4)) {
                i += 4;
            }
            if (!decodeAtom(inputStream, byteBuffer, 72 - i)) {
                break;
            }
        } while (byteBuffer.remaining() > 54);
        return byteBuffer.position();
    }
}
