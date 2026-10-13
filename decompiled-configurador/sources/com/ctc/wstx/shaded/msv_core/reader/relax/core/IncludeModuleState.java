package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.reader.AbortException;
import com.ctc.wstx.shaded.msv_core.reader.ChildlessState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/IncludeModuleState.class */
public class IncludeModuleState extends ChildlessState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        String href = this.startTag.getAttribute("moduleLocation");
        if (href == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "include", "moduleLocation");
        } else {
            try {
                this.reader.switchSource(this, href, new RootModuleMergeState());
            } catch (AbortException e) {
            }
        }
    }
}
