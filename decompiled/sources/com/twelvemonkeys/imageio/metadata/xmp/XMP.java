package com.twelvemonkeys.imageio.metadata.xmp;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/xmp/XMP.class */
public interface XMP {
    public static final String NS_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String NS_DC = "http://purl.org/dc/elements/1.1/";
    public static final String NS_EXIF = "http://ns.adobe.com/exif/1.0/";
    public static final String NS_PHOTOSHOP = "http://ns.adobe.com/photoshop/1.0/";
    public static final String NS_ST_REF = "http://ns.adobe.com/xap/1.0/sType/ResourceRef#";
    public static final String NS_TIFF = "http://ns.adobe.com/tiff/1.0/";
    public static final String NS_XAP = "http://ns.adobe.com/xap/1.0/";
    public static final String NS_XAP_MM = "http://ns.adobe.com/xap/1.0/mm/";
    public static final String NS_X = "adobe:ns:meta/";
    public static final Map<String, String> DEFAULT_NS_MAPPING = Collections.unmodifiableMap(new XMPNamespaceMapping(true));
    public static final Set<String> ELEMENTS = Collections.unmodifiableSet(new XMPNamespaceMapping(false).keySet());
}
