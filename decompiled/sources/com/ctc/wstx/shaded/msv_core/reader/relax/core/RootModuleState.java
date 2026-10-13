package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.relax.RELAXReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/RootModuleState.class */
class RootModuleState extends SimpleState {
    protected final String expectedNamespace;

    RootModuleState(String expectedNamespace) {
        this.expectedNamespace = expectedNamespace;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        if (tag.namespaceURI.equals(RELAXReader.RELAXCoreNamespace) && tag.localName.equals("module")) {
            return new ModuleState(this.expectedNamespace);
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected void endSelf() {
        RELAXCoreReader reader = (RELAXCoreReader) this.reader;
        reader.wrapUp();
        super.endSelf();
    }
}
