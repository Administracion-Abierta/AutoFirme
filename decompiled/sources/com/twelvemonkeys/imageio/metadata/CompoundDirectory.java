package com.twelvemonkeys.imageio.metadata;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/CompoundDirectory.class */
public interface CompoundDirectory extends Directory {
    Directory getDirectory(int i);

    int directoryCount();
}
