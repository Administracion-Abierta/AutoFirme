package com.ctc.wstx.sax;

import com.aowagie.text.xml.XMLConstants;
import java.util.HashMap;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sax/SAXFeature.class */
public final class SAXFeature {
    public static final String STD_FEATURE_PREFIX = "http://xml.org/sax/features/";
    static final HashMap<String, SAXFeature> sInstances = new HashMap<>();
    static final SAXFeature EXTERNAL_GENERAL_ENTITIES = new SAXFeature("external-general-entities");
    static final SAXFeature EXTERNAL_PARAMETER_ENTITIES = new SAXFeature("external-parameter-entities");
    static final SAXFeature IS_STANDALONE = new SAXFeature("is-standalone");
    static final SAXFeature LEXICAL_HANDLER_PARAMETER_ENTITIES = new SAXFeature("lexical-handler/parameter-entities");
    static final SAXFeature NAMESPACES = new SAXFeature("namespaces");
    static final SAXFeature NAMESPACE_PREFIXES = new SAXFeature("namespace-prefixes");
    static final SAXFeature RESOLVE_DTD_URIS = new SAXFeature("resolve-dtd-uris");
    static final SAXFeature STRING_INTERNING = new SAXFeature("string-interning");
    static final SAXFeature UNICODE_NORMALIZATION_CHECKING = new SAXFeature("unicode-normalization-checking");
    static final SAXFeature USE_ATTRIBUTES2 = new SAXFeature("use-attributes2");
    static final SAXFeature USE_LOCATOR2 = new SAXFeature("use-locator2");
    static final SAXFeature USE_ENTITY_RESOLVER2 = new SAXFeature("use-entity-resolver2");
    static final SAXFeature VALIDATION = new SAXFeature("validation");
    static final SAXFeature XMLNS_URIS = new SAXFeature("xmlns-uris");
    static final SAXFeature XML_1_1 = new SAXFeature("xml-1.1");
    static final SAXFeature JDK_SECURE_PROCESSING = new SAXFeature(XMLConstants.FEATURE_SECURE_PROCESSING);
    private final String mSuffix;

    private SAXFeature(String suffix) {
        this.mSuffix = suffix;
        sInstances.put(suffix, this);
    }

    public static SAXFeature findByUri(String uri) {
        if (uri.startsWith(STD_FEATURE_PREFIX)) {
            return findBySuffix(uri.substring(STD_FEATURE_PREFIX.length()));
        }
        return findBySuffix(uri);
    }

    public static SAXFeature findBySuffix(String suffix) {
        return sInstances.get(suffix);
    }

    public String getSuffix() {
        return this.mSuffix;
    }

    public String toString() {
        return STD_FEATURE_PREFIX + this.mSuffix;
    }
}
