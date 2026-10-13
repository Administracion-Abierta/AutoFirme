package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.metadata.jpeg.JPEG;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/Application.class */
class Application extends Segment {
    final String identifier;
    final byte[] data;

    Application(int i, String str, byte[] bArr) {
        super(i);
        this.identifier = str;
        this.data = bArr;
    }

    InputStream data() {
        int length = this.identifier.length() + 1;
        return new ByteArrayInputStream(this.data, length, this.data.length - length);
    }

    public String toString() {
        return "APP" + (this.marker & 15) + "/" + this.identifier + "[length: " + this.data.length + "]";
    }

    public static Application read(int i, String str, DataInput dataInput, int i2) throws IOException {
        switch (i) {
            case JPEG.APP0 /* 65504 */:
                if ("JFIF".equals(str)) {
                    return JFIF.read(dataInput, i2);
                }
            case 65505:
                if ("JFXX".equals(str)) {
                    return JFXX.read(dataInput, i2);
                }
                if ("Exif".equals(str)) {
                    return EXIF.read(dataInput, i2);
                }
            case 65506:
                if ("ICC_PROFILE".equals(str)) {
                    return ICCProfile.read(dataInput, i2);
                }
            case JPEG.APP14 /* 65518 */:
                if ("Adobe".equals(str)) {
                    return AdobeDCT.read(dataInput, i2);
                }
            default:
                byte[] bArr = new byte[Math.max(0, i2 - 2)];
                dataInput.readFully(bArr);
                return new Application(i, str, bArr);
        }
    }
}
