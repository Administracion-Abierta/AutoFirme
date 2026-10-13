package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.relax.HedgeRules;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.relax.HedgeRuleBaseState;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/HedgeRuleState.class */
public class HedgeRuleState extends HedgeRuleBaseState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.relax.HedgeRuleBaseState
    protected void endSelf(Expression contentModel) {
        String label = this.startTag.getAttribute("label");
        if (label == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "hedgeRule", "label");
            return;
        }
        RELAXCoreReader reader = (RELAXCoreReader) this.reader;
        HedgeRules hr = reader.module.hedgeRules.getOrCreate(label);
        reader.setDeclaredLocationOf(hr);
        hr.addHedge(contentModel, reader.pool);
    }
}
