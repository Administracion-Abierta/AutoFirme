package com.ctc.wstx.sax;

import java.util.HashMap;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/sax/SAXProperty.class */
public final class SAXProperty {
    public static final String STD_PROPERTY_PREFIX = "http://xml.org/sax/properties/";
    static final HashMap<String, SAXProperty> sInstances = new HashMap<>();
    public static final SAXProperty DECLARATION_HANDLER = new SAXProperty("declaration-handler");
    public static final SAXProperty DOCUMENT_XML_VERSION = new SAXProperty("document-xml-version");
    public static final SAXProperty DOM_NODE = new SAXProperty("dom-node");
    public static final SAXProperty LEXICAL_HANDLER = new SAXProperty("lexical-handler");
    static final SAXProperty XML_STRING = new SAXProperty("xml-string");
    private final String mSuffix;

    private SAXProperty(String suffix) {
        this.mSuffix = suffix;
        sInstances.put(suffix, this);
    }

    public static SAXProperty findByUri(String uri) {
        if (uri.startsWith(STD_PROPERTY_PREFIX)) {
            return findBySuffix(uri.substring(STD_PROPERTY_PREFIX.length()));
        }
        return null;
    }

    public static SAXProperty findBySuffix(String suffix) {
        return sInstances.get(suffix);
    }

    public String getSuffix() {
        return this.mSuffix;
    }

    public String toString() {
        return STD_PROPERTY_PREFIX + this.mSuffix;
    }
}
