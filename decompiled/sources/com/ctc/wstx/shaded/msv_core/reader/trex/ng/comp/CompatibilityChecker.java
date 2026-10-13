package com.ctc.wstx.shaded.msv_core.reader.trex.ng.comp;

import com.ctc.wstx.shaded.msv_core.grammar.relaxng.RELAXNGGrammar;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/comp/CompatibilityChecker.class */
abstract class CompatibilityChecker {
    protected final RELAXNGCompReader reader;
    protected final RELAXNGGrammar grammar;

    protected abstract void setCompatibility(boolean z);

    protected CompatibilityChecker(RELAXNGCompReader _reader) {
        this.reader = _reader;
        this.grammar = (RELAXNGGrammar) _reader.getGrammar();
    }

    protected void reportCompError(Locator[] locs, String propertyName) {
        reportCompError(locs, propertyName, null);
    }

    protected void reportCompError(Locator[] locs, String propertyName, Object[] args) {
        setCompatibility(false);
        this.reader.reportWarning(propertyName, args, locs);
    }
}
