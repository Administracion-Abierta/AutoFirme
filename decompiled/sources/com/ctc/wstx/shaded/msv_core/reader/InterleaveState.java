package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/InterleaveState.class */
public class InterleaveState extends ExpressionWithChildState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression castExpression(Expression exp, Expression child) {
        return exp == null ? child : this.reader.pool.createInterleave(exp, child);
    }
}
