package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithoutChildState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/AttPoolRefState.class */
public class AttPoolRefState extends ExpressionWithoutChildState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionState
    protected Expression makeExpression() {
        String role = this.startTag.getAttribute("role");
        if (role == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "ref", "role");
            return Expression.epsilon;
        }
        String namespace = this.startTag.getAttribute("namespace");
        RELAXCoreReader reader = (RELAXCoreReader) this.reader;
        return reader.resolveAttPoolRef(namespace, role);
    }
}
