package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.relax.HedgeRules;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.relax.HedgeRuleBaseState;
import org.bouncycastle.jcajce.util.AnnotatedPrivateKey;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/HedgeRuleState.class */
public class HedgeRuleState extends HedgeRuleBaseState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.relax.HedgeRuleBaseState
    protected void endSelf(Expression contentModel) {
        String label = this.startTag.getAttribute(AnnotatedPrivateKey.LABEL);
        if (label == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "hedgeRule", AnnotatedPrivateKey.LABEL);
            return;
        }
        RELAXCoreReader reader = (RELAXCoreReader) this.reader;
        HedgeRules hr = reader.module.hedgeRules.getOrCreate(label);
        reader.setDeclaredLocationOf(hr);
        hr.addHedge(contentModel, reader.pool);
    }
}
