package org.json;

/* JADX INFO: loaded from: autofirma.jar:org/json/JSONParserConfiguration.class */
public class JSONParserConfiguration extends ParserConfiguration {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.json.ParserConfiguration
    /* JADX INFO: renamed from: clone */
    public JSONParserConfiguration mo2300clone() {
        return new JSONParserConfiguration();
    }

    @Override // org.json.ParserConfiguration
    public JSONParserConfiguration withMaxNestingDepth(int maxNestingDepth) {
        return (JSONParserConfiguration) super.withMaxNestingDepth(maxNestingDepth);
    }
}
