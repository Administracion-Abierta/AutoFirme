package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.metadata.jpeg.JPEG;
import java.io.DataInput;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/AdobeDCT.class */
final class AdobeDCT extends Application {
    static final int Unknown = 0;
    static final int YCC = 1;
    static final int YCCK = 2;
    final int version;
    final int flags0;
    final int flags1;
    final int transform;

    private AdobeDCT(int i, int i2, int i3, int i4) {
        super(JPEG.APP14, "Adobe", new byte[]{65, 100, 111, 98, 101, (byte) (i >> 8), (byte) (i & 255), (byte) (i2 >> 8), (byte) (i2 & 255), (byte) (i3 >> 8), (byte) (i3 & 255), (byte) i4});
        this.version = i;
        this.flags0 = i2;
        this.flags1 = i3;
        this.transform = i4;
    }

    @Override // com.twelvemonkeys.imageio.plugins.jpeg.Application
    public String toString() {
        return String.format("AdobeDCT[ver: %d.%02d, flags: %s %s, transform: %d]", Integer.valueOf(this.version / 100), Integer.valueOf(this.version % 100), Integer.toBinaryString(this.flags0), Integer.toBinaryString(this.flags1), Integer.valueOf(this.transform));
    }

    public static AdobeDCT read(DataInput dataInput, int i) throws IOException {
        dataInput.skipBytes(5);
        return new AdobeDCT(dataInput.readUnsignedShort(), dataInput.readUnsignedShort(), dataInput.readUnsignedShort(), dataInput.readUnsignedByte());
    }
}
