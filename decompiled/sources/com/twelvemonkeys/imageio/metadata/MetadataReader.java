package com.twelvemonkeys.imageio.metadata;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/MetadataReader.class */
public abstract class MetadataReader {
    public abstract Directory read(ImageInputStream imageInputStream) throws IOException;
}
