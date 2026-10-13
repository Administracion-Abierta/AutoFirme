package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/NameClassNsNameState.class */
public class NameClassNsNameState extends NameClassWithoutChildState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.NameClassState
    protected NameClass makeNameClass() {
        return new NamespaceNameClass(getPropagatedNamespace());
    }
}
