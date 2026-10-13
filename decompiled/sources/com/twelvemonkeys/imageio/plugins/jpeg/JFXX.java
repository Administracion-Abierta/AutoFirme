package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.metadata.jpeg.JPEG;
import java.io.DataInput;
import java.io.IOException;
import java.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JFXX.class */
final class JFXX extends Application {
    public static final int JPEG = 16;
    public static final int INDEXED = 17;
    public static final int RGB = 19;
    final int extensionCode;
    final byte[] thumbnail;

    JFXX(int i, byte[] bArr) {
        super(JPEG.APP0, "JFXX", new byte[1 + (bArr != null ? bArr.length : 0)]);
        this.extensionCode = i;
        this.thumbnail = bArr;
    }

    @Override // com.twelvemonkeys.imageio.plugins.jpeg.Application
    public String toString() {
        return String.format("APP0/JFXX extension (%s thumb size: %d)", extensionAsString(), Integer.valueOf(this.thumbnail.length));
    }

    private String extensionAsString() {
        switch (this.extensionCode) {
            case 16:
                return "JPEG";
            case 17:
                return "Indexed";
            case 18:
            default:
                return String.valueOf(this.extensionCode);
            case 19:
                return "RGB";
        }
    }

    public static JFXX read(DataInput dataInput, int i) throws IOException {
        dataInput.readFully(new byte[5]);
        byte[] bArr = new byte[(i - 2) - 5];
        dataInput.readFully(bArr);
        return new JFXX(bArr[0] & 255, bArr.length - 1 > 0 ? Arrays.copyOfRange(bArr, 1, bArr.length - 1) : null);
    }
}
