package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/StartState.class */
public class StartState extends DefineState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.DefineState
    protected ReferenceExp getReference() {
        RELAXNGReader reader = (RELAXNGReader) this.reader;
        if (this.startTag.containsAttribute("name")) {
            reader.reportError(GrammarReader.ERR_DISALLOWED_ATTRIBUTE, this.startTag.qName, "name");
            return null;
        }
        return reader.getGrammar();
    }
}
