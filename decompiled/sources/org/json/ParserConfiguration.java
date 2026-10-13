package org.json;

/* JADX INFO: loaded from: autofirma.jar:org/json/ParserConfiguration.class */
public class ParserConfiguration {
    public static final int UNDEFINED_MAXIMUM_NESTING_DEPTH = -1;
    public static final int DEFAULT_MAXIMUM_NESTING_DEPTH = 512;
    protected boolean keepStrings;
    protected int maxNestingDepth;

    public ParserConfiguration() {
        this.keepStrings = false;
        this.maxNestingDepth = 512;
    }

    protected ParserConfiguration(boolean keepStrings, int maxNestingDepth) {
        this.keepStrings = keepStrings;
        this.maxNestingDepth = maxNestingDepth;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // 
    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public ParserConfiguration mo2300clone() {
        return new ParserConfiguration(this.keepStrings, this.maxNestingDepth);
    }

    public boolean isKeepStrings() {
        return this.keepStrings;
    }

    public <T extends ParserConfiguration> T withKeepStrings(boolean z) {
        T t = (T) mo2300clone();
        t.keepStrings = z;
        return t;
    }

    public int getMaxNestingDepth() {
        return this.maxNestingDepth;
    }

    public <T extends ParserConfiguration> T withMaxNestingDepth(int i) {
        T t = (T) mo2300clone();
        if (i > -1) {
            t.maxNestingDepth = i;
        } else {
            t.maxNestingDepth = -1;
        }
        return t;
    }
}
