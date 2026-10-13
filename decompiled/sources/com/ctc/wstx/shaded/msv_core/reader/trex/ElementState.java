package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.trex.ElementPattern;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ElementState.class */
public class ElementState extends NameClassAndExpressionState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression annealExpression(Expression contentModel) {
        ElementPattern e = new ElementPattern(this.nameClass, contentModel);
        this.reader.setDeclaredLocationOf(e);
        return e;
    }
}
