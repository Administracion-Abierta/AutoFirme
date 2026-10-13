package org.ini4j;

import java.io.Serializable;
import java.nio.charset.Charset;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/Config.class */
public class Config implements Cloneable, Serializable {
    public static final String KEY_PREFIX = "org.ini4j.config.";
    public static final String PROP_EMPTY_OPTION = "emptyOption";
    public static final String PROP_EMPTY_SECTION = "emptySection";
    public static final String PROP_GLOBAL_SECTION = "globalSection";
    public static final String PROP_GLOBAL_SECTION_NAME = "globalSectionName";
    public static final String PROP_INCLUDE = "include";
    public static final String PROP_LOWER_CASE_OPTION = "lowerCaseOption";
    public static final String PROP_LOWER_CASE_SECTION = "lowerCaseSection";
    public static final String PROP_MULTI_OPTION = "multiOption";
    public static final String PROP_MULTI_SECTION = "multiSection";
    public static final String PROP_STRICT_OPERATOR = "strictOperator";
    public static final String PROP_UNNAMED_SECTION = "unnamedSection";
    public static final String PROP_ESCAPE = "escape";
    public static final String PROP_ESCAPE_NEWLINE = "escapeNewline";
    public static final String PROP_ESCAPE_KEY_ONLY = "escapeKey";
    public static final String PROP_PATH_SEPARATOR = "pathSeparator";
    public static final String PROP_TREE = "tree";
    public static final String PROP_PROPERTY_FIRST_UPPER = "propertyFirstUpper";
    public static final String PROP_FILE_ENCODING = "fileEncoding";
    public static final String PROP_LINE_SEPARATOR = "lineSeparator";
    public static final String PROP_COMMENT = "comment";
    public static final String PROP_HEADER_COMMENT = "headerComment";
    public static final boolean DEFAULT_EMPTY_OPTION = false;
    public static final boolean DEFAULT_EMPTY_SECTION = false;
    public static final boolean DEFAULT_GLOBAL_SECTION = false;
    public static final String DEFAULT_GLOBAL_SECTION_NAME = "?";
    public static final boolean DEFAULT_INCLUDE = false;
    public static final boolean DEFAULT_LOWER_CASE_OPTION = false;
    public static final boolean DEFAULT_LOWER_CASE_SECTION = false;
    public static final boolean DEFAULT_MULTI_OPTION = true;
    public static final boolean DEFAULT_MULTI_SECTION = false;
    public static final boolean DEFAULT_STRICT_OPERATOR = false;
    public static final boolean DEFAULT_UNNAMED_SECTION = false;
    public static final boolean DEFAULT_ESCAPE = true;
    public static final boolean DEFAULT_ESCAPE_NEWLINE = true;
    public static final boolean DEFAULT_ESCAPE_KEY_ONLY = false;
    public static final boolean DEFAULT_TREE = true;
    public static final boolean DEFAULT_PROPERTY_FIRST_UPPER = false;
    public static final boolean DEFAULT_COMMENT = true;
    public static final boolean DEFAULT_HEADER_COMMENT = true;
    public static final char DEFAULT_PATH_SEPARATOR = '/';
    public static final String DEFAULT_LINE_SEPARATOR = getSystemProperty("line.separator", "\n");
    public static final Charset DEFAULT_FILE_ENCODING = Charset.forName("UTF-8");
    private static final Config GLOBAL = new Config();
    private static final long serialVersionUID = 2865793267410367814L;
    private boolean _comment;
    private boolean _emptyOption;
    private boolean _emptySection;
    private boolean _escape;
    private boolean _escapeKeyOnly;
    private boolean _escapeNewline;
    private Charset _fileEncoding;
    private boolean _globalSection;
    private String _globalSectionName;
    private boolean _headerComment;
    private boolean _include;
    private String _lineSeparator;
    private boolean _lowerCaseOption;
    private boolean _lowerCaseSection;
    private boolean _multiOption;
    private boolean _multiSection;
    private char _pathSeparator;
    private boolean _propertyFirstUpper;
    private boolean _strictOperator;
    private boolean _tree;
    private boolean _unnamedSection;

    public Config() {
        reset();
    }

    public static String getEnvironment(String name) {
        return getEnvironment(name, null);
    }

    public static String getEnvironment(String name, String defaultValue) {
        String value;
        try {
            value = System.getenv(name);
        } catch (SecurityException e) {
            value = null;
        }
        return value == null ? defaultValue : value;
    }

    public static Config getGlobal() {
        return GLOBAL;
    }

    public static String getSystemProperty(String name) {
        return getSystemProperty(name, null);
    }

    public static String getSystemProperty(String name, String defaultValue) {
        String value;
        try {
            value = System.getProperty(name);
        } catch (SecurityException e) {
            value = null;
        }
        return value == null ? defaultValue : value;
    }

    public void setComment(boolean value) {
        this._comment = value;
    }

    public boolean isEscape() {
        return this._escape;
    }

    public boolean isEscapeNewline() {
        return this._escapeNewline;
    }

    public boolean isInclude() {
        return this._include;
    }

    public boolean isTree() {
        return this._tree;
    }

    public void setEmptyOption(boolean value) {
        this._emptyOption = value;
    }

    public void setEmptySection(boolean value) {
        this._emptySection = value;
    }

    public void setEscape(boolean value) {
        this._escape = value;
    }

    public void setEscapeKeyOnly(boolean value) {
        this._escapeKeyOnly = value;
    }

    public void setEscapeNewline(boolean value) {
        this._escapeNewline = value;
    }

    public Charset getFileEncoding() {
        return this._fileEncoding;
    }

    public void setFileEncoding(Charset value) {
        this._fileEncoding = value;
    }

    public void setGlobalSection(boolean value) {
        this._globalSection = value;
    }

    public String getGlobalSectionName() {
        return this._globalSectionName;
    }

    public void setGlobalSectionName(String value) {
        this._globalSectionName = value;
    }

    public void setHeaderComment(boolean value) {
        this._headerComment = value;
    }

    public void setInclude(boolean value) {
        this._include = value;
    }

    public String getLineSeparator() {
        return this._lineSeparator;
    }

    public void setLineSeparator(String value) {
        this._lineSeparator = value;
    }

    public void setLowerCaseOption(boolean value) {
        this._lowerCaseOption = value;
    }

    public void setLowerCaseSection(boolean value) {
        this._lowerCaseSection = value;
    }

    public void setMultiOption(boolean value) {
        this._multiOption = value;
    }

    public void setMultiSection(boolean value) {
        this._multiSection = value;
    }

    public boolean isEmptyOption() {
        return this._emptyOption;
    }

    public boolean isEmptySection() {
        return this._emptySection;
    }

    public boolean isGlobalSection() {
        return this._globalSection;
    }

    public boolean isLowerCaseOption() {
        return this._lowerCaseOption;
    }

    public boolean isLowerCaseSection() {
        return this._lowerCaseSection;
    }

    public boolean isMultiOption() {
        return this._multiOption;
    }

    public boolean isMultiSection() {
        return this._multiSection;
    }

    public boolean isUnnamedSection() {
        return this._unnamedSection;
    }

    public char getPathSeparator() {
        return this._pathSeparator;
    }

    public void setPathSeparator(char value) {
        this._pathSeparator = value;
    }

    public void setPropertyFirstUpper(boolean value) {
        this._propertyFirstUpper = value;
    }

    public boolean isPropertyFirstUpper() {
        return this._propertyFirstUpper;
    }

    public boolean isStrictOperator() {
        return this._strictOperator;
    }

    public void setStrictOperator(boolean value) {
        this._strictOperator = value;
    }

    public boolean isComment() {
        return this._comment;
    }

    public boolean isHeaderComment() {
        return this._headerComment;
    }

    public void setTree(boolean value) {
        this._tree = value;
    }

    public void setUnnamedSection(boolean value) {
        this._unnamedSection = value;
    }

    public boolean isEscapeKeyOnly() {
        return this._escapeKeyOnly;
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public Config m2267clone() {
        try {
            return (Config) super.clone();
        } catch (CloneNotSupportedException x) {
            throw new AssertionError(x);
        }
    }

    public final void reset() {
        this._emptyOption = getBoolean(PROP_EMPTY_OPTION, false);
        this._emptySection = getBoolean(PROP_EMPTY_SECTION, false);
        this._globalSection = getBoolean(PROP_GLOBAL_SECTION, false);
        this._globalSectionName = getString(PROP_GLOBAL_SECTION_NAME, DEFAULT_GLOBAL_SECTION_NAME);
        this._include = getBoolean(PROP_INCLUDE, false);
        this._lowerCaseOption = getBoolean(PROP_LOWER_CASE_OPTION, false);
        this._lowerCaseSection = getBoolean(PROP_LOWER_CASE_SECTION, false);
        this._multiOption = getBoolean(PROP_MULTI_OPTION, true);
        this._multiSection = getBoolean(PROP_MULTI_SECTION, false);
        this._strictOperator = getBoolean(PROP_STRICT_OPERATOR, false);
        this._unnamedSection = getBoolean(PROP_UNNAMED_SECTION, false);
        this._escape = getBoolean(PROP_ESCAPE, true);
        this._escapeKeyOnly = getBoolean(PROP_ESCAPE_KEY_ONLY, false);
        this._escapeNewline = getBoolean(PROP_ESCAPE_NEWLINE, true);
        this._pathSeparator = getChar(PROP_PATH_SEPARATOR, '/');
        this._tree = getBoolean("tree", true);
        this._propertyFirstUpper = getBoolean(PROP_PROPERTY_FIRST_UPPER, false);
        this._lineSeparator = getString(PROP_LINE_SEPARATOR, DEFAULT_LINE_SEPARATOR);
        this._fileEncoding = getCharset(PROP_FILE_ENCODING, DEFAULT_FILE_ENCODING);
        this._comment = getBoolean(PROP_COMMENT, true);
        this._headerComment = getBoolean(PROP_HEADER_COMMENT, true);
    }

    private boolean getBoolean(String name, boolean defaultValue) {
        String value = getSystemProperty(KEY_PREFIX + name);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    private char getChar(String name, char defaultValue) {
        String value = getSystemProperty(KEY_PREFIX + name);
        return value == null ? defaultValue : value.charAt(0);
    }

    private Charset getCharset(String name, Charset defaultValue) {
        String value = getSystemProperty(KEY_PREFIX + name);
        return value == null ? defaultValue : Charset.forName(value);
    }

    private String getString(String name, String defaultValue) {
        return getSystemProperty(KEY_PREFIX + name, defaultValue);
    }
}
