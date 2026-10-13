package com.ctc.wstx.shaded.msv_core.relaxns.reader;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.relax.HedgeRuleBaseState;
import com.ctc.wstx.shaded.msv_core.reader.relax.RELAXReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/relaxns/reader/TopLevelState.class */
public class TopLevelState extends HedgeRuleBaseState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.relax.HedgeRuleBaseState
    protected void endSelf(Expression contentModel) {
        ((RELAXNSReader) this.reader).grammar.topLevel = contentModel;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.relax.HedgeRuleBaseState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        if (tag.namespaceURI.equals(RELAXNSReader.RELAXNamespaceNamespace)) {
            this.reader.reportError(RELAXNSReader.ERR_TOPLEVEL_PARTICLE_MUST_BE_RELAX_CORE);
            return null;
        }
        return super.createChildState(tag);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected boolean isGrammarElement(StartTagInfo tag) {
        if (tag.namespaceURI.equals(RELAXReader.RELAXCoreNamespace) || tag.namespaceURI.equals(RELAXNSReader.RELAXNamespaceNamespace)) {
            return true;
        }
        return false;
    }
}
