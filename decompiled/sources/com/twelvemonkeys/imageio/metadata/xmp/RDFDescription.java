package com.twelvemonkeys.imageio.metadata.xmp;

import com.twelvemonkeys.imageio.metadata.AbstractDirectory;
import com.twelvemonkeys.imageio.metadata.Entry;
import java.util.Collection;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/xmp/RDFDescription.class */
final class RDFDescription extends AbstractDirectory {
    private final String namespace;

    public RDFDescription(Collection<? extends Entry> collection) {
        this(null, collection);
    }

    public RDFDescription(String str, Collection<? extends Entry> collection) {
        super(collection);
        this.namespace = str;
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractDirectory
    public String toString() {
        return this.namespace != null ? super.toString().replaceAll("^RDFDescription\\[", String.format("%s[%s|%s, ", getClass().getSimpleName(), XMP.DEFAULT_NS_MAPPING.get(this.namespace), this.namespace)) : super.toString();
    }
}
