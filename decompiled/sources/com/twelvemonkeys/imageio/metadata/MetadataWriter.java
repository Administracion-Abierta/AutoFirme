package com.twelvemonkeys.imageio.metadata;

import java.io.IOException;
import javax.imageio.stream.ImageOutputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/MetadataWriter.class */
public abstract class MetadataWriter {
    public abstract boolean write(Directory directory, ImageOutputStream imageOutputStream) throws IOException;
}
