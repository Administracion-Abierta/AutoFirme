package com.twelvemonkeys.imageio.plugins.jpeg;

import com.sun.jna.platform.win32.WinError;
import com.twelvemonkeys.imageio.stream.ByteArrayImageInputStream;
import java.io.IOException;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JFXXThumbnail.class */
final class JFXXThumbnail {
    private JFXXThumbnail() {
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    static ThumbnailReader from(JFXX jfxx, ImageReader imageReader) throws IIOException, IOException {
        if (jfxx != null) {
            if (jfxx.thumbnail != null && jfxx.thumbnail.length > 2) {
                switch (jfxx.extensionCode) {
                    case 16:
                        if ((((jfxx.thumbnail[0] & 255) << 8) | (jfxx.thumbnail[1] & 255)) == 65496) {
                            return new ThumbnailReader.JPEGThumbnailReader(imageReader, new ByteArrayImageInputStream(jfxx.thumbnail), 0L);
                        }
                        break;
                    case 17:
                        int i = jfxx.thumbnail[0] & 255;
                        int i2 = jfxx.thumbnail[1] & 255;
                        if (jfxx.thumbnail.length >= WinError.ERROR_CANTFETCHBACKWARDS + (i * i2)) {
                            return new ThumbnailReader.IndexedThumbnailReader(i, i2, jfxx.thumbnail, 2, jfxx.thumbnail, WinError.ERROR_CANTFETCHBACKWARDS);
                        }
                        break;
                    case 18:
                    default:
                        throw new IIOException(String.format("Unknown JFXX extension code: %d, ignoring thumbnail", Integer.valueOf(jfxx.extensionCode)));
                    case 19:
                        int i3 = jfxx.thumbnail[0] & 255;
                        int i4 = jfxx.thumbnail[1] & 255;
                        if (jfxx.thumbnail.length >= 2 + (i3 * i4 * 3)) {
                            return new ThumbnailReader.UncompressedThumbnailReader(i3, i4, jfxx.thumbnail, 2);
                        }
                        break;
                }
            }
            throw new IIOException("JFXX segment truncated, ignoring thumbnail");
        }
        return null;
    }
}
