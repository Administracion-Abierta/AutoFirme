package com.twelvemonkeys.imageio.metadata.xmp;

import com.twelvemonkeys.imageio.metadata.AbstractCompoundDirectory;
import com.twelvemonkeys.imageio.metadata.Directory;
import java.util.Collection;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/xmp/XMPDirectory.class */
final class XMPDirectory extends AbstractCompoundDirectory {
    private final String toolkit;

    public XMPDirectory(Collection<? extends Directory> collection, String str) {
        super(collection);
        this.toolkit = str;
    }

    String getWriterToolkit() {
        return this.toolkit;
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractCompoundDirectory, com.twelvemonkeys.imageio.metadata.AbstractDirectory, com.twelvemonkeys.imageio.metadata.Directory
    public boolean isReadOnly() {
        return super.isReadOnly();
    }
}
