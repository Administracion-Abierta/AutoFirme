package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.relax.RELAXReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/RootModuleMergeState.class */
class RootModuleMergeState extends SimpleState {
    RootModuleMergeState() {
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        if (tag.namespaceURI.equals(RELAXReader.RELAXCoreNamespace) && tag.localName.equals("module")) {
            return new ModuleMergeState(((RELAXCoreReader) this.reader).module.targetNamespace);
        }
        return null;
    }
}
