package org.json;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: autofirma.jar:org/json/XMLParserConfiguration.class */
public class XMLParserConfiguration extends ParserConfiguration {
    public static final XMLParserConfiguration ORIGINAL = new XMLParserConfiguration();
    public static final XMLParserConfiguration KEEP_STRINGS = new XMLParserConfiguration().withKeepStrings(true);
    private String cDataTagName;
    private boolean convertNilAttributeToNull;
    private boolean closeEmptyTag;
    private Map<String, XMLXsiTypeConverter<?>> xsiTypeMap;
    private Set<String> forceList;
    private boolean shouldTrimWhiteSpace;

    public XMLParserConfiguration() {
        this.cDataTagName = "content";
        this.convertNilAttributeToNull = false;
        this.xsiTypeMap = Collections.emptyMap();
        this.forceList = Collections.emptySet();
        this.shouldTrimWhiteSpace = true;
    }

    @Deprecated
    public XMLParserConfiguration(boolean keepStrings) {
        this(keepStrings, "content", false);
    }

    @Deprecated
    public XMLParserConfiguration(String cDataTagName) {
        this(false, cDataTagName, false);
    }

    @Deprecated
    public XMLParserConfiguration(boolean keepStrings, String cDataTagName) {
        super(keepStrings, 512);
        this.cDataTagName = cDataTagName;
        this.convertNilAttributeToNull = false;
    }

    @Deprecated
    public XMLParserConfiguration(boolean keepStrings, String cDataTagName, boolean convertNilAttributeToNull) {
        super(keepStrings, 512);
        this.cDataTagName = cDataTagName;
        this.convertNilAttributeToNull = convertNilAttributeToNull;
    }

    private XMLParserConfiguration(boolean keepStrings, String cDataTagName, boolean convertNilAttributeToNull, Map<String, XMLXsiTypeConverter<?>> xsiTypeMap, Set<String> forceList, int maxNestingDepth, boolean closeEmptyTag) {
        super(keepStrings, maxNestingDepth);
        this.cDataTagName = cDataTagName;
        this.convertNilAttributeToNull = convertNilAttributeToNull;
        this.xsiTypeMap = Collections.unmodifiableMap(xsiTypeMap);
        this.forceList = Collections.unmodifiableSet(forceList);
        this.closeEmptyTag = closeEmptyTag;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.json.ParserConfiguration
    /* JADX INFO: renamed from: clone */
    public XMLParserConfiguration mo2300clone() {
        XMLParserConfiguration config = new XMLParserConfiguration(this.keepStrings, this.cDataTagName, this.convertNilAttributeToNull, this.xsiTypeMap, this.forceList, this.maxNestingDepth, this.closeEmptyTag);
        config.shouldTrimWhiteSpace = this.shouldTrimWhiteSpace;
        return config;
    }

    @Override // org.json.ParserConfiguration
    public XMLParserConfiguration withKeepStrings(boolean newVal) {
        return (XMLParserConfiguration) super.withKeepStrings(newVal);
    }

    public String getcDataTagName() {
        return this.cDataTagName;
    }

    public XMLParserConfiguration withcDataTagName(String newVal) {
        XMLParserConfiguration newConfig = mo2300clone();
        newConfig.cDataTagName = newVal;
        return newConfig;
    }

    public boolean isConvertNilAttributeToNull() {
        return this.convertNilAttributeToNull;
    }

    public XMLParserConfiguration withConvertNilAttributeToNull(boolean newVal) {
        XMLParserConfiguration newConfig = mo2300clone();
        newConfig.convertNilAttributeToNull = newVal;
        return newConfig;
    }

    public Map<String, XMLXsiTypeConverter<?>> getXsiTypeMap() {
        return this.xsiTypeMap;
    }

    public XMLParserConfiguration withXsiTypeMap(Map<String, XMLXsiTypeConverter<?>> xsiTypeMap) {
        XMLParserConfiguration newConfig = mo2300clone();
        Map<String, XMLXsiTypeConverter<?>> cloneXsiTypeMap = new HashMap<>(xsiTypeMap);
        newConfig.xsiTypeMap = Collections.unmodifiableMap(cloneXsiTypeMap);
        return newConfig;
    }

    public Set<String> getForceList() {
        return this.forceList;
    }

    public XMLParserConfiguration withForceList(Set<String> forceList) {
        XMLParserConfiguration newConfig = mo2300clone();
        Set<String> cloneForceList = new HashSet<>(forceList);
        newConfig.forceList = Collections.unmodifiableSet(cloneForceList);
        return newConfig;
    }

    @Override // org.json.ParserConfiguration
    public XMLParserConfiguration withMaxNestingDepth(int maxNestingDepth) {
        return (XMLParserConfiguration) super.withMaxNestingDepth(maxNestingDepth);
    }

    public XMLParserConfiguration withCloseEmptyTag(boolean closeEmptyTag) {
        XMLParserConfiguration clonedConfiguration = mo2300clone();
        clonedConfiguration.closeEmptyTag = closeEmptyTag;
        return clonedConfiguration;
    }

    public XMLParserConfiguration withShouldTrimWhitespace(boolean shouldTrimWhiteSpace) {
        XMLParserConfiguration clonedConfiguration = mo2300clone();
        clonedConfiguration.shouldTrimWhiteSpace = shouldTrimWhiteSpace;
        return clonedConfiguration;
    }

    public boolean isCloseEmptyTag() {
        return this.closeEmptyTag;
    }

    public boolean shouldTrimWhiteSpace() {
        return this.shouldTrimWhiteSpace;
    }
}
