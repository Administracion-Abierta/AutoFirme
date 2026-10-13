package com.twelvemonkeys.imageio.plugins.jpeg;

import java.io.IOException;
import javax.imageio.IIOException;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JFIFThumbnail.class */
final class JFIFThumbnail {
    private JFIFThumbnail() {
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    static ThumbnailReader from(JFIF jfif) throws IIOException, IOException {
        if (jfif != null && jfif.xThumbnail > 0 && jfif.yThumbnail > 0) {
            if (jfif.thumbnail == null || jfif.thumbnail.length < jfif.xThumbnail * jfif.yThumbnail) {
                throw new IIOException("Truncated JFIF thumbnail");
            }
            return new ThumbnailReader.UncompressedThumbnailReader(jfif.xThumbnail, jfif.yThumbnail, jfif.thumbnail);
        }
        return null;
    }
}
