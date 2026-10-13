package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.stream.ByteArrayImageInputStream;
import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/EXIF.class */
final class EXIF extends Application {
    EXIF(byte[] bArr) {
        super(65505, "Exif", bArr);
    }

    @Override // com.twelvemonkeys.imageio.plugins.jpeg.Application
    public String toString() {
        return String.format("APP1/Exif, length: %d", Integer.valueOf(this.data.length));
    }

    ImageInputStream exifData() {
        int length = this.identifier.length() + 2;
        return new ByteArrayImageInputStream(this.data, length, this.data.length - length);
    }

    public static EXIF read(DataInput dataInput, int i) throws IOException {
        if (i < 8) {
            throw new EOFException();
        }
        byte[] bArr = new byte[i - 2];
        dataInput.readFully(bArr);
        return new EXIF(bArr);
    }
}
