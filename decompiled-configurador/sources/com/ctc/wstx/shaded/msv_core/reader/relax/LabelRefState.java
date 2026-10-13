package com.ctc.wstx.shaded.msv_core.reader.relax;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithoutChildState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/relax/LabelRefState.class */
abstract class LabelRefState extends ExpressionWithoutChildState {
    protected abstract Expression resolve(String str, String str2);

    LabelRefState() {
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionState
    protected Expression makeExpression() {
        String label = this.startTag.getAttribute("label");
        String namespace = this.startTag.getAttribute("namespace");
        RELAXReader reader = (RELAXReader) this.reader;
        if (label == null) {
            reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, this.startTag.localName, "label");
            return Expression.nullSet;
        }
        return resolve(namespace, label);
    }
}
