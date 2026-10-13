package org.apache.oro.text;

import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.util.CacheLRU;

/* JADX INFO: loaded from: autofirma.jar:org/apache/oro/text/PatternCacheLRU.class */
public final class PatternCacheLRU extends GenericPatternCache {
    PatternCacheLRU(int capacity, PatternCompiler compiler) {
        super(new CacheLRU(capacity), compiler);
    }

    PatternCacheLRU(PatternCompiler compiler) {
        this(20, compiler);
    }

    PatternCacheLRU(int capacity) {
        this(capacity, new Perl5Compiler());
    }

    public PatternCacheLRU() {
        this(20);
    }
}
