package com.twelvemonkeys.imageio.metadata.xmp;

import java.util.HashMap;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/xmp/XMPNamespaceMapping.class */
final class XMPNamespaceMapping extends HashMap<String, String> {
    public XMPNamespaceMapping(boolean z) {
        if (z) {
            put(XMP.NS_RDF, "rdf");
            put(XMP.NS_X, "x");
        }
        put(XMP.NS_DC, "dc");
        put(XMP.NS_EXIF, "exif");
        put(XMP.NS_PHOTOSHOP, "photoshop");
        put(XMP.NS_ST_REF, "stRef");
        put(XMP.NS_TIFF, "tiff");
        put(XMP.NS_XAP, "xap");
        put(XMP.NS_XAP_MM, "xapMM");
    }
}
