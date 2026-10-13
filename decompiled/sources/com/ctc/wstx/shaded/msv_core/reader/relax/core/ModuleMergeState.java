package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/ModuleMergeState.class */
public class ModuleMergeState extends DivInModuleState {
    protected final String expectedTargetNamespace;
    protected String targetNamespace;

    protected ModuleMergeState(String expectedTargetNamespace) {
        this.expectedTargetNamespace = expectedTargetNamespace;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        String coreVersion = this.startTag.getAttribute("relaxCoreVersion");
        if (coreVersion == null) {
            this.reader.reportWarning(GrammarReader.ERR_MISSING_ATTRIBUTE, "module", "relaxCoreVersion");
        } else if (!"1.0".equals(coreVersion)) {
            this.reader.reportWarning(RELAXCoreReader.WRN_ILLEGAL_RELAXCORE_VERSION, coreVersion);
        }
        this.targetNamespace = this.startTag.getAttribute("targetNamespace");
        if (this.targetNamespace != null) {
            if (this.expectedTargetNamespace != null && !this.expectedTargetNamespace.equals(this.targetNamespace)) {
                this.reader.reportError(RELAXCoreReader.ERR_INCONSISTENT_TARGET_NAMESPACE, this.targetNamespace, this.expectedTargetNamespace);
                this.targetNamespace = this.expectedTargetNamespace;
                return;
            }
            return;
        }
        if (this.expectedTargetNamespace == null) {
            this.reader.reportError(RELAXCoreReader.ERR_MISSING_TARGET_NAMESPACE);
            this.targetNamespace = "";
        } else {
            this.targetNamespace = this.expectedTargetNamespace;
        }
    }
}
