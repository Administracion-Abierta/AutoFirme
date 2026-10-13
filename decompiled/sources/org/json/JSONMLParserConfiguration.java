package org.json;

/* JADX INFO: loaded from: autofirma.jar:org/json/JSONMLParserConfiguration.class */
public class JSONMLParserConfiguration extends ParserConfiguration {
    public static final int DEFAULT_MAXIMUM_NESTING_DEPTH = 512;
    public static final JSONMLParserConfiguration ORIGINAL = new JSONMLParserConfiguration();
    public static final JSONMLParserConfiguration KEEP_STRINGS = new JSONMLParserConfiguration().withKeepStrings(true);

    public JSONMLParserConfiguration() {
        this.maxNestingDepth = 512;
    }

    protected JSONMLParserConfiguration(boolean keepStrings, int maxNestingDepth) {
        super(keepStrings, maxNestingDepth);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.json.ParserConfiguration
    /* JADX INFO: renamed from: clone */
    public JSONMLParserConfiguration mo2300clone() {
        return new JSONMLParserConfiguration(this.keepStrings, this.maxNestingDepth);
    }

    @Override // org.json.ParserConfiguration
    public JSONMLParserConfiguration withKeepStrings(boolean newVal) {
        return (JSONMLParserConfiguration) super.withKeepStrings(newVal);
    }

    @Override // org.json.ParserConfiguration
    public JSONMLParserConfiguration withMaxNestingDepth(int maxNestingDepth) {
        return (JSONMLParserConfiguration) super.withMaxNestingDepth(maxNestingDepth);
    }
}
