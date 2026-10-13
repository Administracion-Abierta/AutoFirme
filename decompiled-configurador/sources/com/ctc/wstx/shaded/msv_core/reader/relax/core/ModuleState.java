package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXModule;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/ModuleState.class */
public class ModuleState extends ModuleMergeState {
    protected ModuleState(String expectedNamespace) {
        super(expectedNamespace);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.relax.core.ModuleMergeState, com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        getReader().module = new RELAXModule(this.reader.pool, this.targetNamespace);
    }
}
