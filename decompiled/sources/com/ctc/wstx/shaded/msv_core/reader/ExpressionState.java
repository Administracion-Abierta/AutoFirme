package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/ExpressionState.class */
public abstract class ExpressionState extends SimpleState {
    protected abstract Expression makeExpression();

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected void endSelf() {
        Expression exp = this.reader.interceptExpression(this, makeExpression());
        if (this.parentState != null) {
            ((ExpressionOwner) this.parentState).onEndChild(exp);
        }
        super.endSelf();
    }
}
