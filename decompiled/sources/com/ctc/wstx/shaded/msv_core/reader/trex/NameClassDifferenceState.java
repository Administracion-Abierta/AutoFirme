package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.DifferenceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/NameClassDifferenceState.class */
public class NameClassDifferenceState extends NameClassWithChildState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.NameClassWithChildState
    protected NameClass castNameClass(NameClass halfCasted, NameClass newChild) {
        return halfCasted == null ? newChild : new DifferenceNameClass(halfCasted, newChild);
    }
}
