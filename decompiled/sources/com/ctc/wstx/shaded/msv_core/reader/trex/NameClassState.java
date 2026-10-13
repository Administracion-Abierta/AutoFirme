package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/NameClassState.class */
public abstract class NameClassState extends SimpleState {
    protected abstract NameClass makeNameClass();

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    public final void endSelf() {
        ((NameClassOwner) this.parentState).onEndChild(makeNameClass());
        super.endSelf();
    }

    protected final String getPropagatedNamespace() {
        return ((TREXBaseReader) this.reader).targetNamespace;
    }
}
