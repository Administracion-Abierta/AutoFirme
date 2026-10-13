package com.ctc.wstx.api;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/api/WstxInputProperties.class */
public final class WstxInputProperties {
    public static final String UNKNOWN_ATTR_TYPE = "CDATA";
    public static final String P_NORMALIZE_LFS = "com.ctc.wstx.normalizeLFs";
    public static final String P_VALIDATE_TEXT_CHARS = "com.ctc.wstx.validateTextChars";
    public static final String P_ALLOW_XML11_ESCAPED_CHARS_IN_XML10 = "com.ctc.wstx.allowXml11EscapedCharsInXml10";
    public static final String P_CACHE_DTDS = "com.ctc.wstx.cacheDTDs";
    public static final String P_CACHE_DTDS_BY_PUBLIC_ID = "com.ctc.wstx.cacheDTDsByPublicId";

    @Deprecated
    public static final String P_LAZY_PARSING = "com.ctc.wstx.lazyParsing";
    public static final String P_RETURN_NULL_FOR_DEFAULT_NAMESPACE = "com.ctc.wstx.returnNullForDefaultNamespace";

    @Deprecated
    public static final String P_SUPPORT_DTDPP = "com.ctc.wstx.supportDTDPP";
    public static final String P_TREAT_CHAR_REFS_AS_ENTS = "com.ctc.wstx.treatCharRefsAsEnts";
    public static final String P_INPUT_BUFFER_LENGTH = "com.ctc.wstx.inputBufferLength";
    public static final String P_MIN_TEXT_SEGMENT = "com.ctc.wstx.minTextSegment";
    public static final String P_MAX_ATTRIBUTES_PER_ELEMENT = "com.ctc.wstx.maxAttributesPerElement";
    public static final String P_MAX_ATTRIBUTE_SIZE = "com.ctc.wstx.maxAttributeSize";
    public static final String P_MAX_CHILDREN_PER_ELEMENT = "com.ctc.wstx.maxChildrenPerElement";
    public static final String P_MAX_ELEMENT_COUNT = "com.ctc.wstx.maxElementCount";
    public static final String P_MAX_ELEMENT_DEPTH = "com.ctc.wstx.maxElementDepth";
    public static final String P_MAX_CHARACTERS = "com.ctc.wstx.maxCharacters";
    public static final String P_MAX_TEXT_LENGTH = "com.ctc.wstx.maxTextLength";
    public static final String P_MAX_ENTITY_COUNT = "com.ctc.wstx.maxEntityCount";
    public static final String P_MAX_ENTITY_DEPTH = "com.ctc.wstx.maxEntityDepth";
    public static final String P_MAX_DTD_DEPTH = "com.ctc.wstx.maxDtdDepth";
    public static final String P_CUSTOM_INTERNAL_ENTITIES = "com.ctc.wstx.customInternalEntities";
    public static final String P_DTD_RESOLVER = "com.ctc.wstx.dtdResolver";
    public static final String P_ENTITY_RESOLVER = "com.ctc.wstx.entityResolver";
    public static final String P_UNDECLARED_ENTITY_RESOLVER = "com.ctc.wstx.undeclaredEntityResolver";
    public static final String P_BASE_URL = "com.ctc.wstx.baseURL";
    public static final String P_INPUT_PARSING_MODE = "com.ctc.wstx.fragmentMode";
    public static final ParsingMode PARSING_MODE_DOCUMENT = new ParsingMode();
    public static final ParsingMode PARSING_MODE_FRAGMENT = new ParsingMode();
    public static final ParsingMode PARSING_MODE_DOCUMENTS = new ParsingMode();

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/api/WstxInputProperties$ParsingMode.class */
    public static final class ParsingMode {
        ParsingMode() {
        }
    }
}
