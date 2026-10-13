package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/GrammarState.class */
public class GrammarState extends com.ctc.wstx.shaded.msv_core.reader.trex.GrammarState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.GrammarState, com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        RELAXNGReader reader = (RELAXNGReader) this.reader;
        if (reader.currentNamedPattern != null) {
            if (reader.directRefernce) {
                reader.currentNamedPattern.directRefs.add(this.newGrammar);
            } else {
                reader.currentNamedPattern.indirectRefs.add(this.newGrammar);
            }
        }
    }
}
