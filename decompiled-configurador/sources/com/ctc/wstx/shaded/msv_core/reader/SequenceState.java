package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/SequenceState.class */
public class SequenceState extends ExpressionWithChildState {
    protected boolean allowEmptySequence;

    public SequenceState() {
        this(false);
    }

    public SequenceState(boolean allowEmptySequence) {
        this.allowEmptySequence = allowEmptySequence;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression initialExpression() {
        if (this.allowEmptySequence) {
            return Expression.epsilon;
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression castExpression(Expression exp, Expression child) {
        return exp == null ? child : this.reader.pool.createSequence(exp, child);
    }
}
