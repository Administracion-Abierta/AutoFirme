package com.twelvemonkeys.imageio;

import java.util.Arrays;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/AbstractMetadata.class */
public abstract class AbstractMetadata extends IIOMetadata implements Cloneable {
    protected AbstractMetadata(boolean z, String str, String str2, String[] strArr, String[] strArr2) {
        super(z, str, str2, strArr, strArr2);
    }

    protected AbstractMetadata() {
        super(true, (String) null, (String) null, (String[]) null, (String[]) null);
    }

    public boolean isReadOnly() {
        return true;
    }

    public Node getAsTree(String str) {
        validateFormatName(str);
        if (str.equals(this.nativeMetadataFormatName)) {
            return getNativeTree();
        }
        if (str.equals("javax_imageio_1.0")) {
            return getStandardTree();
        }
        return null;
    }

    protected Node getNativeTree() {
        throw new UnsupportedOperationException("getNativeTree");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.metadata.IIOInvalidTreeException */
    public void mergeTree(String str, Node node) throws IIOInvalidTreeException {
        assertMutable();
        validateFormatName(str);
        if (!node.getNodeName().equals(str)) {
            throw new IIOInvalidTreeException("Root must be " + str, node);
        }
    }

    public void reset() {
        assertMutable();
    }

    protected final void assertMutable() {
        if (isReadOnly()) {
            throw new IllegalStateException("Metadata is read-only");
        }
    }

    protected final void validateFormatName(String str) {
        String[] metadataFormatNames = getMetadataFormatNames();
        if (metadataFormatNames != null) {
            for (String str2 : metadataFormatNames) {
                if (str2.equals(str)) {
                    return;
                }
            }
        }
        throw new IllegalArgumentException(String.format("Unsupported format name: \"%s\". Expected one of %s", str, Arrays.toString(metadataFormatNames)));
    }

    protected static String toListString(short[] sArr) {
        String string = Arrays.toString(sArr);
        return string.substring(1, string.length() - 1);
    }
}
