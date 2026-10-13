package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/MixedState.class */
public class MixedState extends ExpressionWithChildState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression castExpression(Expression current, Expression child) {
        if (current != null) {
            this.reader.reportError(GrammarReader.ERR_MORE_THAN_ONE_CHILD_EXPRESSION);
        }
        return child;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression annealExpression(Expression exp) {
        return this.reader.pool.createMixed(exp);
    }
}
