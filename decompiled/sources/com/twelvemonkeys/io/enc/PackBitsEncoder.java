package com.twelvemonkeys.io.enc;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/enc/PackBitsEncoder.class */
public final class PackBitsEncoder implements Encoder {
    private final byte[] buffer = new byte[128];

    @Override // com.twelvemonkeys.io.enc.Encoder
    public void encode(OutputStream outputStream, ByteBuffer byteBuffer) throws IOException {
        encode(outputStream, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining());
        byteBuffer.position(byteBuffer.remaining());
    }

    private void encode(OutputStream outputStream, byte[] bArr, int i, int i2) throws IOException {
        int i3 = i;
        int i4 = (i + i2) - 1;
        int i5 = i4 - 1;
        while (i3 <= i4) {
            int i6 = 1;
            byte b = bArr[i3];
            while (i6 < 127 && i3 < i4 && bArr[i3] == bArr[i3 + 1]) {
                i3++;
                i6++;
            }
            if (i6 > 1) {
                i3++;
                outputStream.write(-(i6 - 1));
                outputStream.write(b);
            }
            int i7 = 0;
            while (i7 < 128 && ((i3 < i4 && bArr[i3] != bArr[i3 + 1]) || (i3 < i5 && bArr[i3] != bArr[i3 + 2]))) {
                int i8 = i7;
                i7++;
                int i9 = i3;
                i3++;
                this.buffer[i8] = bArr[i9];
            }
            if (i3 == i4 && i7 > 0 && i7 < 128) {
                int i10 = i7;
                i7++;
                int i11 = i3;
                i3++;
                this.buffer[i10] = bArr[i11];
            }
            if (i7 > 0) {
                outputStream.write(i7 - 1);
                outputStream.write(this.buffer, 0, i7);
            }
            if (i3 == i4 && (i7 <= 0 || i7 >= 128)) {
                outputStream.write(0);
                int i12 = i3;
                i3++;
                outputStream.write(bArr[i12]);
            }
        }
    }
}
