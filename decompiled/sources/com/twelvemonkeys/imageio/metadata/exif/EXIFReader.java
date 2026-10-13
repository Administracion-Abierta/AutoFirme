package com.twelvemonkeys.imageio.metadata.exif;

import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.MetadataReader;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFReader;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/exif/EXIFReader.class */
@Deprecated
public final class EXIFReader extends MetadataReader {
    private final TIFFReader delegate = new TIFFReader();

    @Override // com.twelvemonkeys.imageio.metadata.MetadataReader
    public Directory read(ImageInputStream imageInputStream) throws IOException {
        return this.delegate.read(imageInputStream);
    }
}
