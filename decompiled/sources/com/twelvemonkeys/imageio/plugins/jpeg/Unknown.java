package com.twelvemonkeys.imageio.plugins.jpeg;

import java.io.DataInput;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/Unknown.class */
final class Unknown extends Segment {
    final byte[] data;

    private Unknown(int i, byte[] bArr) {
        super(i);
        this.data = bArr;
    }

    public String toString() {
        return String.format("Unknown[%04x, length: %d]", Integer.valueOf(this.marker), Integer.valueOf(this.data.length));
    }

    public static Segment read(int i, int i2, DataInput dataInput) throws IOException {
        byte[] bArr = new byte[i2 - 2];
        dataInput.readFully(bArr);
        return new Unknown(i, bArr);
    }
}
