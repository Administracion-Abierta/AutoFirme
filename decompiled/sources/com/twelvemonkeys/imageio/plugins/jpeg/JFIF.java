package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.metadata.jpeg.JPEG;
import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JFIF.class */
final class JFIF extends Application {
    final int majorVersion;
    final int minorVersion;
    final int units;
    final int xDensity;
    final int yDensity;
    final int xThumbnail;
    final int yThumbnail;
    final byte[] thumbnail;

    JFIF(int i, int i2, int i3, int i4, int i5, int i6, int i7, byte[] bArr) {
        super(JPEG.APP0, "JFIF", new byte[14 + (bArr != null ? bArr.length : 0)]);
        this.majorVersion = i;
        this.minorVersion = i2;
        this.units = i3;
        this.xDensity = i4;
        this.yDensity = i5;
        this.xThumbnail = i6;
        this.yThumbnail = i7;
        this.thumbnail = bArr;
    }

    @Override // com.twelvemonkeys.imageio.plugins.jpeg.Application
    public String toString() {
        return String.format("APP0/JFIF v%d.%02d %dx%d %s (%s)", Integer.valueOf(this.majorVersion), Integer.valueOf(this.minorVersion), Integer.valueOf(this.xDensity), Integer.valueOf(this.yDensity), unitsAsString(), thumbnailToString());
    }

    private String unitsAsString() {
        switch (this.units) {
            case 0:
                return "(aspect only)";
            case 1:
                return "dpi";
            case 2:
                return "dpcm";
            default:
                return "(unknown unit)";
        }
    }

    private String thumbnailToString() {
        if (this.xThumbnail == 0 || this.yThumbnail == 0) {
            return "no thumbnail";
        }
        return String.format("thumbnail: %dx%d", Integer.valueOf(this.xThumbnail), Integer.valueOf(this.yThumbnail));
    }

    public static JFIF read(DataInput dataInput, int i) throws IOException {
        if (i < 16) {
            throw new EOFException();
        }
        dataInput.readFully(new byte[5]);
        byte[] bArr = new byte[(i - 2) - 5];
        dataInput.readFully(bArr);
        ByteBuffer byteBufferWrap = ByteBuffer.wrap(bArr);
        int i2 = byteBufferWrap.get() & 255;
        int i3 = byteBufferWrap.get() & 255;
        int i4 = byteBufferWrap.get() & 255;
        int i5 = byteBufferWrap.getShort() & 65535;
        int i6 = byteBufferWrap.getShort() & 65535;
        int i7 = byteBufferWrap.get() & 255;
        int i8 = byteBufferWrap.get() & 255;
        return new JFIF(i2, i3, i4, i5, i6, i7, i8, getBytes(byteBufferWrap, Math.min(byteBufferWrap.remaining(), i7 * i8 * 3)));
    }

    private static byte[] getBytes(ByteBuffer byteBuffer, int i) {
        if (i == 0) {
            return null;
        }
        byte[] bArr = new byte[i];
        byteBuffer.get(bArr);
        return bArr;
    }
}
