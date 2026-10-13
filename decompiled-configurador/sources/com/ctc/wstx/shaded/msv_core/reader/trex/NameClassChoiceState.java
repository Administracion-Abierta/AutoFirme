package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.ChoiceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/NameClassChoiceState.class */
public class NameClassChoiceState extends NameClassWithChildState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.NameClassWithChildState
    protected NameClass castNameClass(NameClass halfCasted, NameClass newChild) {
        return halfCasted == null ? newChild : new ChoiceNameClass(halfCasted, newChild);
    }
}
