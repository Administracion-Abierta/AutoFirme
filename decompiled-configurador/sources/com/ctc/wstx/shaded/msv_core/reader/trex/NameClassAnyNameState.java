package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.NameClass;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/NameClassAnyNameState.class */
public class NameClassAnyNameState extends NameClassWithoutChildState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.NameClassState
    protected NameClass makeNameClass() {
        return NameClass.ALL;
    }
}
